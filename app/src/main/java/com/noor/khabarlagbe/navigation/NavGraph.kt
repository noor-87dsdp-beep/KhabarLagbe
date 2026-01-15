package com.noor.khabarlagbe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.noor.khabarlagbe.presentation.auth.login.LoginScreen
import com.noor.khabarlagbe.presentation.auth.otp.OTPVerificationScreen
import com.noor.khabarlagbe.presentation.auth.register.RegisterScreen
import com.noor.khabarlagbe.presentation.cart.CartScreen
import com.noor.khabarlagbe.presentation.checkout.AddressSelectionScreen
import com.noor.khabarlagbe.presentation.checkout.CheckoutScreen
import com.noor.khabarlagbe.presentation.checkout.PaymentMethodScreen
import com.noor.khabarlagbe.presentation.home.HomeScreen
import com.noor.khabarlagbe.presentation.order.details.OrderDetailsScreen
import com.noor.khabarlagbe.presentation.order.history.OrderHistoryScreen
import com.noor.khabarlagbe.presentation.order.tracking.OrderTrackingScreen
import com.noor.khabarlagbe.presentation.profile.ProfileScreen
import com.noor.khabarlagbe.presentation.profile.addresses.AddAddressScreen
import com.noor.khabarlagbe.presentation.profile.addresses.AddressManagementScreen
import com.noor.khabarlagbe.presentation.profile.edit.EditProfileScreen
import com.noor.khabarlagbe.presentation.profile.favorites.FavoritesScreen
import com.noor.khabarlagbe.presentation.restaurant.RestaurantDetailsScreen
import com.noor.khabarlagbe.presentation.search.SearchScreen
import com.noor.khabarlagbe.presentation.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Splash
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        
        composable(
            route = Screen.OTP.route,
            arguments = listOf(
                navArgument("phoneNumber") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            OTPVerificationScreen(
                phoneNumber = phoneNumber,
                navController = navController
            )
        }
        
        // Main
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
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
        
        // Checkout
        composable(Screen.Checkout.route) {
            CheckoutScreen(navController = navController)
        }
        
        composable(Screen.AddressSelection.route) {
            AddressSelectionScreen(navController = navController)
        }
        
        composable(Screen.PaymentMethod.route) {
            PaymentMethodScreen(navController = navController)
        }
        
        composable(Screen.AddAddress.route) {
            AddAddressScreen(navController = navController)
        }
        
        // Orders
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
        
        composable(Screen.OrderHistory.route) {
            OrderHistoryScreen(navController = navController)
        }
        
        composable(
            route = Screen.OrderDetails.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            // Pass orderId to ViewModel via Hilt SavedStateHandle
            OrderDetailsScreen(navController = navController)
        }
        
        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }
        
        composable(Screen.SavedAddresses.route) {
            AddressManagementScreen(navController = navController)
        }
    }
}
