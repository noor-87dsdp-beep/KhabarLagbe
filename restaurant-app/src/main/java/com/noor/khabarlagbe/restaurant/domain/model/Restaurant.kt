package com.noor.khabarlagbe.restaurant.domain.model

data class Restaurant(
    val id: String,
    val businessName: String,
    val ownerName: String,
    val email: String,
    val phone: String,
    val address: Address,
    val cuisineTypes: List<String>,
    val rating: Double,
    val totalReviews: Int,
    val isOpen: Boolean,
    val isBusy: Boolean,
    val imageUrl: String?,
    val coverImageUrl: String?,
    val operatingHours: OperatingHours?,
    val deliverySettings: DeliverySettings?
)

data class Address(
    val street: String,
    val city: String,
    val area: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double
)

data class OperatingHours(
    val monday: DayHours?,
    val tuesday: DayHours?,
    val wednesday: DayHours?,
    val thursday: DayHours?,
    val friday: DayHours?,
    val saturday: DayHours?,
    val sunday: DayHours?
)

data class DayHours(
    val isOpen: Boolean,
    val openTime: String,
    val closeTime: String
)

data class DeliverySettings(
    val minimumOrder: Double,
    val deliveryRadius: Double,
    val estimatedPrepTime: Int,
    val packagingCharge: Double
)

data class RestaurantStats(
    val todayOrders: Int,
    val todayRevenue: Double,
    val averageRating: Double,
    val totalReviews: Int,
    val pendingOrders: Int,
    val preparingOrders: Int,
    val readyOrders: Int,
    val completedOrders: Int
)

data class AuthResult(
    val token: String,
    val restaurant: Restaurant
)

data class RegistrationData(
    val businessName: String,
    val ownerName: String,
    val email: String,
    val phone: String,
    val password: String,
    val address: Address,
    val cuisineTypes: List<String>,
    val documents: Documents,
    val bankDetails: BankDetails
)

data class Documents(
    val tradeLicense: String,
    val nidFront: String,
    val nidBack: String,
    val restaurantPhoto: String
)

data class BankDetails(
    val bankName: String,
    val accountName: String,
    val accountNumber: String,
    val branchName: String,
    val routingNumber: String?
)
