package com.noor.khabarlagbe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.noor.khabarlagbe.presentation.auth.login.LoginScreen
import com.noor.khabarlagbe.presentation.auth.register.RegisterScreen
import com.noor.khabarlagbe.presentation.home.HomeScreen
import com.noor.khabarlagbe.presentation.restaurant.RestaurantDetailsScreen
import com.noor.khabarlagbe.presentation.cart.CartScreen
import com.noor.khabarlagbe.presentation.checkout.CheckoutScreen
import com.noor.khabarlagbe.presentation.order.tracking.OrderTrackingScreen
import com.noor.khabarlagbe.presentation.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        
        // Home
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        // Restaurant
        composable(
            route = Screen.RestaurantDetails.route,
            arguments = listOf(
                navArgument("restaurantId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: return@composable
            RestaurantDetailsScreen(
                restaurantId = restaurantId,
                navController = navController
            )
        }
        
        // Cart
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }
        
        // Checkout
        composable(Screen.Checkout.route) {
            CheckoutScreen(navController = navController)
        }
        
        // Order Tracking
        composable(
            route = Screen.OrderTracking.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            OrderTrackingScreen(
                orderId = orderId,
                navController = navController
            )
        }
        
        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
