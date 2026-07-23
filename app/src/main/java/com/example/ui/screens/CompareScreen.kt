package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.Product
import com.example.ui.components.InductionCookerGraphic
import com.example.ui.theme.*

@Composable
fun CompareScreen(
    viewModel: StoreViewModel,
    onNavigateToProduct: (Int) -> Unit,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier
) {
    val compareList by viewModel.compareList.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {
        // Title block - Sleek Style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Compare",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = SleekSlate950
                )
                Text(
                    text = "Side-by-side technical specs",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = SleekSlate500
                )
            }

            if (compareList.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearCompare() },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Text("Clear All", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        if (compareList.isEmpty()) {
            // Elegant empty state - Sleek Style
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.CompareArrows,
                        contentDescription = "Compare empty",
                        modifier = Modifier.size(64.dp),
                        tint = SleekSlate300
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Comparison Deck Empty",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SleekSlate950,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Browse induction systems in explorer and click compare on items to perform side-by-side technical specs audit.",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = SleekSlate500,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Header Row (Images & titles) - Sleek Style
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    compareList.forEach { product ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(210.dp)
                                .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Close button top-right
                                IconButton(
                                    onClick = { viewModel.toggleCompare(product) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(28.dp)
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = SleekSlate500,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Custom visual cooker graphic
                                    InductionCookerGraphic(
                                        modifier = Modifier
                                            .size(75.dp)
                                            .padding(bottom = 4.dp),
                                        imageUrl = product.imageUrl,
                                        title = product.title,
                                        watts = product.powerWatts,
                                        coils = product.coilsCount,
                                        isDark = false
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = product.title,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekSlate950,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "KSh ${String.format("%,.0f", product.price)}",
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.SansSerif,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SleekSlate700,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.addToCart(product.id, 1, onLoginRequired)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Black,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add", fontSize = 9.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    // If less than 3, add virtual empty spaces to keep layout consistent
                    repeat(3 - compareList.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                HorizontalDivider(color = SleekSlate100, thickness = 1.dp)

                // Comparison Specs Specs Blocks
                CompareRow(title = "Maximum Power", values = compareList.map { "${it.powerWatts} Watts" })
                CompareRow(title = "Cooking Coils", values = compareList.map { "${it.coilsCount} Zones" })
                CompareRow(title = "Control Interface", values = compareList.map { it.controlType })
                CompareRow(title = "Warranty Period", values = compareList.map { "${it.warrantyMonths} Months" })
                CompareRow(title = "Safety Features", values = compareList.map { it.safetyFeatures })
                CompareRow(title = "Glass Surface", values = compareList.map { "Microcrystalline Glass" })
            }
        }
    }
}

@Composable
fun CompareRow(
    title: String,
    values: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            color = SleekSlate500,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEach { valStr ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekSlate50)
                        .border(1.dp, SleekSlate100, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = valStr,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = SleekSlate950,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            // Repeat spacer if there are missing slots to align grids perfectly
            repeat(3 - values.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = SleekSlate100)
    }
}
