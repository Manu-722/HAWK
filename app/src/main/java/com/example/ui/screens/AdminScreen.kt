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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val isAuthorized = currentUser != null && currentUser!!.isAdmin

    if (!isAuthorized) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .border(1.dp, SleekSlate200, RoundedCornerShape(24.dp))
                    .background(SleekSlate50, RoundedCornerShape(24.dp))
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = "Security Alert",
                    tint = Color.Black,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Access Denied",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This area is strictly restricted to authorized administrator credentials. Unauthenticated access attempts are logged. Please return to the customer terminal.",
                    fontSize = 13.sp,
                    color = SleekSlate500,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBack,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Return to Store Terminal", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
            Column {
                Text(
                    text = "Admin Dashboard",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = SleekSlate950
                )
                Text(
                    text = "Add systems, manage stocks & customer orders",
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
                val tabs = listOf(
                    "ADD" to "Add System",
                    "MANAGE" to "Manage Stock",
                    "ORDERS" to "Customer Orders",
                    "ADMINS" to "Admins Setup"
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

                                if (title.isBlank() || price == null || watts == null || coils == null || warranty == null) {
                                    formMessage = "Invalid parameter fields. Please review values."
                                } else {
                                    viewModel.addProductAdmin(
                                        title = title,
                                        description = description,
                                        price = price,
                                        powerWatts = watts,
                                        coilsCount = coils,
                                        imageUrl = imageUrl,
                                        warrantyMonths = warranty,
                                        safetyFeatures = safetyFeatures,
                                        controlType = controlType,
                                        isFeatured = isFeatured,
                                        category = category
                                    )
                                    formMessage = "Successfully launched: $title"
                                    // Reset
                                    title = ""
                                    description = ""
                                    priceStr = ""
                                    imageUrl = ""
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
                            text = "Customer Transactions",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

                        if (allOrders.isEmpty()) {
                            Text("No customer transactions submitted yet.", fontSize = 13.sp, fontFamily = FontFamily.SansSerif, color = SleekSlate500)
                        } else {
                            allOrders.forEach { order ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                        .background(Color.White, RoundedCornerShape(16.dp))
                                        .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp))
                                        .padding(14.dp)
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
                                                text = order.userEmail,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                color = SleekSlate600,
                                                modifier = Modifier.padding(top = 1.dp)
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
                                            text = order.status,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.Bold,
                                            color = if (order.status.contains("Approval")) Color.Black else SleekSlate500,
                                            modifier = Modifier
                                                .background(if (order.status.contains("Approval")) SleekSlate200 else SleekSlate100, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Ref: ${order.paybillReference}",
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekSlate950
                                            )
                                            Text(
                                                text = "Total: KSh ${String.format("%,.0f", order.totalAmount)}",
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                color = SleekSlate600
                                            )
                                        }

                                        // Status management triggers
                                        if (order.status != "Delivered") {
                                            Button(
                                                onClick = {
                                                    val nextStatus = if (order.status == "Pending Approval") "Shipped" else "Delivered"
                                                    viewModel.updateOrderStatusAdmin(order.id, nextStatus)
                                                },
                                                shape = RoundedCornerShape(16.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color.Black,
                                                    contentColor = Color.White
                                                ),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text(
                                                    text = if (order.status == "Pending Approval") "Ship System" else "Deliver",
                                                    fontSize = 11.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontWeight = FontWeight.Bold
                                                )
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
                        val newPrice = editPriceStr.toDoubleOrNull() ?: editingProduct!!.price
                        val newWatts = editWattsStr.toIntOrNull() ?: editingProduct!!.powerWatts
                        val newCoils = editCoilsStr.toIntOrNull() ?: editingProduct!!.coilsCount
                        val newWarranty = editWarrantyStr.toIntOrNull() ?: editingProduct!!.warrantyMonths
                        
                        if (editTitleStr.isNotBlank() && editingProduct != null) {
                            viewModel.updateProductAdmin(
                                editingProduct!!.copy(
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
