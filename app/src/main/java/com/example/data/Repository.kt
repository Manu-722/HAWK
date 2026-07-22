package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val db: AppDatabase) {

    private val productDao = db.productDao()
    private val userDao = db.userDao()
    private val reviewDao = db.reviewDao()
    private val cartItemDao = db.cartItemDao()
    private val orderDao = db.orderDao()

    // Products
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allAdmins: Flow<List<User>> = userDao.getAllAdmins()

    suspend fun getProductById(id: Int): Product? = withContext(Dispatchers.IO) {
        productDao.getProductById(id)
    }

    suspend fun insertProduct(product: Product): Long = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    // Users
    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun insertUser(user: User) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    // Reviews
    fun getReviewsForProduct(productId: Int): Flow<List<Review>> = reviewDao.getReviewsForProduct(productId)

    suspend fun insertReview(review: Review) = withContext(Dispatchers.IO) {
        reviewDao.insertReview(review)
    }

    // Cart
    fun getCartForUser(email: String): Flow<List<CartItem>> = cartItemDao.getCartForUser(email)

    suspend fun getCartForUserSync(email: String): List<CartItem> = withContext(Dispatchers.IO) {
        cartItemDao.getCartForUserSync(email)
    }

    suspend fun addToCart(email: String, productId: Int, qty: Int = 1) = withContext(Dispatchers.IO) {
        val existing = cartItemDao.getCartItem(email, productId)
        if (existing != null) {
            cartItemDao.updateCartItemQuantity(existing.id, existing.quantity + qty)
        } else {
            cartItemDao.insertCartItem(CartItem(userEmail = email, productId = productId, quantity = qty))
        }
    }

    suspend fun updateCartQuantity(id: Int, qty: Int) = withContext(Dispatchers.IO) {
        if (qty <= 0) {
            cartItemDao.deleteCartItem(id)
        } else {
            cartItemDao.updateCartItemQuantity(id, qty)
        }
    }

    suspend fun deleteCartItem(id: Int) = withContext(Dispatchers.IO) {
        cartItemDao.deleteCartItem(id)
    }

    suspend fun clearCartForUser(email: String) = withContext(Dispatchers.IO) {
        cartItemDao.clearCartForUser(email)
    }

    // Orders
    fun getOrdersForUser(email: String): Flow<List<Order>> = orderDao.getOrdersForUser(email)
    fun getAllOrders(): Flow<List<Order>> = orderDao.getAllOrders()

    suspend fun getOrderItems(orderId: Int): List<OrderItem> = withContext(Dispatchers.IO) {
        orderDao.getOrderItems(orderId)
    }

    suspend fun updateOrderStatus(orderId: Int, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
    }

    suspend fun placeOrder(
        email: String,
        paymentMethod: String,
        paybillReference: String,
        totalAmount: Double,
        cartItemsWithProduct: List<Pair<CartItem, Product>>
    ): Boolean = withContext(Dispatchers.IO) {
        if (cartItemsWithProduct.isEmpty()) return@withContext false

        // 1. Create order
        val order = Order(
            userEmail = email,
            totalAmount = totalAmount,
            paymentMethod = paymentMethod,
            paybillReference = paybillReference,
            status = "Pending Approval"
        )
        val orderId = orderDao.insertOrder(order).toInt()

        // 2. Map items
        val orderItems = cartItemsWithProduct.map { (cartItem, product) ->
            OrderItem(
                orderId = orderId,
                productId = product.id,
                productTitle = product.title,
                price = product.price,
                quantity = cartItem.quantity
            )
        }
        orderDao.insertOrderItems(orderItems)

        // 3. Clear cart
        cartItemDao.clearCartForUser(email)
        true
    }

    // Seeding DB
    suspend fun seedDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        // 1. Seed two admin users
        val admin1 = userDao.getUserByEmail("admin1@induction.com")
        if (admin1 == null) {
            userDao.insertUser(
                User(
                    email = "admin1@induction.com",
                    fullName = "Emmanuel Mulongo",
                    passwordHash = "admin123", // Simple hash or plain text for local demo
                    isAdmin = true,
                    phoneNumber = "+254 712 345 678",
                    details = "Head of Hawk Inductions. Chief engineer and safety compliance supervisor.",
                    pictureUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
                )
            )
        }

        val admin2 = userDao.getUserByEmail("admin2@induction.com")
        if (admin2 == null) {
            userDao.insertUser(
                User(
                    email = "admin2@induction.com",
                    fullName = "Admin Assistant",
                    passwordHash = "admin456",
                    isAdmin = true,
                    phoneNumber = "+254 722 987 654",
                    details = "Inventory administrator. Manages stock lists, applies discounts, and adjusts prices.",
                    pictureUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150"
                )
            )
        }

        // 2. Seed products if empty
        val currentProducts = db.openHelper.writableDatabase.compileStatement("SELECT COUNT(*) FROM products").simpleQueryForLong()
        if (currentProducts == 0L) {
            val p1Id = productDao.insertProduct(
                Product(
                    title = "Hawk Aura Single Hob",
                    description = "Sleek, ultra-thin single-zone induction cooker with crystal glass surface. Instant electromagnetic heating directly in your cookware with minimal power loss. Perfect for compact spaces, studio apartments, and precise boiling.",
                    price = 8500.0,
                    powerWatts = 2100,
                    coilsCount = 1,
                    imageUrl = "aura_single",
                    warrantyMonths = 12,
                    safetyFeatures = "Child Lock, Overheat Auto-off, Pan Detector",
                    controlType = "Touch Slider Interface",
                    isFeatured = true,
                    category = "Induction Cookers"
                )
            ).toInt()

            val p2Id = productDao.insertProduct(
                Product(
                    title = "Hawk Apex Duo Cooktop",
                    description = "Professional dual-zone cooktop designed for fast multi-tasking. Delivers 9 power stages per zone, allowing you to simmer a delicate sauce on one side while searing on the other.",
                    price = 19500.0,
                    powerWatts = 3500,
                    coilsCount = 2,
                    imageUrl = "apex_duo",
                    warrantyMonths = 24,
                    safetyFeatures = "Individual Zone Timers, Spill Protection, Child Safety Lock",
                    controlType = "Dual Precise Touch Controls",
                    isFeatured = true,
                    category = "Induction Cookers"
                )
            ).toInt()

            val p3Id = productDao.insertProduct(
                Product(
                    title = "Hawk Zenith Quad Hob",
                    description = "Our premium 4-zone masterclass induction unit. Features smart bridge-mode zone linking for oversized griddles or large pots. Automated boiling sensors prevent water spillover.",
                    price = 59999.0,
                    powerWatts = 7400,
                    coilsCount = 4,
                    imageUrl = "zenith_quad",
                    warrantyMonths = 36,
                    safetyFeatures = "Triple-Zone Auto-Sizing, Residual Heat Indicators, Key Lock",
                    controlType = "Integrated Multi-Slider Touch",
                    isFeatured = false,
                    category = "Induction Cookers"
                )
            ).toInt()

            val p4Id = productDao.insertProduct(
                Product(
                    title = "Hawk Nomad Go Portable",
                    description = "Highly durable portable cooker built for heavy use on the move. Features an impact-resistant robust frame and classic dial selector for fast, responsive temperature setting. Ideal for outdoor catering.",
                    price = 5200.0,
                    powerWatts = 1500,
                    coilsCount = 1,
                    imageUrl = "nomad_go",
                    warrantyMonths = 12,
                    safetyFeatures = "Non-slip Feet, Magnetic Pan Verification, Auto Shutoff",
                    controlType = "Mechanical Dial + Tactile Touch",
                    isFeatured = true,
                    category = "Induction Cookers"
                )
            ).toInt()

            val p5Id = productDao.insertProduct(
                Product(
                    title = "Hawk Heavy Stainless Steel Sufuria",
                    description = "Heavy-base, triple-ply induction optimized stainless steel sufuria (28cm). Designed specifically for maximum heat absorption on electromagnetic induction cookers. Perfect for traditional meals like Ugali and Sukuma Wiki.",
                    price = 4500.0,
                    powerWatts = 0,
                    coilsCount = 0,
                    imageUrl = "sufuria_heavy",
                    warrantyMonths = 24,
                    safetyFeatures = "Cool-Touch double-riveted steel handles",
                    controlType = "Stainless steel lids",
                    isFeatured = true,
                    category = "Sufurias & Cookware"
                )
            ).toInt()

            val p6Id = productDao.insertProduct(
                Product(
                    title = "Hawk Non-Stick Frying Pan",
                    description = "Premium non-stick composite frying pan (26cm) optimized for magnetic induction. Features a multi-layer PFOA-free coating for healthy oil-free cooking, quick thermal response, and easy clean-up.",
                    price = 3800.0,
                    powerWatts = 0,
                    coilsCount = 0,
                    imageUrl = "nonstick_pan",
                    warrantyMonths = 12,
                    safetyFeatures = "Ergonomic stay-cool silicone handle",
                    controlType = "Flat aluminum-steel base",
                    isFeatured = true,
                    category = "Non-Stick Pans"
                )
            ).toInt()

            val p7Id = productDao.insertProduct(
                Product(
                    title = "Hawk Deep Non-Stick Wok",
                    description = "Spacious deep non-stick stir-fry pan (32cm) with heavy tempered glass lid. Offers rapid heat-up and uniform heat distribution on induction hobs. Perfect for family deep-frying or steaming.",
                    price = 4800.0,
                    powerWatts = 0,
                    coilsCount = 0,
                    imageUrl = "nonstick_wok",
                    warrantyMonths = 12,
                    safetyFeatures = "Tempered shatter-proof glass lid",
                    controlType = "Bakelite insulated handle",
                    isFeatured = false,
                    category = "Non-Stick Pans"
                )
            ).toInt()

            // Seed reviews for Product 1
            reviewDao.insertReview(
                Review(
                    productId = p1Id,
                    userEmail = "john@demo.com",
                    userName = "John Doe",
                    rating = 5,
                    comment = "Absolutely incredible! Boils water faster than my gas stove. Easy to clean, and looks incredibly premium in my kitchen."
                )
            )
            reviewDao.insertReview(
                Review(
                    productId = p1Id,
                    userEmail = "alice@demo.com",
                    userName = "Alice Smith",
                    rating = 4,
                    comment = "Very sleek design. Fits nicely in my tiny studio. The touch slider is extremely responsive."
                )
            )

            // Seed reviews for Product 2
            reviewDao.insertReview(
                Review(
                    productId = p2Id,
                    userEmail = "chef_mark@demo.com",
                    userName = "Chef Mark",
                    rating = 5,
                    comment = "The heat control is incredibly precise. Perfect for professional pan-searing. Best induction cooktop I've purchased so far."
                )
            )
        }
    }
}
