package com.noor.khabarlagbe.rider.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noor.khabarlagbe.rider.domain.model.*

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val restaurantLatitude: Double,
    val restaurantLongitude: Double,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val deliveryLatitude: Double,
    val deliveryLongitude: Double,
    val itemsJson: String, // JSON serialized list of items
    val totalAmount: Double,
    val deliveryFee: Double,
    val distance: Double,
    val estimatedTime: Int,
    val status: String,
    val createdAt: Long,
    val acceptedAt: Long?,
    val pickedUpAt: Long?,
    val deliveredAt: Long?,
    val syncedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): RiderOrder {
        val items = try {
            com.google.gson.Gson().fromJson(itemsJson, Array<OrderItem>::class.java).toList()
        } catch (e: Exception) {
            emptyList<OrderItem>()
        }
        
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
            items = items,
            totalAmount = totalAmount,
            deliveryFee = deliveryFee,
            distance = distance,
            estimatedTime = estimatedTime,
            status = try { OrderStatus.valueOf(status) } catch (e: Exception) { OrderStatus.PENDING },
            createdAt = createdAt,
            acceptedAt = acceptedAt,
            pickedUpAt = pickedUpAt,
            deliveredAt = deliveredAt
        )
    }

    companion object {
        fun fromDomain(order: RiderOrder): OrderEntity {
            val itemsJson = com.google.gson.Gson().toJson(order.items)
            
            return OrderEntity(
                id = order.id,
                restaurantId = order.restaurantId,
                restaurantName = order.restaurantName,
                restaurantAddress = order.restaurantAddress,
                restaurantLatitude = order.restaurantLocation.latitude,
                restaurantLongitude = order.restaurantLocation.longitude,
                customerId = order.customerId,
                customerName = order.customerName,
                customerPhone = order.customerPhone,
                deliveryAddress = order.deliveryAddress,
                deliveryLatitude = order.deliveryLocation.latitude,
                deliveryLongitude = order.deliveryLocation.longitude,
                itemsJson = itemsJson,
                totalAmount = order.totalAmount,
                deliveryFee = order.deliveryFee,
                distance = order.distance,
                estimatedTime = order.estimatedTime,
                status = order.status.name,
                createdAt = order.createdAt,
                acceptedAt = order.acceptedAt,
                pickedUpAt = order.pickedUpAt,
                deliveredAt = order.deliveredAt
            )
        }
    }
}

@Entity(tableName = "delivery_history")
data class DeliveryHistoryEntity(
    @PrimaryKey val id: String,
    val orderNumber: String?,
    val restaurantName: String,
    val customerName: String,
    val deliveryAddress: String,
    val totalAmount: Double,
    val deliveryFee: Double,
    val tip: Double,
    val distance: Double,
    val status: String,
    val completedAt: Long,
    val rating: Double?
)

@Entity(tableName = "earnings")
data class EarningsEntity(
    @PrimaryKey val id: String,
    val date: Long,
    val orderId: String?,
    val amount: Double,
    val tip: Double,
    val type: String,
    val description: String?
)
