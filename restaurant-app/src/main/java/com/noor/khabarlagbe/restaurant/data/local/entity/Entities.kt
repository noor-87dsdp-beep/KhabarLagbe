package com.noor.khabarlagbe.restaurant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.noor.khabarlagbe.restaurant.data.local.Converters

@Entity(tableName = "restaurants")
@TypeConverters(Converters::class)
data class RestaurantEntity(
    @PrimaryKey
    val id: String,
    val businessName: String,
    val ownerName: String,
    val email: String,
    val phone: String,
    val street: String,
    val city: String,
    val area: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val cuisineTypes: List<String>,
    val rating: Double,
    val totalReviews: Int,
    val isOpen: Boolean,
    val isBusy: Boolean,
    val imageUrl: String?,
    val coverImageUrl: String?,
    val minimumOrder: Double?,
    val deliveryRadius: Double?,
    val estimatedPrepTime: Int?,
    val packagingCharge: Double?,
    val createdAt: String,
    val updatedAt: String,
    val lastSyncAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
@TypeConverters(Converters::class)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val orderNumber: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val customerStreet: String,
    val customerCity: String,
    val customerArea: String,
    val subtotal: Double,
    val deliveryFee: Double,
    val packagingCharge: Double,
    val discount: Double,
    val total: Double,
    val status: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val specialInstructions: String?,
    val estimatedPrepTime: Int,
    val riderId: String?,
    val riderName: String?,
    val riderPhone: String?,
    val createdAt: String,
    val updatedAt: String,
    val lastSyncAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val menuItemId: String,
    val name: String,
    val nameEn: String?,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double,
    val specialInstructions: String?
)

@Entity(tableName = "menu_categories")
data class MenuCategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nameEn: String?,
    val description: String?,
    val imageUrl: String?,
    val sortOrder: Int,
    val isActive: Boolean,
    val itemCount: Int,
    val createdAt: String,
    val updatedAt: String,
    val lastSyncAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "menu_items")
@TypeConverters(Converters::class)
data class MenuItemEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val name: String,
    val nameEn: String?,
    val description: String?,
    val descriptionEn: String?,
    val price: Double,
    val discountedPrice: Double?,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val isVegetarian: Boolean,
    val isVegan: Boolean,
    val isGlutenFree: Boolean,
    val isSpicy: Boolean,
    val spicyLevel: Int,
    val preparationTime: Int,
    val calories: Int?,
    val tags: List<String>?,
    val sortOrder: Int,
    val createdAt: String,
    val updatedAt: String,
    val lastSyncAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val customerId: String,
    val customerName: String,
    val customerAvatar: String?,
    val rating: Int,
    val comment: String?,
    val responseText: String?,
    val respondedAt: String?,
    val createdAt: String,
    val updatedAt: String,
    val lastSyncAt: Long = System.currentTimeMillis()
)
