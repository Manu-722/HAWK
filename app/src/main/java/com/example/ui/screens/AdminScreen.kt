package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.StoreViewModel
import com.example.data.Order
import com.example.data.Product
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: StoreViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val products by viewModel.products.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()

    val scrollState = rememberScrollState()

    // Sub tabs: ADD, MANAGE, ORDERS
    var selectedAdminSubTab by remember { mutableStateOf("ADD") }

    // Check strict authorized privileges
    val isAuthorized = currentUser?.isAdmin == true

    if (!isAuthorized) {
        var quickAdminEmail by remember { mutableStateOf("") }
        var quickAdminPassword by remember { mutableStateOf("") }
        var isQuickPasswordVisible by remember { mutableStateOf(false) }
        var quickAuthError by remember { mutableStateOf<String?>(null) }

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .border(1.dp, SleekSlate200, RoundedCornerShape(24.dp))
                    .background(SleekSlate50, RoundedCornerShape(24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hawk_logo),
                    contentDescription = "Hawk Life Solutions Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, SleekSlate200, CircleShape)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "HAWK LIFE SOLUTIONS",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate500,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Admin Portal Login",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    color = SleekSlate950,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = quickAdminEmail,
                    onValueChange = { 
                        quickAdminEmail = it
                        quickAuthError = null
                    },
                    placeholder = { Text("Admin Email", fontSize = 13.sp, color = SleekSlate400) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Admin", tint = SleekSlate500) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = SleekSlate200,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = quickAdminPassword,
                    onValueChange = { 
                        quickAdminPassword = it
                        quickAuthError = null
                    },
                    placeholder = { Text("Admin Password", fontSize = 13.sp, color = SleekSlate400) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = SleekSlate500) },
                    trailingIcon = {
                        IconButton(onClick = { isQuickPasswordVisible = !isQuickPasswordVisible }) {
                            Icon(
                                imageVector = if (isQuickPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password",
                                tint = SleekSlate600
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (isQuickPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = SleekSlate200,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                quickAuthError?.let { err ->
                    Text(
                        text = err,
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Button(
                    onClick = {
                        if (quickAdminEmail.isBlank() || quickAdminPassword.isBlank()) {
                            quickAuthError = "Please enter Admin Email and Password."
                            return@Button
                        }
                        viewModel.login(quickAdminEmail, quickAdminPassword) { success ->
                            if (!success) {
                                quickAuthError = "Invalid Admin credentials."
                            }
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Unlock Admin Dashboard", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onBack) {
                    Text("Return to Storefront", fontSize = 12.sp, color = SleekSlate600, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        return
    }

    // FORM STATE
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var wattsStr by remember { mutableStateOf("2000") }
    var coilsStr by remember { mutableStateOf("1") }
    var imageUrl by remember { mutableStateOf("") }
    var warrantyStr by remember { mutableStateOf("12") }
    var safetyFeatures by remember { mutableStateOf("Child Lock, Overheat Auto-off, Pan Detector") }
    var controlType by remember { mutableStateOf("Touch Slider Control") }
    var isFeatured by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Induction Cookers") }

    var formMessage by remember { mutableStateOf("") }

    var selectedFileName by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUrl = uri.toString()
            selectedFileName = "Gallery Photo (${uri.lastPathSegment ?: "image"})"
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "document"
            selectedFileName = fileName
            if (!description.contains("Attachment:")) {
                description = "$description\n\n[Attachment: $fileName | Uri: $uri]"
            }
        }
    }

    // NEW ADMIN REGISTER FORM STATE
    var adminEmail by remember { mutableStateOf("") }
    var adminName by remember { mutableStateOf("") }
    var adminPassword by remember { mutableStateOf("") }
    var adminPhone by remember { mutableStateOf("") }
    var adminDetails by remember { mutableStateOf("") }
    var adminPicture by remember { mutableStateOf("") }
    var adminMessage by remember { mutableStateOf("") }

    val allAdmins by viewModel.allAdmins.collectAsState()

    // Dialog inline edit state
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var editTitleStr by remember { mutableStateOf("") }
    var editDescriptionStr by remember { mutableStateOf("") }
    var editPriceStr by remember { mutableStateOf("") }
    var editWattsStr by remember { mutableStateOf("") }
    var editCoilsStr by remember { mutableStateOf("") }
    var editWarrantyStr by remember { mutableStateOf("") }
    var editImageUrl by remember { mutableStateOf("") }
    var editControlTypeStr by remember { mutableStateOf("") }
    var editSafetyFeaturesStr by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // App top header - Sleek Style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.hawk_logo),
                contentDescription = "Hawk Life Solutions Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .border(1.dp, SleekSlate200, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Admin Dashboard",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = SleekSlate950
                )
                Text(
                    text = "Hawk Life Solutions Management",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = SleekSlate500
                )
            }
        }

        HorizontalDivider(color = SleekSlate100)

        // Segmented Tabs Selector - Sleek Style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SleekSlate50, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                val pendingOrdersCount = allOrders.count { it.status.contains("Pending", ignoreCase = true) || it.status.contains("Approval", ignoreCase = true) }
                val tabs = listOf(
                    "ADD" to "Add",
                    "SLIDESHOW" to "Slideshow",
                    "MANAGE" to "Stock",
                    "ORDERS" to if (pendingOrdersCount > 0) "Approvals ($pendingOrdersCount)" else "Approvals",
                    "ADMINS" to "Admins"
                )
                tabs.forEach { (key, display) ->
                    val isSel = selectedAdminSubTab == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color.Black else Color.Transparent)
                            .clickable { selectedAdminSubTab = key }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = display,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else SleekSlate500
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = SleekSlate100)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            when (selectedAdminSubTab) {
                "ADD" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Launch New Induction System",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Product Title", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Description", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = false,
                            maxLines = 4,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )

                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            OutlinedTextField(
                                value = priceStr,
                                onValueChange = { priceStr = it },
                                placeholder = { Text("Price (KSh)", fontSize = 14.sp, color = SleekSlate400) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).padding(end = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                                )
                            )
                            OutlinedTextField(
                                value = wattsStr,
                                onValueChange = { wattsStr = it },
                                placeholder = { Text("Power (Watts)", fontSize = 14.sp, color = SleekSlate400) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).padding(start = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                                )
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            OutlinedTextField(
                                value = coilsStr,
                                onValueChange = { coilsStr = it },
                                placeholder = { Text("Coils / Zones", fontSize = 14.sp, color = SleekSlate400) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).padding(end = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                                )
                            )
                            OutlinedTextField(
                                value = warrantyStr,
                                onValueChange = { warrantyStr = it },
                                placeholder = { Text("Warranty (Months)", fontSize = 14.sp, color = SleekSlate400) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).padding(start = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                                )
                            )
                        }

                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            placeholder = { Text("Image URL or Select from Gallery", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    imagePickerLauncher.launch("image/*")
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SleekSlate100,
                                    contentColor = Color.Black
                                ),
                                border = BorderStroke(1.dp, SleekSlate200),
                                modifier = Modifier.weight(1f).height(40.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                            }

                            Button(
                                onClick = {
                                    filePickerLauncher.launch("*/*")
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SleekSlate100,
                                    contentColor = Color.Black
                                ),
                                border = BorderStroke(1.dp, SleekSlate200),
                                modifier = Modifier.weight(1f).height(40.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Other File", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                            }
                        }

                        if (selectedFileName.isNotBlank()) {
                            Text(
                                text = "Selected Attachment: $selectedFileName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20), // Dark Green
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = controlType,
                            onValueChange = { controlType = it },
                            placeholder = { Text("Control Panel Style", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                            )
                        )

                        OutlinedTextField(
                            value = safetyFeatures,
                            onValueChange = { safetyFeatures = it },
                            placeholder = { Text("Safety Shields & Protocols", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                            )
                        )

                        Text(
                            text = "Product Category Section",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekSlate700,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categoriesList = listOf("Induction Cookers", "Sufurias & Cookware", "Non-Stick Pans")
                            categoriesList.forEach { cat ->
                                val isSelected = category == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.dp, if (isSelected) Color.Black else SleekSlate200, RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color.Black else SleekSlate50, RoundedCornerShape(12.dp))
                                        .clickable { category = cat }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else SleekSlate600,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isFeatured,
                                onCheckedChange = { isFeatured = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color.Black, checkmarkColor = Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Promote to slideshow carousel system", fontSize = 13.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, color = SleekSlate700)
                        }

                        if (formMessage.isNotBlank()) {
                            Text(
                                text = formMessage,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }

                        Button(
                            onClick = {
                                val price = priceStr.toDoubleOrNull()
                                val watts = wattsStr.toIntOrNull()
                                val coils = coilsStr.toIntOrNull()
                                val warranty = warrantyStr.toIntOrNull()

                                if (title.isBlank() || description.isBlank() || priceStr.isBlank() ||
                                    wattsStr.isBlank() || coilsStr.isBlank() || warrantyStr.isBlank() ||
                                    imageUrl.isBlank() || safetyFeatures.isBlank() || controlType.isBlank() || category.isBlank() ||
                                    price == null || price <= 0 || watts == null || coils == null || warranty == null) {
                                    formMessage = "All fields are required! Please ensure no field is left empty."
                                } else {
                                    viewModel.addProductAdmin(
                                        title = title.trim(),
                                        description = description.trim(),
                                        price = price,
                                        powerWatts = watts,
                                        coilsCount = coils,
                                        imageUrl = imageUrl.trim(),
                                        warrantyMonths = warranty,
                                        safetyFeatures = safetyFeatures.trim(),
                                        controlType = controlType.trim(),
                                        isFeatured = isFeatured,
                                        category = category.trim()
                                    )
                                    formMessage = "Successfully launched: $title"
                                    // Reset
                                    title = ""
                                    description = ""
                                    priceStr = ""
                                    wattsStr = ""
                                    coilsStr = ""
                                    warrantyStr = ""
                                    imageUrl = ""
                                    safetyFeatures = ""
                                    controlType = ""
                                    category = "Induction Cooker"
                                    selectedFileName = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Launch Induction Cooktop", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                "SLIDESHOW" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Homepage Slideshow & Banner Manager",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Easily add products to the homepage banner slideshow carousel. Items marked featured rotate automatically.",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = SleekSlate500,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val featuredList = remember(products) { products.filter { it.isFeatured } }
                        val notFeaturedList = remember(products) { products.filter { !it.isFeatured } }

                        Text(
                            text = "Active Slideshow Carousel Products (${featuredList.size})",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (featuredList.isEmpty()) {
                            Surface(
                                color = SleekSlate50,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SleekSlate200),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                Text(
                                    text = "No products explicitly pinned to slideshow yet. Tap '+ Add to Slideshow' on any product below.",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = SleekSlate600,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        } else {
                            featuredList.forEach { product ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                        .background(Color.White, RoundedCornerShape(14.dp))
                                        .border(1.5.dp, Color.Black, RoundedCornerShape(14.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = product.title,
                                                fontSize = 13.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekSlate950,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(
                                            text = "${product.powerWatts}W • ${product.coilsCount} Zone(s) • KSh ${String.format("%,.0f", product.price)}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            color = SleekSlate500,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.toggleProductSlideshowAdmin(product) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SleekSlate100,
                                            contentColor = Color.Red
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Remove", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Catalog Stock (Tap to Add to Slideshow)",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (notFeaturedList.isEmpty()) {
                            Text("All active products are currently added to the slideshow!", fontSize = 12.sp, color = SleekSlate500)
                        } else {
                            notFeaturedList.forEach { product ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .border(1.dp, SleekSlate100, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.title,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SleekSlate950,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${product.powerWatts}W • KSh ${String.format("%,.0f", product.price)}",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            color = SleekSlate500
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.toggleProductSlideshowAdmin(product) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Black,
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("+ Add to Slideshow", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                                    }
                                }
                            }
                        }
                    }
                }

                "MANAGE" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "System Stock Manager",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        products.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                              ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = p.title,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekSlate950,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${p.powerWatts}W • ${p.coilsCount} Zone(s) • KSh ${String.format("%,.0f", p.price)}",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        color = SleekSlate500,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Row {
                                    IconButton(onClick = {
                                         editingProduct = p
                                         editTitleStr = p.title
                                         editDescriptionStr = p.description
                                         editPriceStr = p.price.toString()
                                         editWattsStr = p.powerWatts.toString()
                                         editCoilsStr = p.coilsCount.toString()
                                         editWarrantyStr = p.warrantyMonths.toString()
                                         editImageUrl = p.imageUrl
                                         editControlTypeStr = p.controlType
                                         editSafetyFeaturesStr = p.safetyFeatures
                                     }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black)
                                    }
                                    IconButton(onClick = { viewModel.deleteProductAdmin(p) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SleekSlate500)
                                    }
                                }
                            }
                        }
                    }
                }

                "ORDERS" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Customer Order Approvals & Status Tracking",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Review payment details and approve customer purchases for fulfillment and dispatch.",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = SleekSlate500,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val pendingOrders = allOrders.filter { it.status.contains("Pending", ignoreCase = true) || it.status.contains("Approval", ignoreCase = true) }

                        if (pendingOrders.isNotEmpty()) {
                            Surface(
                                color = Color.Black,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color.Yellow)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${pendingOrders.size} Order(s) Awaiting Admin Payment Approval",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

                        if (allOrders.isEmpty()) {
                            Text("No customer transactions submitted yet.", fontSize = 13.sp, fontFamily = FontFamily.SansSerif, color = SleekSlate500)
                        } else {
                            allOrders.forEach { order ->
                                val isPending = order.status.contains("Pending", ignoreCase = true) || order.status.contains("Approval", ignoreCase = true)

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                        .background(Color.White, RoundedCornerShape(16.dp))
                                        .border(if (isPending) 2.dp else 1.dp, if (isPending) Color.Black else SleekSlate100, RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Order ID: #${order.id}",
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekSlate950
                                            )
                                            Text(
                                                text = "Customer: ${order.userEmail}",
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SleekSlate700,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                            Text(
                                                text = sdf.format(Date(order.orderDate)),
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                color = SleekSlate400,
                                                modifier = Modifier.padding(top = 1.dp)
                                            )
                                        }

                                        Text(
                                            text = order.status.uppercase(),
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isPending) Color.White else SleekSlate700,
                                            modifier = Modifier
                                                .background(if (isPending) Color.Black else SleekSlate100, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = SleekSlate100)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Paybill Ref: ${order.paybillReference}",
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekSlate950
                                            )
                                            Text(
                                                text = "Total Amount: KSh ${String.format("%,.0f", order.totalAmount)}",
                                                fontSize = 13.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.Black
                                            )
                                        }

                                        if (isPending) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedButton(
                                                    onClick = { viewModel.updateOrderStatusAdmin(order.id, "DECLINED") },
                                                    shape = RoundedCornerShape(12.dp),
                                                    border = BorderStroke(1.dp, SleekSlate300),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(38.dp)
                                                ) {
                                                    Text("Decline", fontSize = 11.sp, color = SleekSlate700, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                                                }

                                                Button(
                                                    onClick = { viewModel.updateOrderStatusAdmin(order.id, "APPROVED & DISPATCHED") },
                                                    shape = RoundedCornerShape(12.dp),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color.Black,
                                                        contentColor = Color.White
                                                    ),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(38.dp)
                                                ) {
                                                    Text("APPROVE ORDER", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif)
                                                }
                                            }
                                        } else if (order.status == "APPROVED & DISPATCHED") {
                                            Button(
                                                onClick = { viewModel.updateOrderStatusAdmin(order.id, "Delivered") },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = SleekSlate100,
                                                    contentColor = Color.Black
                                                ),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(36.dp)
                                            ) {
                                                Text("Mark Delivered", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "ADMINS" -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Register Secure Administrator",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = adminName,
                            onValueChange = { adminName = it },
                            placeholder = { Text("Full Name (e.g. Emmanuel Mulongo)", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )

                        OutlinedTextField(
                            value = adminEmail,
                            onValueChange = { adminEmail = it },
                            placeholder = { Text("Secure Email Address", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )

                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            OutlinedTextField(
                                value = adminPassword,
                                onValueChange = { adminPassword = it },
                                placeholder = { Text("Secret Password", fontSize = 14.sp, color = SleekSlate400) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).padding(end = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                                )
                            )

                            OutlinedTextField(
                                value = adminPhone,
                                onValueChange = { adminPhone = it },
                                placeholder = { Text("Phone (e.g. +254...)", fontSize = 14.sp, color = SleekSlate400) },
                                singleLine = true,
                                modifier = Modifier.weight(1f).padding(start = 6.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Black,
                                    unfocusedBorderColor = SleekSlate200,
                                    focusedContainerColor = SleekSlate50,
                                    unfocusedContainerColor = SleekSlate50
                                )
                            )
                        }

                        OutlinedTextField(
                            value = adminDetails,
                            onValueChange = { adminDetails = it },
                            placeholder = { Text("Safety Officer details & role instructions...", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = false,
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )

                        OutlinedTextField(
                            value = adminPicture,
                            onValueChange = { adminPicture = it },
                            placeholder = { Text("Profile Photo URL (Optional)", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )

                        if (adminMessage.isNotBlank()) {
                            Text(
                                text = adminMessage,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.SansSerif,
                                color = if (adminMessage.contains("Success")) Color.Black else Color.Red,
                                modifier = Modifier.padding(vertical = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                if (adminName.isBlank() || adminEmail.isBlank() || adminPassword.isBlank() || adminPhone.isBlank() || adminDetails.isBlank()) {
                                    adminMessage = "All parameters are mandatory for authorized admins!"
                                } else {
                                    viewModel.addAdminUser(
                                        email = adminEmail,
                                        fullName = adminName,
                                        passwordHash = adminPassword,
                                        phoneNumber = adminPhone,
                                        details = adminDetails,
                                        pictureUrl = adminPicture
                                    )
                                    adminMessage = "Successfully registered admin: $adminName!"
                                    // Reset fields
                                    adminName = ""
                                    adminEmail = ""
                                    adminPassword = ""
                                    adminPhone = ""
                                    adminDetails = ""
                                    adminPicture = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) {
                            Text("REGISTER SAFETY OFFICER", fontSize = 12.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = SleekSlate100)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Active Hawk Administrators (${allAdmins.size})",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        allAdmins.forEach { admin ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .border(1.dp, SleekSlate200, RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SleekSlate50)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Profile Image Placeholder Box
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = admin.fullName.take(2).uppercase(),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = admin.fullName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekSlate950
                                        )
                                        Text(
                                            text = "Email: ${admin.email}",
                                            fontSize = 11.sp,
                                            color = SleekSlate500
                                        )
                                        Text(
                                            text = "Phone: ${admin.phoneNumber}",
                                            fontSize = 11.sp,
                                            color = SleekSlate500
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = admin.details,
                                            fontSize = 12.sp,
                                            color = SleekSlate700,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialogue editor for stocking prices - Sleek Style
    if (editingProduct != null) {
        AlertDialog(
            onDismissRequest = { editingProduct = null },
            confirmButton = {
                Button(
                    onClick = {
                        val prod = editingProduct
                        if (prod != null) {
                            val newPrice = editPriceStr.toDoubleOrNull() ?: prod.price
                            val newWatts = editWattsStr.toIntOrNull() ?: prod.powerWatts
                            val newCoils = editCoilsStr.toIntOrNull() ?: prod.coilsCount
                            val newWarranty = editWarrantyStr.toIntOrNull() ?: prod.warrantyMonths
                            
                            if (editTitleStr.isNotBlank()) {
                                viewModel.updateProductAdmin(
                                    prod.copy(
                                        title = editTitleStr,
                                        description = editDescriptionStr,
                                        price = newPrice,
                                        powerWatts = newWatts,
                                        coilsCount = newCoils,
                                        warrantyMonths = newWarranty,
                                        imageUrl = editImageUrl.trim(),
                                        controlType = editControlTypeStr,
                                        safetyFeatures = editSafetyFeaturesStr
                                    )
                                )
                                editingProduct = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Save", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingProduct = null }) {
                    Text("Cancel", color = Color.Black, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Edit Product Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = SleekSlate950
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Modify core specifications, aesthetics, and electrical parameters for this induction cooker product.",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = SleekSlate500,
                        modifier = Modifier.padding(bottom = 14.dp),
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = editTitleStr,
                        onValueChange = { editTitleStr = it },
                        placeholder = { Text("Product Title", fontSize = 14.sp, color = SleekSlate400) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = SleekSlate200,
                            focusedContainerColor = SleekSlate50,
                            unfocusedContainerColor = SleekSlate50
                        )
                    )

                    OutlinedTextField(
                        value = editDescriptionStr,
                        onValueChange = { editDescriptionStr = it },
                        placeholder = { Text("Description & Specifications", fontSize = 14.sp, color = SleekSlate400) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = SleekSlate200,
                            focusedContainerColor = SleekSlate50,
                            unfocusedContainerColor = SleekSlate50
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                        OutlinedTextField(
                            value = editWattsStr,
                            onValueChange = { editWattsStr = it },
                            placeholder = { Text("Watts", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )
                        OutlinedTextField(
                            value = editCoilsStr,
                            onValueChange = { editCoilsStr = it },
                            placeholder = { Text("Coils / Zones", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                        OutlinedTextField(
                            value = editPriceStr,
                            onValueChange = { editPriceStr = it },
                            placeholder = { Text("Price (KSh)", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).padding(end = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )
                        OutlinedTextField(
                            value = editWarrantyStr,
                            onValueChange = { editWarrantyStr = it },
                            placeholder = { Text("Warranty", fontSize = 14.sp, color = SleekSlate400) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).padding(start = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = SleekSlate200,
                                focusedContainerColor = SleekSlate50,
                                unfocusedContainerColor = SleekSlate50
                            )
                        )
                    }

                    OutlinedTextField(
                        value = editImageUrl,
                        onValueChange = { editImageUrl = it },
                        placeholder = { Text("Image Resource URL", fontSize = 14.sp, color = SleekSlate400) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = SleekSlate200,
                            focusedContainerColor = SleekSlate50,
                            unfocusedContainerColor = SleekSlate50
                        )
                    )

                    OutlinedTextField(
                        value = editControlTypeStr,
                        onValueChange = { editControlTypeStr = it },
                        placeholder = { Text("Control Style", fontSize = 14.sp, color = SleekSlate400) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = SleekSlate200,
                            focusedContainerColor = SleekSlate50,
                            unfocusedContainerColor = SleekSlate50
                        )
                    )

                    OutlinedTextField(
                        value = editSafetyFeaturesStr,
                        onValueChange = { editSafetyFeaturesStr = it },
                        placeholder = { Text("Safety Features", fontSize = 14.sp, color = SleekSlate400) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = SleekSlate200,
                            focusedContainerColor = SleekSlate50,
                            unfocusedContainerColor = SleekSlate50
                        )
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.border(1.dp, SleekSlate100, RoundedCornerShape(24.dp))
        )
    }
}
