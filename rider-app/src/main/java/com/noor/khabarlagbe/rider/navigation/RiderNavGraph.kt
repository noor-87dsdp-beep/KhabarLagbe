package com.noor.khabarlagbe.rider.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noor.khabarlagbe.rider.presentation.auth.RiderAuthViewModel
import com.noor.khabarlagbe.rider.presentation.auth.RiderLoginScreen
import com.noor.khabarlagbe.rider.presentation.auth.RiderRegistrationScreen
import com.noor.khabarlagbe.rider.presentation.delivery.ActiveDeliveryScreen
import com.noor.khabarlagbe.rider.presentation.delivery.DeliveryScreen
import com.noor.khabarlagbe.rider.presentation.earnings.EarningsScreen
import com.noor.khabarlagbe.rider.presentation.history.DeliveryHistoryScreen
import com.noor.khabarlagbe.rider.presentation.home.EnhancedRiderHomeScreen
import com.noor.khabarlagbe.rider.presentation.orders.AvailableOrdersScreen
import com.noor.khabarlagbe.rider.presentation.profile.RiderProfileScreen
import com.noor.khabarlagbe.rider.presentation.stats.StatsScreen

sealed class RiderScreen(val route: String) {
    object Home : RiderScreen("home")
    object Login : RiderScreen("login")
    object Register : RiderScreen("register")
    object AvailableOrders : RiderScreen("available_orders")
    object ActiveDelivery : RiderScreen("active_delivery")
    object OrderDetails : RiderScreen("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }
    object Delivery : RiderScreen("delivery/{orderId}") {
        fun createRoute(orderId: String) = "delivery/$orderId"
    }
    object Earnings : RiderScreen("earnings")
    object History : RiderScreen("history")
    object Stats : RiderScreen("stats")
    object Profile : RiderScreen("profile")
}

@Composable
fun RiderNavGraph() {
    val navController = rememberNavController()
    val authViewModel: RiderAuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)
    
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) RiderScreen.Home.route else RiderScreen.Login.route
    ) {
        // Auth screens
        composable(RiderScreen.Login.route) {
            RiderLoginScreen(navController = navController)
        }
        
        composable(RiderScreen.Register.route) {
            RiderRegistrationScreen(navController = navController)
        }
        
        // Home screen
        composable(RiderScreen.Home.route) {
            EnhancedRiderHomeScreen(navController = navController)
        }
        
        // Available orders
        composable(RiderScreen.AvailableOrders.route) {
            AvailableOrdersScreen(navController = navController)
        }
        
        // Active delivery (no specific order ID)
        composable(RiderScreen.ActiveDelivery.route) {
            ActiveDeliveryScreen(navController = navController)
        }
        
        // Delivery Screen with order ID
        composable(
            route = RiderScreen.Delivery.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            DeliveryScreen(
                orderId = orderId,
                navController = navController
            )
        }
        
        // Earnings
        composable(RiderScreen.Earnings.route) {
            EarningsScreen(navController = navController)
        }
        
        // Delivery history
        composable(RiderScreen.History.route) {
            DeliveryHistoryScreen(navController = navController)
        }
        
        // Stats
        composable(RiderScreen.Stats.route) {
            StatsScreen(navController = navController)
        }
        
        // Profile
        composable(RiderScreen.Profile.route) {
            RiderProfileScreen(navController = navController)
        }
    }
}
