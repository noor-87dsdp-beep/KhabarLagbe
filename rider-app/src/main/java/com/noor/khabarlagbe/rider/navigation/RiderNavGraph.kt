package com.noor.khabarlagbe.rider.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noor.khabarlagbe.rider.presentation.delivery.DeliveryScreen
import com.noor.khabarlagbe.rider.presentation.home.RiderHomeScreen

sealed class RiderScreen(val route: String) {
    object Home : RiderScreen("home")
    object Login : RiderScreen("login")
    object Register : RiderScreen("register")
    object OrderDetails : RiderScreen("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }
    object Delivery : RiderScreen("delivery/{orderId}") {
        fun createRoute(orderId: String) = "delivery/$orderId"
    }
    object Navigation : RiderScreen("navigation/{orderId}") {
        fun createRoute(orderId: String) = "navigation/$orderId"
    }
    object Earnings : RiderScreen("earnings")
    object Profile : RiderScreen("profile")
}

@Composable
fun RiderNavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = RiderScreen.Home.route
    ) {
        composable(RiderScreen.Home.route) {
            RiderHomeScreen(navController = navController)
        }
        
        // Delivery Screen with Mapbox navigation
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
        
        // Add other screens as they are implemented
    }
}
