package com.noor.khabarlagbe.restaurant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noor.khabarlagbe.restaurant.presentation.auth.RestaurantAuthViewModel
import com.noor.khabarlagbe.restaurant.presentation.auth.RestaurantLoginScreen
import com.noor.khabarlagbe.restaurant.presentation.auth.RestaurantRegistrationScreen
import com.noor.khabarlagbe.restaurant.presentation.dashboard.RestaurantDashboardScreen
import com.noor.khabarlagbe.restaurant.presentation.menu.AddEditMenuItemScreen
import com.noor.khabarlagbe.restaurant.presentation.menu.CategoryManagementScreen
import com.noor.khabarlagbe.restaurant.presentation.menu.MenuManagementScreen
import com.noor.khabarlagbe.restaurant.presentation.orders.OrderDetailScreen
import com.noor.khabarlagbe.restaurant.presentation.orders.OrdersScreen
import com.noor.khabarlagbe.restaurant.presentation.reports.ReportsScreen
import com.noor.khabarlagbe.restaurant.presentation.reviews.ReviewsScreen
import com.noor.khabarlagbe.restaurant.presentation.settings.RestaurantSettingsScreen

sealed class RestaurantScreen(val route: String) {
    object Login : RestaurantScreen("login")
    object Register : RestaurantScreen("register")
    object Dashboard : RestaurantScreen("dashboard")
    object Orders : RestaurantScreen("orders")
    object OrderDetails : RestaurantScreen("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }
    object Menu : RestaurantScreen("menu")
    object AddEditItem : RestaurantScreen("add_edit_item/{itemId}") {
        fun createRoute(itemId: String?) = if (itemId != null) "add_edit_item/$itemId" else "add_edit_item/new"
    }
    object CategoryManagement : RestaurantScreen("category_management")
    object Reports : RestaurantScreen("reports")
    object Reviews : RestaurantScreen("reviews")
    object Settings : RestaurantScreen("settings")
}

@Composable
fun RestaurantNavGraph() {
    val navController = rememberNavController()
    val authViewModel: RestaurantAuthViewModel = hiltViewModel()
    
    val startDestination = if (authViewModel.isLoggedIn()) {
        RestaurantScreen.Dashboard.route
    } else {
        RestaurantScreen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(RestaurantScreen.Login.route) {
            RestaurantLoginScreen(
                onLoginSuccess = {
                    navController.navigate(RestaurantScreen.Dashboard.route) {
                        popUpTo(RestaurantScreen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(RestaurantScreen.Register.route)
                }
            )
        }
        
        composable(RestaurantScreen.Register.route) {
            RestaurantRegistrationScreen(
                onRegistrationSuccess = {
                    navController.navigate(RestaurantScreen.Dashboard.route) {
                        popUpTo(RestaurantScreen.Login.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(RestaurantScreen.Dashboard.route) {
            RestaurantDashboardScreen(
                onNavigateToOrders = {
                    navController.navigate(RestaurantScreen.Orders.route)
                },
                onNavigateToMenu = {
                    navController.navigate(RestaurantScreen.Menu.route)
                },
                onNavigateToReports = {
                    navController.navigate(RestaurantScreen.Reports.route)
                },
                onNavigateToSettings = {
                    navController.navigate(RestaurantScreen.Settings.route)
                },
                onOrderClick = { orderId ->
                    navController.navigate(RestaurantScreen.OrderDetails.createRoute(orderId))
                }
            )
        }
        
        composable(RestaurantScreen.Orders.route) {
            OrdersScreen(
                onOrderClick = { orderId ->
                    navController.navigate(RestaurantScreen.OrderDetails.createRoute(orderId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = RestaurantScreen.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            OrderDetailScreen(
                orderId = orderId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(RestaurantScreen.Menu.route) {
            MenuManagementScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddItem = {
                    navController.navigate(RestaurantScreen.AddEditItem.createRoute(null))
                },
                onEditItem = { itemId ->
                    navController.navigate(RestaurantScreen.AddEditItem.createRoute(itemId))
                },
                onManageCategories = {
                    navController.navigate(RestaurantScreen.CategoryManagement.route)
                }
            )
        }
        
        composable(
            route = RestaurantScreen.AddEditItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            AddEditMenuItemScreen(
                itemId = if (itemId == "new") null else itemId,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(RestaurantScreen.CategoryManagement.route) {
            CategoryManagementScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(RestaurantScreen.Reports.route) {
            ReportsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(RestaurantScreen.Reviews.route) {
            ReviewsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(RestaurantScreen.Settings.route) {
            RestaurantSettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(RestaurantScreen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
