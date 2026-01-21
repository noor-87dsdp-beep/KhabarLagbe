package com.noor.khabarlagbe.rider.data.dto

import com.google.gson.annotations.SerializedName
import com.noor.khabarlagbe.rider.domain.model.*

data class OrderDto(
    @SerializedName("id") val id: String,
    @SerializedName("orderNumber") val orderNumber: String?,
    @SerializedName("restaurantId") val restaurantId: String,
    @SerializedName("restaurantName") val restaurantName: String,
    @SerializedName("restaurantAddress") val restaurantAddress: String,
    @SerializedName("restaurantLatitude") val restaurantLatitude: Double,
    @SerializedName("restaurantLongitude") val restaurantLongitude: Double,
    @SerializedName("restaurantPhone") val restaurantPhone: String?,
    @SerializedName("restaurantImageUrl") val restaurantImageUrl: String?,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerPhone") val customerPhone: String,
    @SerializedName("customerImageUrl") val customerImageUrl: String?,
    @SerializedName("deliveryAddress") val deliveryAddress: String,
    @SerializedName("deliveryLatitude") val deliveryLatitude: Double,
    @SerializedName("deliveryLongitude") val deliveryLongitude: Double,
    @SerializedName("deliveryInstructions") val deliveryInstructions: String?,
    @SerializedName("items") val items: List<OrderItemDto>,
    @SerializedName("subtotal") val subtotal: Double,
    @SerializedName("deliveryFee") val deliveryFee: Double,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("tip") val tip: Double?,
    @SerializedName("distance") val distance: Double,
    @SerializedName("estimatedTime") val estimatedTime: Int,
    @SerializedName("status") val status: String,
    @SerializedName("paymentMethod") val paymentMethod: String?,
    @SerializedName("isPaid") val isPaid: Boolean,
    @SerializedName("createdAt") val createdAt: Long,
    @SerializedName("acceptedAt") val acceptedAt: Long?,
    @SerializedName("pickedUpAt") val pickedUpAt: Long?,
    @SerializedName("deliveredAt") val deliveredAt: Long?
) {
    fun toDomain(): RiderOrder {
        return RiderOrder(
            id = id,
            restaurantId = restaurantId,
            restaurantName = restaurantName,
            restaurantAddress = restaurantAddress,
            restaurantLocation = Location(restaurantLatitude, restaurantLongitude),
            customerId = customerId,
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = deliveryAddress,
            deliveryLocation = Location(deliveryLatitude, deliveryLongitude),
            items = items.map { it.toDomain() },
            totalAmount = totalAmount,
            deliveryFee = deliveryFee,
            distance = distance,
            estimatedTime = estimatedTime,
            status = try { OrderStatus.valueOf(status.uppercase()) } catch (e: Exception) { OrderStatus.PENDING },
            createdAt = createdAt,
            acceptedAt = acceptedAt,
            pickedUpAt = pickedUpAt,
            deliveredAt = deliveredAt
        )
    }
}

data class OrderItemDto(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("specialInstructions") val specialInstructions: String?
) {
    fun toDomain(): OrderItem {
        return OrderItem(
            name = name,
            quantity = quantity,
            price = price
        )
    }
}

// Order Requests
data class RejectOrderRequest(
    @SerializedName("reason") val reason: String?
)

data class UpdateOrderStatusRequest(
    @SerializedName("status") val status: String
)

data class CompleteDeliveryRequest(
    @SerializedName("deliveryProofImageUrl") val deliveryProofImageUrl: String?,
    @SerializedName("signature") val signature: String?,
    @SerializedName("notes") val notes: String?
)

// Order Responses
data class OrdersResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("orders") val orders: List<OrderDto>,
    @SerializedName("total") val total: Int
)

data class DeliveryCompletionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("order") val order: OrderDto,
    @SerializedName("earnings") val earnings: DeliveryEarningsDto,
    @SerializedName("message") val message: String?
)

data class DeliveryEarningsDto(
    @SerializedName("deliveryFee") val deliveryFee: Double,
    @SerializedName("tip") val tip: Double,
    @SerializedName("bonus") val bonus: Double,
    @SerializedName("total") val total: Double
)

data class DeliveryHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("deliveries") val deliveries: List<OrderDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("totalPages") val totalPages: Int
)
