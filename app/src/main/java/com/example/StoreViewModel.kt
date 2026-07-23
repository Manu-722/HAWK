package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = Repository(db)

    // User Session
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Products
    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredProducts: StateFlow<List<Product>> = products
        .map { list -> list.filter { it.isFeatured } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Product Comparison
    private val _compareList = MutableStateFlow<List<Product>>(emptyList())
    val compareList: StateFlow<List<Product>> = _compareList.asStateFlow()

    // Cart (reactive combined with products)
    val cartItems: StateFlow<List<Pair<CartItem, Product>>> = currentUser
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                repository.getCartForUser(user.email).combine(products) { cartList, prodList ->
                    cartList.mapNotNull { cartItem ->
                        val matchedProduct = prodList.find { it.id == cartItem.productId }
                        if (matchedProduct != null) {
                            cartItem to matchedProduct
                        } else {
                            null
                        }
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartTotal: StateFlow<Double> = cartItems
        .map { list -> list.sumOf { it.first.quantity * it.second.price } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Order History
    val orderHistory: StateFlow<List<Order>> = currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else repository.getOrdersForUser(user.email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Orders
    val allOrders: StateFlow<List<Order>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Users List
    val allAdmins: StateFlow<List<User>> = repository.allAdmins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Reviews across products for dynamic ratings
    val allReviews: StateFlow<List<Review>> = repository.allReviews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfNeeded()
        }
    }

    // AUTH METHODS
    fun login(email: String, pwhash: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val cleanedEmail = email.trim().lowercase()
            val user = repository.getUserByEmail(cleanedEmail)
            if (user != null && user.passwordHash == pwhash) {
                _currentUser.value = user
                onResult(true)
            } else {
                _authError.value = "Invalid email or password."
                onResult(false)
            }
        }
    }

    fun signup(email: String, fullName: String, pwhash: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val cleanedEmail = email.trim().lowercase()

            if (cleanedEmail.isEmpty() || fullName.trim().isEmpty() || pwhash.isEmpty()) {
                _authError.value = "Please fill in all fields."
                onResult(false)
                return@launch
            }

            val existing = repository.getUserByEmail(cleanedEmail)
            if (existing != null) {
                _authError.value = "An account with this email already exists."
                onResult(false)
                return@launch
            }

            // Note: STRICT restriction - signup is ALWAYS customer (isAdmin = false).
            // Predefined admin users "admin1@induction.com" and "admin2@induction.com"
            // are preseeded in the DB and are the ONLY accounts that have admin capability.
            val newUser = User(
                email = cleanedEmail,
                fullName = fullName,
                passwordHash = pwhash,
                isAdmin = false
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            onResult(true)
        }
    }

    fun loginWithGoogleSimulated(email: String, name: String, phoneNumber: String = "") {
        viewModelScope.launch {
            val cleanedEmail = email.trim().lowercase()
            // Check if admin email or regular
            val isAdminEmail = (cleanedEmail == "admin1@induction.com" || cleanedEmail == "admin2@induction.com")
            
            var user = repository.getUserByEmail(cleanedEmail)
            if (user == null) {
                user = User(
                    email = cleanedEmail,
                    fullName = name,
                    passwordHash = "google_sign_in_token",
                    isAdmin = isAdminEmail,
                    phoneNumber = phoneNumber
                )
                repository.insertUser(user)
            } else if (phoneNumber.isNotBlank()) {
                user = user.copy(phoneNumber = phoneNumber)
                repository.insertUser(user)
            }
            _currentUser.value = user
        }
    }

    fun logout() {
        _currentUser.value = null
        _authError.value = null
        _compareList.value = emptyList()
    }

    // PASSWORD MANAGEMENT (Forgot Password & Change Password)
    private val _resetCode = MutableStateFlow<String?>(null)
    val resetCode: StateFlow<String?> = _resetCode.asStateFlow()
    
    private var codeGeneratedTime: Long = 0L
    private var resetEmailAddress: String = ""

    fun sendResetCode(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val cleanedEmail = email.trim().lowercase()
            val user = repository.getUserByEmail(cleanedEmail)
            if (user == null) {
                onResult(false, "No account found with this email.")
                return@launch
            }
            val code = (100000..999999).random().toString()
            _resetCode.value = code
            codeGeneratedTime = System.currentTimeMillis()
            resetEmailAddress = cleanedEmail
            onResult(true, "Reset code successfully generated and simulated-sent to $email!")
        }
    }

    fun verifyCodeAndResetPassword(code: String, newPw: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val currentCode = _resetCode.value
            if (currentCode == null || currentCode != code) {
                onResult(false, "Invalid verification code.")
                return@launch
            }
            val elapsed = System.currentTimeMillis() - codeGeneratedTime
            if (elapsed > 4 * 60 * 1000) { // 4 minutes expiry
                _resetCode.value = null
                onResult(false, "The verification code has expired (4-minute limit exceeded). Please request a new one.")
                return@launch
            }
            val user = repository.getUserByEmail(resetEmailAddress)
            if (user != null) {
                val updatedUser = user.copy(passwordHash = newPw)
                repository.insertUser(updatedUser)
                _currentUser.value = updatedUser // Auto sign in on successful reset
                _resetCode.value = null
                onResult(true, "Password successfully reset!")
            } else {
                onResult(false, "Failed to reset password. User not found.")
            }
        }
    }

    fun changePassword(newPw: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onResult(false, "No active user session found.")
            return
        }
        viewModelScope.launch {
            val updatedUser = user.copy(passwordHash = newPw)
            repository.insertUser(updatedUser)
            _currentUser.value = updatedUser
            onResult(true, "Password willingly updated successfully!")
        }
    }

    // CART METHODS
    fun addToCart(productId: Int, qty: Int = 1, onLoginRequired: () -> Unit) {
        val user = _currentUser.value
        if (user == null) {
            onLoginRequired()
            return
        }
        viewModelScope.launch {
            repository.addToCart(user.email, productId, qty)
        }
    }

    fun updateCartQuantity(cartItemId: Int, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeFromCart(cartItemId: Int) {
        viewModelScope.launch {
            repository.deleteCartItem(cartItemId)
        }
    }

    // COMPARE METHODS
    fun toggleCompare(product: Product) {
        val current = _compareList.value.toMutableList()
        val existing = current.find { it.id == product.id }
        if (existing != null) {
            current.remove(existing)
        } else {
            if (current.size >= 3) {
                // Limit to 3 items for side-by-side readability
                current.removeAt(0)
            }
            current.add(product)
        }
        _compareList.value = current
    }

    fun clearCompare() {
        _compareList.value = emptyList()
    }

    // ORDERING / CHECKOUT
    fun checkout(paybillRef: String, onResult: (Boolean) -> Unit) {
        val user = _currentUser.value ?: return
        val currentCart = cartItems.value
        val total = cartTotal.value

        if (currentCart.isEmpty()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            val success = repository.placeOrder(
                email = user.email,
                paymentMethod = "Paybill",
                paybillReference = paybillRef,
                totalAmount = total,
                cartItemsWithProduct = currentCart
            )
            onResult(success)
        }
    }

    // ADMIN CONTROLS
    fun addProductAdmin(
        title: String,
        description: String,
        price: Double,
        powerWatts: Int,
        coilsCount: Int,
        imageUrl: String,
        warrantyMonths: Int,
        safetyFeatures: String,
        controlType: String,
        isFeatured: Boolean,
        category: String
    ) {
        viewModelScope.launch {
            val product = Product(
                title = title,
                description = description,
                price = price,
                powerWatts = powerWatts,
                coilsCount = coilsCount,
                imageUrl = imageUrl.ifBlank { "custom_cooker" },
                warrantyMonths = warrantyMonths,
                safetyFeatures = safetyFeatures,
                controlType = controlType,
                isFeatured = isFeatured,
                category = category.ifBlank { "Induction Cooker" }
            )
            repository.insertProduct(product)
        }
    }

    fun updateProductPriceAdmin(productId: Int, newPrice: Double) {
        viewModelScope.launch {
            val p = repository.getProductById(productId)
            if (p != null) {
                repository.updateProduct(p.copy(price = newPrice))
            }
        }
    }

    fun updateProductImageAdmin(productId: Int, newUrl: String) {
        viewModelScope.launch {
            val p = repository.getProductById(productId)
            if (p != null) {
                repository.updateProduct(p.copy(imageUrl = newUrl))
            }
        }
    }

    fun deleteProductAdmin(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateProductAdmin(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun addAdminUser(
        email: String,
        fullName: String,
        passwordHash: String,
        phoneNumber: String,
        details: String,
        pictureUrl: String
    ) {
        viewModelScope.launch {
            val adminUser = User(
                email = email.trim().lowercase(),
                fullName = fullName,
                passwordHash = passwordHash,
                isAdmin = true,
                phoneNumber = phoneNumber,
                details = details,
                pictureUrl = pictureUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150" }
            )
            repository.insertUser(adminUser)
        }
    }

    fun updateOrderStatusAdmin(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun toggleProductSlideshowAdmin(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product.copy(isFeatured = !product.isFeatured))
        }
    }

    fun submitReview(productId: Int, rating: Int, comment: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val review = Review(
                productId = productId,
                userEmail = user.email,
                userName = user.fullName,
                rating = rating,
                comment = comment
            )
            repository.insertReview(review)
        }
    }

    fun getReviewsForProduct(productId: Int): Flow<List<Review>> {
        return repository.getReviewsForProduct(productId)
    }

    suspend fun getOrderItems(orderId: Int): List<OrderItem> {
        return repository.getOrderItems(orderId)
    }
}
