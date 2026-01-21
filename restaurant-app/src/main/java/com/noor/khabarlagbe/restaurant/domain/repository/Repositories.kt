package com.noor.khabarlagbe.restaurant.domain.repository

import com.noor.khabarlagbe.restaurant.domain.model.*
import kotlinx.coroutines.flow.Flow

interface RestaurantAuthRepository {
    suspend fun login(email: String, password: String): Result<AuthResult>
    suspend fun register(data: RegistrationData): Result<AuthResult>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(): Result<String>
    suspend fun forgotPassword(email: String): Result<String>
    fun isLoggedIn(): Boolean
    fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearSession()
    fun getCurrentRestaurant(): Flow<Restaurant?>
    suspend fun saveRestaurant(restaurant: Restaurant)
}

interface RestaurantOrderRepository {
    fun getOrders(): Flow<List<Order>>
    fun getOrdersByStatus(status: OrderStatusEnum): Flow<List<Order>>
    suspend fun getOrderById(orderId: String): Result<Order>
    fun observeOrder(orderId: String): Flow<Order?>
    suspend fun acceptOrder(orderId: String, estimatedPrepTime: Int): Result<Order>
    suspend fun rejectOrder(orderId: String, reason: String): Result<Order>
    suspend fun markPreparing(orderId: String): Result<Order>
    suspend fun markReady(orderId: String): Result<Order>
    suspend fun refreshOrders(status: String? = null): Result<List<Order>>
    suspend fun getRestaurantStats(): Result<RestaurantStats>
}

interface MenuRepository {
    fun getCategories(): Flow<List<MenuCategory>>
    fun getAllCategories(): Flow<List<MenuCategory>>
    fun getMenuItems(): Flow<List<MenuItem>>
    fun getMenuItemsByCategory(categoryId: String): Flow<List<MenuItem>>
    fun searchMenuItems(query: String): Flow<List<MenuItem>>
    suspend fun getMenuItemById(itemId: String): Result<MenuItem>
    suspend fun createCategory(name: String, nameEn: String?, description: String?, sortOrder: Int): Result<MenuCategory>
    suspend fun updateCategory(categoryId: String, name: String?, nameEn: String?, description: String?, sortOrder: Int?, isActive: Boolean?): Result<MenuCategory>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
    suspend fun createMenuItem(item: MenuItem): Result<MenuItem>
    suspend fun updateMenuItem(itemId: String, item: MenuItem): Result<MenuItem>
    suspend fun deleteMenuItem(itemId: String): Result<Unit>
    suspend fun toggleItemAvailability(itemId: String): Result<Boolean>
    suspend fun refreshMenu(): Result<MenuWithCategories>
}

interface ReportsRepository {
    suspend fun getReports(startDate: String, endDate: String, period: String = "daily"): Result<Reports>
    suspend fun exportReport(startDate: String, endDate: String, format: String): Result<String>
}

interface ReviewRepository {
    fun getReviews(): Flow<List<Review>>
    fun getReviewsByRating(rating: Int): Flow<List<Review>>
    suspend fun getReviewsWithSummary(page: Int = 1, rating: Int? = null): Result<ReviewsWithSummary>
    suspend fun respondToReview(reviewId: String, response: String): Result<Review>
    suspend fun refreshReviews(): Result<List<Review>>
}

interface SettingsRepository {
    suspend fun getRestaurantProfile(): Result<Restaurant>
    suspend fun updateRestaurantProfile(restaurant: Restaurant): Result<Restaurant>
    suspend fun updateOpenStatus(isOpen: Boolean): Result<Restaurant>
    suspend fun updateBusyMode(isBusy: Boolean): Result<Restaurant>
    suspend fun updateOperatingHours(hours: OperatingHours): Result<Restaurant>
    suspend fun updateDeliverySettings(settings: DeliverySettings): Result<Restaurant>
    suspend fun registerFcmToken(token: String): Result<Unit>
}
