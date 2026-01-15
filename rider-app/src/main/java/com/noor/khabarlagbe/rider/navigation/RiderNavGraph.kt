package com.noor.khabarlagbe.rider.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.noor.khabarlagbe.rider.presentation.auth.RiderLoginScreen
import com.noor.khabarlagbe.rider.presentation.auth.RiderRegistrationScreen
import com.noor.khabarlagbe.rider.presentation.delivery.ActiveDeliveryScreen
import com.noor.khabarlagbe.rider.presentation.home.RiderHomeScreen
import com.noor.khabarlagbe.rider.presentation.orders.AvailableOrdersScreen

sealed class RiderScreen(val route: String) {
    object Splash : RiderScreen("splash")
    object Login : RiderScreen("login")
    object Register : RiderScreen("register")
    object Home : RiderScreen("home")
    object AvailableOrders : RiderScreen("available_orders")
    object ActiveDelivery : RiderScreen("active_delivery/{orderId}") {
        fun createRoute(orderId: String) = "active_delivery/$orderId"
    }
    object Earnings : RiderScreen("earnings")
    object History : RiderScreen("history")
    object Stats : RiderScreen("stats")
    object Profile : RiderScreen("profile")
    object EditProfile : RiderScreen("edit_profile")
    object VehicleDetails : RiderScreen("vehicle_details")
    object BankDetails : RiderScreen("bank_details")
    object Documents : RiderScreen("documents")
}

@Composable
fun RiderNavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = RiderScreen.Login.route
    ) {
        composable(RiderScreen.Login.route) {
            RiderLoginScreen(navController = navController)
        }
        
        composable(RiderScreen.Register.route) {
            RiderRegistrationScreen(navController = navController)
        }
        
        composable(RiderScreen.Home.route) {
            RiderHomeScreen(navController = navController)
        }
        
        composable(RiderScreen.AvailableOrders.route) {
            AvailableOrdersScreen(navController = navController)
        }
        
        composable(
            route = RiderScreen.ActiveDelivery.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) {
            ActiveDeliveryScreen(navController = navController)
        }
        
        // Additional screens can be added here as they are implemented
    }
}
