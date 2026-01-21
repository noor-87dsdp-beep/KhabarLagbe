package com.noor.khabarlagbe.restaurant.data.api

import com.noor.khabarlagbe.restaurant.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RestaurantApi {
    
    // Auth endpoints
    @POST("restaurant/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>
    
    @POST("restaurant/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<RegisterResponseDto>
    
    @POST("restaurant/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): Response<RefreshTokenResponseDto>
    
    @POST("restaurant/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
    
    @POST("restaurant/auth/forgot-password")
    suspend fun forgotPassword(@Body email: Map<String, String>): Response<Map<String, Any>>
    
    // Restaurant profile endpoints
    @GET("restaurant/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<RestaurantResponseDto>
    
    @PUT("restaurant/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: RestaurantUpdateRequestDto
    ): Response<RestaurantResponseDto>
    
    @GET("restaurant/stats")
    suspend fun getStats(@Header("Authorization") token: String): Response<RestaurantStatsResponseDto>
    
    @PUT("restaurant/status")
    suspend fun updateStatus(
        @Header("Authorization") token: String,
        @Body status: Map<String, Boolean>
    ): Response<RestaurantResponseDto>
    
    // Order endpoints
    @GET("restaurant/orders")
    suspend fun getOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<OrdersResponseDto>
    
    @GET("restaurant/orders/{orderId}")
    suspend fun getOrderById(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<OrderResponseDto>
    
    @PUT("restaurant/orders/{orderId}/accept")
    suspend fun acceptOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequestDto
    ): Response<OrderResponseDto>
    
    @PUT("restaurant/orders/{orderId}/reject")
    suspend fun rejectOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: RejectOrderRequestDto
    ): Response<OrderResponseDto>
    
    @PUT("restaurant/orders/{orderId}/preparing")
    suspend fun markPreparing(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<OrderResponseDto>
    
    @PUT("restaurant/orders/{orderId}/ready")
    suspend fun markReady(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<OrderResponseDto>
    
    // Menu endpoints
    @GET("restaurant/menu")
    suspend fun getMenu(@Header("Authorization") token: String): Response<MenuResponseDto>
    
    @GET("restaurant/menu/categories")
    suspend fun getCategories(@Header("Authorization") token: String): Response<CategoriesResponseDto>
    
    @POST("restaurant/menu/categories")
    suspend fun createCategory(
        @Header("Authorization") token: String,
        @Body request: CreateCategoryRequestDto
    ): Response<CategoryResponseDto>
    
    @PUT("restaurant/menu/categories/{categoryId}")
    suspend fun updateCategory(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: String,
        @Body request: UpdateCategoryRequestDto
    ): Response<CategoryResponseDto>
    
    @DELETE("restaurant/menu/categories/{categoryId}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: String
    ): Response<Map<String, Any>>
    
    @GET("restaurant/menu/items")
    suspend fun getMenuItems(
        @Header("Authorization") token: String,
        @Query("categoryId") categoryId: String? = null
    ): Response<MenuItemsResponseDto>
    
    @GET("restaurant/menu/items/{itemId}")
    suspend fun getMenuItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String
    ): Response<MenuItemResponseDto>
    
    @POST("restaurant/menu/items")
    suspend fun createMenuItem(
        @Header("Authorization") token: String,
        @Body request: CreateMenuItemRequestDto
    ): Response<MenuItemResponseDto>
    
    @PUT("restaurant/menu/items/{itemId}")
    suspend fun updateMenuItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String,
        @Body request: UpdateMenuItemRequestDto
    ): Response<MenuItemResponseDto>
    
    @DELETE("restaurant/menu/items/{itemId}")
    suspend fun deleteMenuItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String
    ): Response<Map<String, Any>>
    
    @PUT("restaurant/menu/items/{itemId}/availability")
    suspend fun toggleItemAvailability(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String
    ): Response<ToggleAvailabilityResponseDto>
    
    // Reports endpoints
    @GET("restaurant/reports")
    suspend fun getReports(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("period") period: String = "daily"
    ): Response<ReportsResponseDto>
    
    @POST("restaurant/reports/export")
    suspend fun exportReport(
        @Header("Authorization") token: String,
        @Body request: ExportReportRequestDto
    ): Response<ExportReportResponseDto>
    
    // Reviews endpoints
    @GET("restaurant/reviews")
    suspend fun getReviews(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("rating") rating: Int? = null,
        @Query("sortBy") sortBy: String = "createdAt",
        @Query("order") order: String = "desc"
    ): Response<ReviewsResponseDto>
    
    @POST("restaurant/reviews/{reviewId}/respond")
    suspend fun respondToReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String,
        @Body request: RespondToReviewRequestDto
    ): Response<ReviewResponseDto>
    
    // FCM token registration
    @POST("restaurant/device/register")
    suspend fun registerDevice(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<Map<String, Any>>
}
