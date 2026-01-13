package com.noor.khabarlagbe.rider.domain.model

data class RiderOrder(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val restaurantLocation: Location,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryLocation: Location,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val deliveryFee: Double,
    val distance: Double, // in km
    val estimatedTime: Int, // in minutes
    val status: OrderStatus,
    val createdAt: Long,
    val acceptedAt: Long? = null,
    val pickedUpAt: Long? = null,
    val deliveredAt: Long? = null
)

data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

data class Location(
    val latitude: Double,
    val longitude: Double
)

enum class OrderStatus {
    PENDING,
    ACCEPTED,
    ARRIVED_AT_RESTAURANT,
    PICKED_UP,
    ON_THE_WAY,
    DELIVERED,
    CANCELLED
}
