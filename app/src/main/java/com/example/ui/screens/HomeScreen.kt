package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.StoreViewModel
import com.example.data.Product
import com.example.ui.components.InductionCookerGraphic
import com.example.ui.theme.*
import kotlinx.coroutines.delay

data class PromotionOffer(
    val title: String,
    val subtitle: String,
    val code: String,
    val discount: String,
    val coils: Int,
    val watts: Int,
    val imageUrl: String
)

@Composable
fun HomeScreen(
    viewModel: StoreViewModel,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val compareList by viewModel.compareList.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()

    var selectedCategory by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    // Settings / Willing Password Change States
    var showSettingsDialog by remember { mutableStateOf(false) }
    var changePwInput by remember { mutableStateOf("") }
    var changePwConfirmInput by remember { mutableStateOf("") }
    var settingsError by remember { mutableStateOf<String?>(null) }
    var settingsMessage by remember { mutableStateOf<String?>(null) }

    var showLogoutDialog by remember { mutableStateOf(false) }

    // Dynamic Promotion Carousel Slideshow built directly from Products in database marked as featured / slideshow
    val offers = remember(products) {
        val featured = products.filter { it.isFeatured }
        val displayProducts = if (featured.isNotEmpty()) featured else products
        if (displayProducts.isNotEmpty()) {
            displayProducts.map { p ->
                PromotionOffer(
                    title = p.title,
                    subtitle = "${p.powerWatts}W Rapid Induction • ${p.coilsCount} Zone${if (p.coilsCount > 1) "s" else ""}",
                    code = "HAWK${p.id}",
                    discount = "KSh ${String.format("%,.0f", p.price)}",
                    coils = p.coilsCount,
                    watts = p.powerWatts,
                    imageUrl = p.imageUrl
                )
            }
        } else {
            listOf(
                PromotionOffer(
                    title = "Hawk Aura Single Hob",
                    subtitle = "2200W Rapid Induction • 1 Zone",
                    code = "HAWK1",
                    discount = "KSh 12,500",
                    coils = 1,
                    watts = 2200,
                    imageUrl = ""
                )
            )
        }
    }
    var currentOfferIndex by remember { mutableStateOf(0) }

    LaunchedEffect(offers) {
        if (currentOfferIndex >= offers.size) {
            currentOfferIndex = 0
        }
        if (offers.isNotEmpty()) {
            while (true) {
                delay(4000)
                currentOfferIndex = (currentOfferIndex + 1) % offers.size
            }
        }
    }

    // Filter Products based on category and search query
    val filteredProducts = remember(products, selectedCategory, searchQuery) {
        var list = products
        if (selectedCategory == "SPECIAL_OFFERS") {
            list = list.filter { it.isFeatured }
        } else if (selectedCategory != "ALL") {
            list = list.filter { it.category == selectedCategory }
        }
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App top header - Sleek Interface Style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.hawk_logo),
                    contentDescription = "Hawk Life Solutions Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.dp, SleekSlate200, CircleShape)
                )
                Column {
                    Text(
                        text = "HAWK INDUCTIONS",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        color = SleekSlate950
                    )
                    Text(
                        text = "PREMIUM ELECTROMAGNETIC HEATING",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold,
                        color = SleekSlate500,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val user = currentUser
                if (user != null) {
                    val displayName = user.fullName.trim().substringBefore(" ").uppercase()
                    Text(
                        text = displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = SleekSlate950,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            changePwInput = ""
                            changePwConfirmInput = ""
                            settingsError = null
                            settingsMessage = null
                            showSettingsDialog = true
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .background(SleekSlate100, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = SleekSlate950,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { showLogoutDialog = true },
                        modifier = Modifier
                            .size(34.dp)
                            .background(SleekSlate100, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Log Out",
                            tint = SleekSlate950,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onNavigateToAuth,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = SleekSlate950
                        ),
                        border = BorderStroke(1.dp, SleekSlate200),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = SleekSlate950
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Login",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = SleekSlate100)

        // Slideshow Banner - Ultra-sleek luxury showcase
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .height(155.dp)
                .background(Color(0xFF0F172A), RoundedCornerShape(22.dp))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                .drawBehind {
                    drawCircle(
                        color = Color(0xFF38BDF8).copy(alpha = 0.08f),
                        radius = 120.dp.toPx(),
                        center = Offset(size.width - 25.dp.toPx(), size.height / 2)
                    )
                }
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val offer = offers[currentOfferIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 105.dp), // Safe right margin prevents any text overlap with graphic
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Category / Tag Pill
                Surface(
                    color = Color(0xFFE5A93C).copy(alpha = 0.18f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5A93C).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFDE047))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "FEATURED HOB",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                            color = Color(0xFFFDE047),
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Main Title & Subtitle
                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        text = offer.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = offer.subtitle,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Price & Code Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = offer.discount,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color(0xFF4ADE80)
                    )
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = offer.code,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Carousel Dots indicator
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 2.dp, end = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                offers.forEachIndexed { idx, _ ->
                    Box(
                        modifier = Modifier
                            .size(width = if (idx == currentOfferIndex) 16.dp else 5.dp, height = 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (idx == currentOfferIndex) Color(0xFF38BDF8)
                                else Color.White.copy(alpha = 0.25f)
                            )
                    )
                }
            }

            // Cooker Graphic neatly positioned on the right
            InductionCookerGraphic(
                imageUrl = offer.imageUrl,
                title = offer.title,
                watts = offer.watts,
                coils = offer.coils,
                isDark = true,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 4.dp)
                    .width(82.dp)
                    .height(95.dp)
            )
        }

        // Functional Search Bar with search button/icon
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search hobs, sufurias, non-stick pans...", fontSize = 13.sp, color = SleekSlate400) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(24.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search button",
                    tint = SleekSlate600
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = SleekSlate600
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = SleekSlate200,
                focusedContainerColor = SleekSlate50,
                unfocusedContainerColor = SleekSlate50
            )
        )

        // Category Filter Row - Sleek Interface style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val cats = listOf(
                "ALL" to "All Products",
                "SPECIAL_OFFERS" to "🔥 Slideshow Offers",
                "Induction Cookers" to "Induction Cookers",
                "Sufurias & Cookware" to "Sufurias & Cookware",
                "Non-Stick Pans" to "Non-Stick Pans"
            )
            cats.forEach { (key, display) ->
                val isSelected = selectedCategory == key
                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else SleekSlate200,
                            RoundedCornerShape(24.dp)
                        )
                        .background(
                            if (isSelected) Color.Black else SleekSlate50,
                            RoundedCornerShape(24.dp)
                        )
                        .clickable { selectedCategory = key }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = display,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else SleekSlate600
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid List of products - Sleek Interface styling
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "NO COMPATIBLE COOKERS SEEDED",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate500
                )
            }
        } else {
            // FAQ and Tips Overlay dialogue triggers
            var showFaqDialog by remember { mutableStateOf(false) }
            var showTipsDialog by remember { mutableStateOf(false) }

            if (showFaqDialog) {
                AlertDialog(
                    onDismissRequest = { showFaqDialog = false },
                    confirmButton = {
                        Button(
                            onClick = { showFaqDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("DONE", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                        }
                    },
                    title = { Text("HELP & FAQ CENTER", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif) },
                    text = {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            // High contrast Hotline Section
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .border(1.dp, SleekSlate200, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SleekSlate50)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "DIRECT HELPLINE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SleekSlate700,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "For instant order enquiries, deliveries or technical assistance:",
                                        fontSize = 11.sp,
                                        color = SleekSlate600
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Phone",
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "+254 112 660355",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }

                            com.example.ui.components.CookingData.faqs.forEach { faq ->
                                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                    Text(text = "• ${faq.question}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = faq.answer, fontSize = 11.sp, color = SleekSlate600)
                                }
                            }
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
            }

            if (showTipsDialog) {
                AlertDialog(
                    onDismissRequest = { showTipsDialog = false },
                    confirmButton = {
                        Button(
                            onClick = { showTipsDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("DONE", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                        }
                    },
                    title = { Text("INDUCTION PRO TIPS", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.SansSerif) },
                    text = {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            com.example.ui.components.CookingData.tips.forEach { tip ->
                                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text(text = tip.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f))
                                        Text(text = tip.level.uppercase(), fontSize = 8.sp, color = SleekSlate500, fontWeight = FontWeight.Black)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = tip.tip, fontSize = 11.sp, color = SleekSlate600)
                                }
                            }
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(24.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts) { product ->
                    val isCompared = compareList.any { it.id == product.id }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SleekSlate200, RoundedCornerShape(24.dp))
                            .clickable { onNavigateToProduct(product.id) },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SleekSlate50)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            // Top action icons over preview
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Specs summary pill
                                Text(
                                    text = "${product.powerWatts}W",
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color.Black, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )

                                Text(
                                    text = "NEW",
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(Color.Red, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Induction drawing graphic inside slate frame
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SleekSlate100)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                InductionCookerGraphic(
                                    modifier = Modifier.fillMaxSize(),
                                    imageUrl = product.imageUrl,
                                    title = product.title,
                                    watts = product.powerWatts,
                                    coils = product.coilsCount,
                                    isDark = false
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Title, subtitle and price Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                                    Text(
                                        text = product.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SleekSlate950,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                    Text(
                                        text = "${product.coilsCount} Zones • ${product.controlType}",
                                        fontSize = 10.sp,
                                        color = SleekSlate500,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                                Text(
                                    text = "KSh ${String.format("%,.0f", product.price)}",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Black,
                                    color = SleekSlate950
                                )
                            }

                            // Dynamic Star rating based on real reviews
                            val productReviews = remember(allReviews, product.id) { allReviews.filter { it.productId == product.id } }
                            val hasReviews = productReviews.isNotEmpty()
                            val avgRating = if (hasReviews) productReviews.map { it.rating }.average() else 0.0
                            val reviewCount = productReviews.size

                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = if (hasReviews) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Rating",
                                    tint = if (hasReviews) Color.Black else SleekSlate400,
                                    modifier = Modifier.size(11.dp)
                                )
                                Text(
                                    text = if (hasReviews) String.format("%.1f", avgRating) else "No ratings",
                                    fontSize = 10.sp,
                                    fontWeight = if (hasReviews) FontWeight.Bold else FontWeight.Normal,
                                    color = if (hasReviews) Color.Black else SleekSlate500,
                                    fontFamily = FontFamily.SansSerif
                                )
                                if (hasReviews) {
                                    Text(
                                        text = "($reviewCount)",
                                        fontSize = 10.sp,
                                        color = SleekSlate500,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }

                            // CTA add to cart button and compare button row
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.addToCart(product.id, 1, onLoginRequired) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Black,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f).height(36.dp)
                                ) {
                                    Text(
                                        text = "Add to Cart",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.toggleCompare(product) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .border(1.dp, SleekSlate200, RoundedCornerShape(12.dp))
                                        .background(Color.Transparent, RoundedCornerShape(12.dp))
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                                        contentDescription = "Compare",
                                        tint = if (isCompared) Color.Black else SleekSlate400,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Contact Enquiry Card
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 4.dp)
                            .border(1.dp, SleekSlate200, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SleekSlate950)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Contact Enquiry",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "24/7 CUSTOMER SUPPORT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SleekSlate400,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = "For inquiries, call or WhatsApp:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "+254 112 660355",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Append Help/FAQ & Pro Tips Quick Links block at bottom of grid (full span)
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, SleekSlate200, RoundedCornerShape(16.dp))
                                .background(SleekSlate50, RoundedCornerShape(16.dp))
                                .clickable { showFaqDialog = true }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Help,
                                contentDescription = "Help FAQ",
                                tint = SleekSlate600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Help & FAQ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekSlate950,
                                fontFamily = FontFamily.SansSerif
                            )
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, SleekSlate200, RoundedCornerShape(16.dp))
                                .background(SleekSlate50, RoundedCornerShape(16.dp))
                                .clickable { showTipsDialog = true }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Pro Tips",
                                tint = SleekSlate600,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pro Tips",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekSlate950,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSettingsDialog && currentUser != null) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        settingsError = null
                        settingsMessage = null
                        if (changePwInput.isBlank() || changePwConfirmInput.isBlank()) {
                            settingsError = "Please fill in all fields."
                            return@Button
                        }
                        if (changePwInput != changePwConfirmInput) {
                            settingsError = "New passwords do not match."
                            return@Button
                        }
                        // check complexity
                        val hasLetter = changePwInput.any { it.isLetter() }
                        val hasDigit = changePwInput.any { it.isDigit() }
                        if (!hasLetter || !hasDigit || changePwInput.length < 6) {
                            settingsError = "Password must be at least 6 characters and contain both letters and numbers."
                            return@Button
                        }
                        viewModel.changePassword(changePwInput) { success, msg ->
                            if (success) {
                                settingsMessage = msg
                                changePwInput = ""
                                changePwConfirmInput = ""
                            } else {
                                settingsError = msg
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text("Update Password", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSettingsDialog = false },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text("Close", color = SleekSlate500, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Profile & Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        color = SleekSlate950
                    )
                }
            },
            text = {
                val user = currentUser
                if (user != null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Profile Info
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SleekSlate50, RoundedCornerShape(12.dp))
                                .border(1.dp, SleekSlate100, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Name: ${user.fullName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekSlate950,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Email: ${user.email}",
                                    fontSize = 12.sp,
                                    color = SleekSlate600,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (user.isAdmin) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Role: Administrator",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Red,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }
                        }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Willingly Change Password",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekSlate800,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = changePwInput,
                        onValueChange = { 
                            changePwInput = it
                            settingsError = null
                        },
                        placeholder = { Text("Enter New Password", fontSize = 13.sp, color = SleekSlate400) },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = SleekSlate200,
                            focusedContainerColor = SleekSlate50,
                            unfocusedContainerColor = SleekSlate50
                        )
                    )

                    OutlinedTextField(
                        value = changePwConfirmInput,
                        onValueChange = { 
                            changePwConfirmInput = it
                            settingsError = null
                        },
                        placeholder = { Text("Confirm New Password", fontSize = 13.sp, color = SleekSlate400) },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = SleekSlate200,
                            focusedContainerColor = SleekSlate50,
                            unfocusedContainerColor = SleekSlate50
                        )
                    )

                    if (settingsError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = settingsError ?: "",
                            color = Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    if (settingsMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = settingsMessage ?: "",
                            color = Color(0xFF2E7D32), // Dark green
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            modifier = Modifier.border(1.dp, SleekSlate100, RoundedCornerShape(24.dp))
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text("Yes, Log Out", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text("Cancel", color = SleekSlate600, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Confirm Log Out",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    color = SleekSlate950
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to end your session? You will need to log in again to add items to your cart or view past orders.",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = SleekSlate600,
                    lineHeight = 18.sp
                )
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White,
            modifier = Modifier.border(1.dp, SleekSlate100, RoundedCornerShape(24.dp))
        )
    }
}
