package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.StoreViewModel
import com.example.data.Product
import com.example.data.Review
import com.example.ui.components.CookingData
import com.example.ui.components.InductionCookerGraphic
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    productId: Int,
    viewModel: StoreViewModel,
    onBack: () -> Unit,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val product = remember(products, productId) { products.find { it.id == productId } }
    val currentUser by viewModel.currentUser.collectAsState()

    val scrollState = rememberScrollState()

    // Tab state at bottom (Specs, FAQs, Cooking Tips, Reviews)
    var selectedBottomTab by remember { mutableStateOf("SPECS") }

    if (product == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Induction cooker not found.", fontFamily = FontFamily.Monospace)
        }
        return
    }

    // Reviews flow
    val reviews by viewModel.getReviewsForProduct(productId).collectAsState(initial = emptyList())

    // Review form state
    var reviewRating by remember { mutableStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App top header - Sleek style
        TopAppBar(
            title = {
                Text(
                    text = "Product Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = (-0.5).sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SleekSlate950)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = SleekSlate950,
                navigationIconContentColor = SleekSlate950
            )
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            // Main induction design graphic hero - Sleek style
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(SleekSlate50)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                InductionCookerGraphic(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp),
                    imageUrl = product.imageUrl,
                    title = product.title,
                    watts = product.powerWatts,
                    coils = product.coilsCount,
                    isDark = false
                )
            }

            HorizontalDivider(color = SleekSlate100)

            // Title & Buy Block - Sleek style
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.title,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = SleekSlate950
                )

                // Dynamic rating row under title from actual user/admin reviews
                val avgRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
                val reviewCount = reviews.size
                val fullStars = kotlin.math.round(avgRating).toInt()

                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(5) { starIndex ->
                        val isFilled = reviewCount > 0 && starIndex < fullStars
                        Icon(
                            imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star",
                            tint = if (isFilled) Color.Black else SleekSlate300,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (reviewCount > 0) String.format("%.1f (%d review%s)", avgRating, reviewCount, if (reviewCount == 1) "" else "s") else "No reviews yet",
                        fontSize = 12.sp,
                        color = SleekSlate500,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "KSh ${String.format("%,.0f", product.price)}",
                        fontSize = 26.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        color = SleekSlate950
                    )

                    Button(
                        onClick = { viewModel.addToCart(product.id, 1, onLoginRequired) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold, fontFamily = FontFamily.SansSerif)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = SleekSlate600,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))

            // Dynamic Tab Controls - Sleek Segmented Style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SleekSlate50)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    "SPECS" to "Specs",
                    "FAQs" to "FAQ",
                    "COOKING" to "Pro Tips",
                    "REVIEWS" to "Reviews (${reviews.size})"
                )
                tabs.forEach { (key, display) ->
                    val isSelected = selectedBottomTab == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.Black else Color.Transparent)
                            .clickable { selectedBottomTab = key }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = display,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else SleekSlate500
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))

            // Tab contents
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (selectedBottomTab) {
                    "SPECS" -> {
                        Column {
                            SpecItem(label = "Maximum Power", value = "${product.powerWatts} Watts")
                            SpecItem(label = "Cooking Zones", value = "${product.coilsCount} Zones")
                            SpecItem(label = "Interface Type", value = product.controlType)
                            SpecItem(label = "Warranty Coverage", value = "${product.warrantyMonths} Months")
                            SpecItem(label = "Safety Systems", value = product.safetyFeatures)
                        }
                    }

                    "FAQs" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            CookingData.faqs.forEach { faq ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SleekSlate50)
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = faq.question,
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekSlate950
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = faq.answer,
                                            fontSize = 13.sp,
                                            color = SleekSlate600,
                                            lineHeight = 18.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "COOKING" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CookingData.tips.forEach { tip ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp))
                                        .background(Color.White, RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = tip.title,
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekSlate950
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .background(SleekSlate100, RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = tip.level,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = SleekSlate950
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = tip.tip,
                                            fontSize = 13.sp,
                                            color = SleekSlate600,
                                            lineHeight = 18.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "REVIEWS" -> {
                        Column {
                            // Review submission block (Only if logged in)
                            if (currentUser != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, SleekSlate200, RoundedCornerShape(16.dp))
                                        .background(Color.White, RoundedCornerShape(16.dp))
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Add Review",
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekSlate950,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )

                                    // Stars Rating row
                                    Row(
                                        modifier = Modifier.padding(bottom = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Rating: ",
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.Medium,
                                            color = SleekSlate600
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        (1..5).forEach { r ->
                                            val isSelected = r <= reviewRating
                                            Icon(
                                                imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "$r Stars",
                                                tint = Color.Black,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clickable { reviewRating = r }
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = reviewComment,
                                        onValueChange = { reviewComment = it },
                                        placeholder = { Text("Write your honest review here...", fontSize = 13.sp, color = SleekSlate400) },
                                        singleLine = false,
                                        maxLines = 3,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Black,
                                            unfocusedBorderColor = SleekSlate200,
                                            focusedContainerColor = SleekSlate50,
                                            unfocusedContainerColor = SleekSlate50
                                        )
                                    )

                                    Button(
                                        onClick = {
                                            if (reviewComment.isNotBlank()) {
                                                viewModel.submitReview(product.id, reviewRating, reviewComment)
                                                reviewComment = ""
                                                reviewRating = 5
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Black,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Submit Review", fontSize = 12.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SleekSlate50, RoundedCornerShape(16.dp))
                                        .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp))
                                        .clickable { onLoginRequired() }
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Sign in to post reviews",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            if (reviews.isEmpty()) {
                                Text(
                                    text = "No reviews posted yet. Be the first to review this product!",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = SleekSlate400,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                            } else {
                                reviews.forEach { r ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp)
                                            .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp))
                                            .background(Color.White, RoundedCornerShape(16.dp))
                                            .padding(16.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = r.userName,
                                                fontSize = 13.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontWeight = FontWeight.Bold,
                                                color = SleekSlate950
                                            )
                                            Row {
                                                repeat(5) { starIndex ->
                                                    Icon(
                                                        imageVector = if (starIndex < r.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                        contentDescription = null,
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = r.comment,
                                            fontSize = 13.sp,
                                            color = SleekSlate600,
                                            lineHeight = 18.sp,
                                            fontFamily = FontFamily.SansSerif
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
}

@Composable
fun SpecItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                color = SleekSlate500
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                color = SleekSlate950,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = SleekSlate100)
    }
}
