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
import com.noor.khabarlagbe.presentation.auth.otp.OTPVerificationScreen
import com.noor.khabarlagbe.presentation.splash.SplashScreen
import com.noor.khabarlagbe.presentation.home.HomeScreen
import com.noor.khabarlagbe.presentation.restaurant.RestaurantDetailsScreen
import com.noor.khabarlagbe.presentation.cart.CartScreen
import com.noor.khabarlagbe.presentation.checkout.CheckoutScreen
import com.noor.khabarlagbe.presentation.checkout.payment.PaymentMethodScreen
import com.noor.khabarlagbe.presentation.checkout.address.AddressSelectionScreen
import com.noor.khabarlagbe.presentation.checkout.address.AddAddressScreen
import com.noor.khabarlagbe.presentation.order.tracking.OrderTrackingScreen
import com.noor.khabarlagbe.presentation.order.history.OrderHistoryScreen
import com.noor.khabarlagbe.presentation.order.details.OrderDetailsScreen
import com.noor.khabarlagbe.presentation.profile.ProfileScreen
import com.noor.khabarlagbe.presentation.profile.edit.EditProfileScreen
import com.noor.khabarlagbe.presentation.profile.addresses.AddressManagementScreen
import com.noor.khabarlagbe.presentation.profile.favorites.FavoritesScreen
import com.noor.khabarlagbe.presentation.search.SearchScreen
import com.noor.khabarlagbe.presentation.wallet.WalletScreen
import com.noor.khabarlagbe.presentation.wallet.AddMoneyScreen
import com.noor.khabarlagbe.presentation.wallet.TransactionHistoryScreen
import com.noor.khabarlagbe.presentation.social.GroupOrderScreen
import com.noor.khabarlagbe.presentation.social.ReferralScreen
import com.noor.khabarlagbe.presentation.social.FriendsFeedScreen
import com.noor.khabarlagbe.presentation.social.ShareOrderScreen

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
            route = Screen.OTPVerification.route,
            arguments = listOf(
                navArgument("phoneNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            OTPVerificationScreen(
                phoneNumber = phoneNumber,
                navController = navController
            )
        }
        
        // Home
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        // Search
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
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
        
        composable(Screen.PaymentMethod.route) {
            PaymentMethodScreen(navController = navController)
        }
        
        composable(Screen.AddressSelection.route) {
            AddressSelectionScreen(navController = navController)
        }
        
        composable(Screen.AddAddress.route) {
            AddAddressScreen(navController = navController)
        }
        
        composable(
            route = Screen.EditAddress.route,
            arguments = listOf(
                navArgument("addressId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val addressId = backStackEntry.arguments?.getString("addressId")
            AddAddressScreen(
                navController = navController,
                addressId = addressId
            )
        }
        
        // Order
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
            OrderDetailsScreen(
                orderId = orderId,
                navController = navController
            )
        }
        
        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController = navController)
        }
        
        composable(Screen.SavedAddresses.route) {
            AddressManagementScreen(navController = navController)
        }
        
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }
        
        // Wallet
        composable(Screen.Wallet.route) {
            WalletScreen(navController = navController)
        }
        
        composable(Screen.AddMoney.route) {
            AddMoneyScreen(navController = navController)
        }
        
        composable(Screen.TransactionHistory.route) {
            TransactionHistoryScreen(navController = navController)
        }
        
        // Social
        composable(Screen.GroupOrder.route) {
            GroupOrderScreen(navController = navController)
        }
        
        composable(Screen.Referral.route) {
            ReferralScreen(navController = navController)
        }
        
        composable(Screen.FriendsFeed.route) {
            FriendsFeedScreen(navController = navController)
        }
        
        composable(
            route = Screen.ShareOrder.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")
            ShareOrderScreen(
                navController = navController,
                orderId = orderId
            )
        }
    }
}
