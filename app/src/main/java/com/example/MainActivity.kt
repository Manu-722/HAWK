package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.*
import androidx.compose.foundation.border
import androidx.compose.foundation.background

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    val navController = rememberNavController()
    val viewModel: StoreViewModel = viewModel()
    
    // Track current route for bottom bar visibility
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Retrieve active cart items to show badge count
    val cartItems by viewModel.cartItems.collectAsState()
    val cartCount = cartItems.sumOf { it.first.quantity }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only show bottom navigation on core storefront screens
            if (currentRoute == "home" || currentRoute == "compare" || currentRoute == "cart") {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier.border(1.dp, SleekSlate100)
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = {
                            if (currentRoute != "home") {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = false }
                                }
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Explorer") },
                        label = {
                            Text(
                                text = "Explorer",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            indicatorColor = SleekSlate200,
                            unselectedIconColor = SleekSlate400,
                            unselectedTextColor = SleekSlate400
                        )
                    )

                    NavigationBarItem(
                        selected = currentRoute == "compare",
                        onClick = {
                            if (currentRoute != "compare") {
                                navController.navigate("compare")
                            }
                        },
                        icon = { Icon(Icons.Default.CompareArrows, contentDescription = "Compare") },
                        label = {
                            Text(
                                text = "Compare",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            indicatorColor = SleekSlate200,
                            unselectedIconColor = SleekSlate400,
                            unselectedTextColor = SleekSlate400
                        )
                    )

                    NavigationBarItem(
                        selected = currentRoute == "cart",
                        onClick = {
                            if (currentRoute != "cart") {
                                navController.navigate("cart")
                            }
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(
                                            containerColor = Color.Black,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = cartCount.toString(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                            }
                        },
                        label = {
                            Text(
                                text = "Cart",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = Color.Black,
                            indicatorColor = SleekSlate200,
                            unselectedIconColor = SleekSlate400,
                            unselectedTextColor = SleekSlate400
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { productId ->
                        navController.navigate("details/$productId")
                    },
                    onNavigateToAdmin = {
                        navController.navigate("admin")
                    },
                    onNavigateToAuth = {
                        navController.navigate("auth")
                    },
                    onLoginRequired = {
                        navController.navigate("auth")
                    }
                )
            }

            composable(
                route = "details/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                DetailsScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onLoginRequired = {
                        navController.navigate("auth")
                    }
                )
            }

            composable("cart") {
                CartScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { productId ->
                        navController.navigate("details/$productId")
                    },
                    onLoginRequired = {
                        navController.navigate("auth")
                    }
                )
            }

            composable("compare") {
                CompareScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { productId ->
                        navController.navigate("details/$productId")
                    },
                    onLoginRequired = {
                        navController.navigate("auth")
                    }
                )
            }

            composable("admin") {
                AdminScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("auth") {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
