package com.noor.khabarlagbe.rider.domain.model

data class Rider(
    val id: String,
    val name: String,
    val phone: String,
    val email: String? = null,
    val profileImageUrl: String? = null,
    val vehicleType: VehicleType,
    val vehicleNumber: String,
    val licenseNumber: String,
    val nidNumber: String,
    val isOnline: Boolean = false,
    val rating: Double = 0.0,
    val totalDeliveries: Int = 0,
    val todayEarnings: Double = 0.0,
    val weeklyEarnings: Double = 0.0,
    val monthlyEarnings: Double = 0.0,
    val currentLocation: Location? = null
)

enum class VehicleType {
    BICYCLE,
    MOTORCYCLE,
    SCOOTER,
    CAR
}
