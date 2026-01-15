package com.noor.khabarlagbe.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object OTP : Screen("otp")
    
    // Main
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Cart : Screen("cart")
    data object Profile : Screen("profile")
    
    // Restaurant
    data object RestaurantDetails : Screen("restaurant/{restaurantId}") {
        fun createRoute(restaurantId: String) = "restaurant/$restaurantId"
    }
    
    data object MenuItemDetail : Screen("menu_item/{itemId}") {
        fun createRoute(itemId: String) = "menu_item/$itemId"
    }
    
    // Checkout
    data object Checkout : Screen("checkout")
    data object AddressSelection : Screen("address_selection")
    data object PaymentMethod : Screen("payment_method")
    data object AddAddress : Screen("add_address")
    
    // Order
    data object OrderTracking : Screen("order/{orderId}/tracking") {
        fun createRoute(orderId: String) = "order/$orderId/tracking"
    }
    data object OrderHistory : Screen("order_history")
    data object OrderDetails : Screen("order/{orderId}/details") {
        fun createRoute(orderId: String) = "order/$orderId/details"
    }
    
    // Profile
    data object EditProfile : Screen("edit_profile")
    data object SavedAddresses : Screen("saved_addresses")
    data object Favorites : Screen("favorites")
    data object Settings : Screen("settings")
}
