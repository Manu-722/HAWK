package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.StoreViewModel
import com.example.data.Order
import com.example.data.OrderItem
import com.example.data.Product
import com.example.ui.components.InductionCookerGraphic
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: StoreViewModel,
    onNavigateToProduct: (Int) -> Unit,
    onLoginRequired: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val orderHistory by viewModel.orderHistory.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val scrollState = rememberScrollState()

    // Screen tab: CART or ORDERS
    var selectedCartTab by remember { mutableStateOf("CART") }

    // Checkout form state
    var paybillReference by remember { mutableStateOf("") }
    var paybillError by remember { mutableStateOf<String?>(null) }
    var checkoutSuccess by remember { mutableStateOf(false) }
    var generatedReference by remember { mutableStateOf("") }

    // Order expansion detail state
    var expandedOrderId by remember { mutableStateOf<Int?>(null) }
    var expandedOrderItems by remember { mutableStateOf<List<OrderItem>>(emptyList()) }

    LaunchedEffect(expandedOrderId) {
        val targetId = expandedOrderId
        if (targetId != null) {
            expandedOrderItems = viewModel.getOrderItems(targetId)
        } else {
            expandedOrderItems = emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // App top header - Sleek style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Checkout",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = SleekSlate950
                )
                Text(
                    text = "Secure mobile payment gateway",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = SleekSlate500
                )
            }

            // Sub tabs (Cart vs Receipt History) - Sleek Segmented Style
            Row(
                modifier = Modifier
                    .background(SleekSlate50, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                val tabs = listOf("CART" to "Cart", "ORDERS" to "History")
                tabs.forEach { (key, display) ->
                    val isSel = selectedCartTab == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color.Black else Color.Transparent)
                            .clickable { selectedCartTab = key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
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

        if (currentUser == null) {
            // Not signed in state
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
                        Icons.Default.ShoppingCart,
                        contentDescription = "Cart Lock",
                        modifier = Modifier.size(64.dp),
                        tint = SleekSlate300
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sign in Required",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SleekSlate950
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Access your cart and review active order statuses by logging in.",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        color = SleekSlate500,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (selectedCartTab == "CART") {
            if (cartItems.isEmpty() && !checkoutSuccess) {
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
                            Icons.Default.RemoveShoppingCart,
                            contentDescription = "Empty Cart",
                            modifier = Modifier.size(64.dp),
                            tint = SleekSlate200
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Shopping Cart Empty",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = SleekSlate950
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Explore our premium selection of induction cooktops to begin.",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            color = SleekSlate500,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (checkoutSuccess) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(28.dp)
                    ) {
                        // Hawk Logo above Thank You Message
                        Image(
                            painter = painterResource(id = R.drawable.hawk_logo),
                            contentDescription = "Hawk Life Solutions Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.Black, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "THANK YOU FOR YOUR ORDER!",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = SleekSlate950,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "HAWK LIFE SOLUTIONS",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SleekSlate500,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        Text(
                            text = "Your order has been submitted for payment approval and dispatch. Your transaction reference code is:",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = SleekSlate600,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = SleekSlate50,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, Color.Black),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Text(
                                    text = generatedReference,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = Color.Black,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PAYMENT & DISPATCH REFERENCE",
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp,
                                    color = SleekSlate500,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Once your pending charge is approved, your order status will be updated to APPROVED & DISPATCHED.",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20), // Dark green
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                selectedCartTab = "ORDERS"
                                checkoutSuccess = false
                            },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("View Order Status & Receipts", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    // Cart List Header
                    Text(
                        text = "Shopping Cart Items",
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold,
                        color = SleekSlate950,
                        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 10.dp)
                    )

                    cartItems.forEach { (cartItem, product) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InductionCookerGraphic(
                                modifier = Modifier.size(56.dp),
                                imageUrl = product.imageUrl,
                                title = product.title,
                                watts = product.powerWatts,
                                coils = product.coilsCount,
                                isDark = false
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.title,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekSlate950,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "KSh ${String.format("%,.0f", product.price)} each",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    color = SleekSlate500
                                )
                            }

                            // Qty adjusters - Sleek style
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .border(1.dp, SleekSlate200, RoundedCornerShape(12.dp))
                                    .background(SleekSlate50, RoundedCornerShape(12.dp))
                                    .padding(2.dp)
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Reduce",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1) }
                                        .padding(6.dp)
                                )
                                Text(
                                    text = "${cartItem.quantity}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1) }
                                        .padding(6.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.removeFromCart(cartItem.id) },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SleekSlate950, modifier = Modifier.size(20.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SleekSlate100)

                    // Billing & Checkout Instructions
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Billing Summary",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.ExtraBold,
                            color = SleekSlate950,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", fontSize = 13.sp, fontFamily = FontFamily.SansSerif, color = SleekSlate500)
                            Text("KSh ${String.format("%,.0f", cartTotal)}", fontSize = 13.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = SleekSlate950)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Shipping", fontSize = 13.sp, fontFamily = FontFamily.SansSerif, color = SleekSlate500)
                            Text("FREE (Promo)", fontSize = 13.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = SleekSlate950)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = SleekSlate100)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total", fontSize = 15.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.ExtraBold, color = SleekSlate950)
                            Text("KSh ${String.format("%,.0f", cartTotal)}", fontSize = 20.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Black, color = Color.Black)
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Paybill instructions box - Sleek Style
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SleekSlate200, RoundedCornerShape(16.dp))
                                .background(SleekSlate50)
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Secure Paybill Gateway",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = SleekSlate950
                                )
                            }

                            Text(
                                text = "Please complete the payment using our authorized Paybill. Follow these steps on your mobile money device:",
                                fontSize = 13.sp,
                                color = SleekSlate600,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.padding(bottom = 12.dp),
                                lineHeight = 18.sp
                            )

                            // Clean mobile payment guidelines
                            PaymentGuideline(label = "Business Paybill Number", value = "522522")
                            PaymentGuideline(label = "Account Number", value = "7518213")
                            PaymentGuideline(label = "Account Name", value = "Hawk Life Solutions")
                            PaymentGuideline(label = "Exact Amount Due", value = "KSh ${String.format("%,.0f", cartTotal)}")

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val autoRef = "HWK-" + (100000..999999).random().toString()
                                     generatedReference = autoRef
                                    viewModel.checkout(autoRef) { success ->
                                        if (success) {
                                            checkoutSuccess = true
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Complete Checkout", fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // Receipts Order History List
            if (orderHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "No receipts",
                            modifier = Modifier.size(48.dp),
                            tint = SleekSlate300
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Receipts Available",
                            fontSize = 14.sp,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            color = SleekSlate950
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Order Audit Receipts",
                        fontSize = 15.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.ExtraBold,
                        color = SleekSlate950,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

                    orderHistory.forEach { order ->
                        val isExpanded = expandedOrderId == order.id

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp))
                                .background(Color.White, RoundedCornerShape(16.dp))
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
                                        text = sdf.format(Date(order.orderDate)),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        color = SleekSlate500
                                    )
                                }

                                Text(
                                    text = order.status.uppercase(),
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
                                        text = "Payment: ${order.paymentMethod.uppercase()}",
                                        fontSize = 11.sp,
                                        color = SleekSlate500,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                    Text(
                                        text = "Ref: ${order.paybillReference}",
                                        fontSize = 11.sp,
                                        color = SleekSlate950,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "KSh ${String.format("%,.0f", order.totalAmount)}",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Black,
                                    color = SleekSlate950
                                )
                            }

                            // Expand details button - Sleek Style
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        expandedOrderId = if (isExpanded) null else order.id
                                    }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isExpanded) "Collapse Items" else "Show Order Items",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekSlate600
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = SleekSlate600,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Relational mappings list (order items detail expansion)
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    HorizontalDivider(color = SleekSlate100)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    if (expandedOrderItems.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                                        }
                                    } else {
                                        expandedOrderItems.forEach { item ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${item.quantity}x ${item.productTitle}",
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SleekSlate800,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    text = "KSh ${String.format("%,.0f", item.price * item.quantity)}",
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    color = SleekSlate950,
                                                    fontWeight = FontWeight.Medium
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
    }
}

@Composable
fun PaymentGuideline(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 11.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, color = SleekSlate500)
        Text(text = value, fontSize = 14.sp, fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = SleekSlate950)
    }
}
