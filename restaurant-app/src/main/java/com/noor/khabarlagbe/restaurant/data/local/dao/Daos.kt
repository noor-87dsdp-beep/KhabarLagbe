package com.noor.khabarlagbe.restaurant.data.local.dao

import androidx.room.*
import com.noor.khabarlagbe.restaurant.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants WHERE id = :id")
    fun getRestaurant(id: String): Flow<RestaurantEntity?>
    
    @Query("SELECT * FROM restaurants LIMIT 1")
    suspend fun getCurrentRestaurant(): RestaurantEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: RestaurantEntity)
    
    @Update
    suspend fun updateRestaurant(restaurant: RestaurantEntity)
    
    @Query("DELETE FROM restaurants")
    suspend fun clearRestaurants()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity?
    
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun observeOrder(orderId: String): Flow<OrderEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)
    
    @Update
    suspend fun updateOrder(order: OrderEntity)
    
    @Query("UPDATE orders SET status = :status, updatedAt = :updatedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, updatedAt: String)
    
    @Delete
    suspend fun deleteOrder(order: OrderEntity)
    
    @Query("DELETE FROM orders WHERE createdAt < :timestamp")
    suspend fun deleteOldOrders(timestamp: String)
    
    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}

@Dao
interface OrderItemDao {
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>
    
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun observeOrderItems(orderId: String): Flow<List<OrderItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)
    
    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItems(orderId: String)
    
    @Query("DELETE FROM order_items")
    suspend fun clearOrderItems()
}

@Dao
interface MenuCategoryDao {
    @Query("SELECT * FROM menu_categories WHERE isActive = 1 ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<MenuCategoryEntity>>
    
    @Query("SELECT * FROM menu_categories ORDER BY sortOrder ASC")
    fun getAllCategoriesIncludingInactive(): Flow<List<MenuCategoryEntity>>
    
    @Query("SELECT * FROM menu_categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): MenuCategoryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: MenuCategoryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<MenuCategoryEntity>)
    
    @Update
    suspend fun updateCategory(category: MenuCategoryEntity)
    
    @Delete
    suspend fun deleteCategory(category: MenuCategoryEntity)
    
    @Query("DELETE FROM menu_categories WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: String)
    
    @Query("DELETE FROM menu_categories")
    suspend fun clearCategories()
}

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items ORDER BY sortOrder ASC")
    fun getAllMenuItems(): Flow<List<MenuItemEntity>>
    
    @Query("SELECT * FROM menu_items WHERE categoryId = :categoryId ORDER BY sortOrder ASC")
    fun getMenuItemsByCategory(categoryId: String): Flow<List<MenuItemEntity>>
    
    @Query("SELECT * FROM menu_items WHERE isAvailable = 1 ORDER BY sortOrder ASC")
    fun getAvailableMenuItems(): Flow<List<MenuItemEntity>>
    
    @Query("SELECT * FROM menu_items WHERE id = :itemId")
    suspend fun getMenuItemById(itemId: String): MenuItemEntity?
    
    @Query("SELECT * FROM menu_items WHERE name LIKE '%' || :query || '%' OR nameEn LIKE '%' || :query || '%'")
    fun searchMenuItems(query: String): Flow<List<MenuItemEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItemEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)
    
    @Update
    suspend fun updateMenuItem(item: MenuItemEntity)
    
    @Query("UPDATE menu_items SET isAvailable = :isAvailable WHERE id = :itemId")
    suspend fun updateAvailability(itemId: String, isAvailable: Boolean)
    
    @Delete
    suspend fun deleteMenuItem(item: MenuItemEntity)
    
    @Query("DELETE FROM menu_items WHERE id = :itemId")
    suspend fun deleteMenuItemById(itemId: String)
    
    @Query("DELETE FROM menu_items WHERE categoryId = :categoryId")
    suspend fun deleteMenuItemsByCategory(categoryId: String)
    
    @Query("DELETE FROM menu_items")
    suspend fun clearMenuItems()
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>
    
    @Query("SELECT * FROM reviews WHERE rating = :rating ORDER BY createdAt DESC")
    fun getReviewsByRating(rating: Int): Flow<List<ReviewEntity>>
    
    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: String): ReviewEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)
    
    @Update
    suspend fun updateReview(review: ReviewEntity)
    
    @Query("UPDATE reviews SET responseText = :response, respondedAt = :respondedAt WHERE id = :reviewId")
    suspend fun updateReviewResponse(reviewId: String, response: String, respondedAt: String)
    
    @Query("DELETE FROM reviews")
    suspend fun clearReviews()
}
