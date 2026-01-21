package com.noor.khabarlagbe.restaurant.domain.model

data class Order(
    val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: Address,
    val items: List<OrderItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val packagingCharge: Double,
    val discount: Double,
    val total: Double,
    val status: OrderStatusEnum,
    val paymentMethod: String,
    val paymentStatus: String,
    val specialInstructions: String?,
    val estimatedPrepTime: Int,
    val riderId: String?,
    val riderName: String?,
    val riderPhone: String?,
    val timeline: List<OrderTimeline>,
    val createdAt: String,
    val updatedAt: String
)

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val name: String,
    val nameEn: String?,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double,
    val customizations: List<OrderCustomization>?,
    val specialInstructions: String?
)

data class OrderCustomization(
    val name: String,
    val option: String,
    val price: Double
)

data class OrderTimeline(
    val status: String,
    val timestamp: String,
    val note: String?
)

enum class OrderStatusEnum {
    PENDING,
    ACCEPTED,
    PREPARING,
    READY,
    PICKED_UP,
    DELIVERED,
    CANCELLED,
    REJECTED;
    
    companion object {
        fun fromString(status: String): OrderStatusEnum {
            return when (status.lowercase()) {
                "pending" -> PENDING
                "accepted" -> ACCEPTED
                "preparing" -> PREPARING
                "ready" -> READY
                "picked_up" -> PICKED_UP
                "delivered" -> DELIVERED
                "cancelled" -> CANCELLED
                "rejected" -> REJECTED
                else -> PENDING
            }
        }
    }
    
    fun toApiString(): String {
        return when (this) {
            PENDING -> "pending"
            ACCEPTED -> "accepted"
            PREPARING -> "preparing"
            READY -> "ready"
            PICKED_UP -> "picked_up"
            DELIVERED -> "delivered"
            CANCELLED -> "cancelled"
            REJECTED -> "rejected"
        }
    }
    
    fun displayNameBn(): String {
        return when (this) {
            PENDING -> "অপেক্ষমান"
            ACCEPTED -> "গৃহীত"
            PREPARING -> "প্রস্তুত হচ্ছে"
            READY -> "প্রস্তুত"
            PICKED_UP -> "পিকআপ হয়েছে"
            DELIVERED -> "ডেলিভারি সম্পন্ন"
            CANCELLED -> "বাতিল"
            REJECTED -> "প্রত্যাখ্যাত"
        }
    }
}
