package com.noor.khabarlagbe.restaurant.data.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    @SerializedName("id") val id: String,
    @SerializedName("orderNumber") val orderNumber: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerPhone") val customerPhone: String,
    @SerializedName("customerAddress") val customerAddress: AddressDto,
    @SerializedName("items") val items: List<OrderItemDto>,
    @SerializedName("subtotal") val subtotal: Double,
    @SerializedName("deliveryFee") val deliveryFee: Double,
    @SerializedName("packagingCharge") val packagingCharge: Double,
    @SerializedName("discount") val discount: Double,
    @SerializedName("total") val total: Double,
    @SerializedName("status") val status: String,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("paymentStatus") val paymentStatus: String,
    @SerializedName("specialInstructions") val specialInstructions: String?,
    @SerializedName("estimatedPrepTime") val estimatedPrepTime: Int,
    @SerializedName("riderId") val riderId: String?,
    @SerializedName("riderName") val riderName: String?,
    @SerializedName("riderPhone") val riderPhone: String?,
    @SerializedName("timeline") val timeline: List<OrderTimelineDto>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class OrderItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("menuItemId") val menuItemId: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("customizations") val customizations: List<OrderCustomizationDto>?,
    @SerializedName("specialInstructions") val specialInstructions: String?
)

data class OrderCustomizationDto(
    @SerializedName("name") val name: String,
    @SerializedName("option") val option: String,
    @SerializedName("price") val price: Double
)

data class OrderTimelineDto(
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("note") val note: String?
)

data class OrdersResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: OrdersDataDto?
)

data class OrdersDataDto(
    @SerializedName("orders") val orders: List<OrderDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class OrderResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: OrderDto?
)

data class UpdateOrderStatusRequestDto(
    @SerializedName("status") val status: String,
    @SerializedName("estimatedPrepTime") val estimatedPrepTime: Int? = null,
    @SerializedName("note") val note: String? = null
)

data class RejectOrderRequestDto(
    @SerializedName("reason") val reason: String
)

object OrderStatus {
    const val PENDING = "pending"
    const val ACCEPTED = "accepted"
    const val PREPARING = "preparing"
    const val READY = "ready"
    const val PICKED_UP = "picked_up"
    const val DELIVERED = "delivered"
    const val CANCELLED = "cancelled"
    const val REJECTED = "rejected"
}
