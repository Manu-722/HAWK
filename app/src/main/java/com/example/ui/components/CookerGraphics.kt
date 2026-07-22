package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

// Data Models for static educational info
data class FAQItem(val question: String, val answer: String)
data class CookingTip(val title: String, val tip: String, val level: String)

object CookingData {
    val faqs = listOf(
        FAQItem(
            "How does induction cooking work?",
            "Induction cookers use electromagnets to generate a magnetic field. This field induces electric currents directly in the magnetic pan itself, heating the cookware directly rather than the glass top."
        ),
        FAQItem(
            "What kind of cookware is compatible?",
            "Cookware must contain ferromagnetic iron. Cast iron, carbon steel, and magnetic stainless steel work perfectly. Test your pan by holding a magnet to the bottom—if it sticks, it works!"
        ),
        FAQItem(
            "Is induction cooking safer?",
            "Yes, significantly safer. There is no open flame or exposed red-hot heating element. Since only the cookware heats up, the ceramic glass remains relatively cool, preventing accidental burns."
        ),
        FAQItem(
            "Why is there a buzzing sound?",
            "A slight hum or buzzing is normal, especially at high power. It is caused by the magnetic vibration of multi-layer pan metals reacting to the high-frequency electromagnetic field."
        ),
        FAQItem(
            "How do I clean an induction cooktop?",
            "Simply wipe with a damp microfiber cloth. Because the cooktop remains cool, spilled food does not bake onto the glass surface, making cleanup incredibly fast and simple."
        )
    )

    val tips = listOf(
        CookingTip(
            "Instant Heat Management",
            "Induction changes temperature instantly. If a pot is about to boil over, don't lift it—simply lower the power scale. The boiling stops in under a split second.",
            "Beginner"
        ),
        CookingTip(
            "Beware of Instant Searing",
            "Since induction preheats in seconds, never leave an empty pan on high. It can quickly overheat non-stick coatings or permanently warp thin cookware.",
            "Intermediate"
        ),
        CookingTip(
            "Centering the Cookware",
            "Always center your pan exactly over the circular ring. Proper centering ensures even heat distribution across the bottom and prevents uneven pan wear.",
            "Beginner"
        ),
        CookingTip(
            "The Power Boost Feature",
            "Use the Power Boost ('P') mode exclusively for boiling water. Avoid using it to preheat oil or butter, as they can smoke or burn in a matter of seconds.",
            "Professional"
        ),
        CookingTip(
            "Simmering with Precision",
            "Induction is famous for its low-temp stability. Use low settings (stages 1-3) for melting chocolate without a double boiler, or holding delicate sauces for hours.",
            "Professional"
        )
    )
}

@Composable
fun InductionCookerGraphic(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
    watts: Int,
    coils: Int,
    isDark: Boolean = true
) {
    // If the image URL starts with http, render it via Coil. Otherwise, draw our beautiful custom vector cooker!
    if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://") || imageUrl.startsWith("content://")) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Black else OffWhite)
                .border(1.dp, if (isDark) White.copy(0.15f) else Black.copy(0.15f), RoundedCornerShape(16.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    } else {
        // Draw custom premium induction cooker graphics using Jetpack Compose!
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Black else OffWhite)
                .border(2.dp, if (isDark) White else Black, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Brand label
                Text(
                    text = "A U R A",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) White.copy(0.5f) else Black.copy(0.5f),
                        letterSpacing = 4.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Cooktop Circles drawing
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val lineColor = if (isDark) White else Black
                    val secondaryLineColor = if (isDark) White.copy(0.3f) else Black.copy(0.3f)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        
                        if (coils == 1) {
                            // Single Zone
                            val radiusOuter = size.width.coerceAtMost(size.height) * 0.38f
                            val radiusInner = radiusOuter * 0.6f
                            
                            // Outer solid ring
                            drawCircle(
                                color = lineColor,
                                radius = radiusOuter,
                                center = center,
                                style = Stroke(width = 3.dp.toPx())
                            )
                            // Inner dashed ring
                            drawCircle(
                                color = lineColor,
                                radius = radiusInner,
                                center = center,
                                style = Stroke(
                                    width = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            )
                            // Core pan-sensor guides
                            drawCircle(
                                color = secondaryLineColor,
                                radius = radiusInner * 0.4f,
                                center = center,
                                style = Stroke(width = 1.dp.toPx())
                            )
                            // Four crosshair ticks
                            drawLine(
                                color = lineColor,
                                start = Offset(center.x - radiusOuter * 1.1f, center.y),
                                end = Offset(center.x - radiusOuter * 0.9f, center.y),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = lineColor,
                                start = Offset(center.x + radiusOuter * 0.9f, center.y),
                                end = Offset(center.x + radiusOuter * 1.1f, center.y),
                                strokeWidth = 2.dp.toPx()
                            )
                        } else if (coils == 2) {
                            // Double Zone (vertical or horizontal stack based on aspect ratio)
                            val pad = size.height * 0.1f
                            val radius = size.height * 0.18f
                            
                            // Zone 1 (Top)
                            val center1 = Offset(size.width / 2, size.height * 0.3f)
                            drawCircle(
                                color = lineColor,
                                radius = radius,
                                center = center1,
                                style = Stroke(width = 2.5.dp.toPx())
                            )
                            drawCircle(
                                color = secondaryLineColor,
                                radius = radius * 0.6f,
                                center = center1,
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )
                            )
                            
                            // Zone 2 (Bottom)
                            val center2 = Offset(size.width / 2, size.height * 0.7f)
                            drawCircle(
                                color = lineColor,
                                radius = radius * 0.8f,
                                center = center2,
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawCircle(
                                color = secondaryLineColor,
                                radius = radius * 0.48f,
                                center = center2,
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )
                            )
                        } else {
                            // Quad Zone / Flex Zone (Draw 4 corners grid)
                            val r = size.width.coerceAtMost(size.height) * 0.16f
                            val gapX = size.width * 0.25f
                            val gapY = size.height * 0.22f
                            
                            val centers = listOf(
                                Offset(size.width / 2 - gapX, size.height / 2 - gapY),
                                Offset(size.width / 2 + gapX, size.height / 2 - gapY),
                                Offset(size.width / 2 - gapX, size.height / 2 + gapY),
                                Offset(size.width / 2 + gapX, size.height / 2 + gapY)
                            )
                            
                            centers.forEach { c ->
                                drawCircle(
                                    color = lineColor,
                                    radius = r,
                                    center = c,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                                drawCircle(
                                    color = secondaryLineColor,
                                    radius = r * 0.6f,
                                    center = c,
                                    style = Stroke(
                                        width = 1.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                                    )
                                )
                            }
                        }
                    }
                }

                // Control panel block at the bottom
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (isDark) White.copy(0.08f) else Black.copy(0.04f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isDark) White.copy(0.2f) else Black.copy(0.15f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Touch controls and indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ON/OFF",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) White else Black,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    if (isDark) White else Black,
                                    RoundedCornerShape(3.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        
                        // Small digital LED indicator
                        Text(
                            text = "[${watts}W]",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) White else Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .background(
                                    color = if (isDark) Black else White,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .border(
                                    0.5.dp,
                                    if (isDark) White.copy(0.4f) else Black.copy(0.4f),
                                    RoundedCornerShape(2.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )

                        Text(
                            text = if (coils > 1) "DUAL" else "LOCK",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) White.copy(0.7f) else Black.copy(0.7f),
                        )
                    }
                }
            }
        }
    }
}
