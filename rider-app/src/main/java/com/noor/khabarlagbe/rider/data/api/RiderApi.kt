package com.noor.khabarlagbe.rider.data.api

import com.noor.khabarlagbe.rider.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RiderApi {
    
    // Auth endpoints
    @POST("rider/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("rider/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("rider/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>
    
    @POST("rider/auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<MessageResponse>
    
    @POST("rider/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<MessageResponse>
    
    @POST("rider/auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>
    
    // Profile endpoints
    @GET("rider/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<RiderDto>
    
    @PUT("rider/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<RiderDto>
    
    @PUT("rider/profile/vehicle")
    suspend fun updateVehicleInfo(
        @Header("Authorization") token: String,
        @Body request: UpdateVehicleRequest
    ): Response<RiderDto>
    
    @PUT("rider/profile/bank")
    suspend fun updateBankDetails(
        @Header("Authorization") token: String,
        @Body request: UpdateBankRequest
    ): Response<RiderDto>
    
    @Multipart
    @POST("rider/profile/photo")
    suspend fun uploadProfilePhoto(
        @Header("Authorization") token: String,
        @Part photo: okhttp3.MultipartBody.Part
    ): Response<PhotoUploadResponse>
    
    @Multipart
    @POST("rider/documents/upload")
    suspend fun uploadDocument(
        @Header("Authorization") token: String,
        @Part document: okhttp3.MultipartBody.Part,
        @Part("type") type: okhttp3.RequestBody
    ): Response<DocumentUploadResponse>
    
    // Status endpoints
    @PUT("rider/status")
    suspend fun updateOnlineStatus(
        @Header("Authorization") token: String,
        @Body request: UpdateStatusRequest
    ): Response<RiderDto>
    
    @PUT("rider/location")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Body request: UpdateLocationRequest
    ): Response<MessageResponse>
    
    // Order endpoints
    @GET("rider/orders/available")
    suspend fun getAvailableOrders(
        @Header("Authorization") token: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double = 5.0
    ): Response<OrdersResponse>
    
    @GET("rider/orders/active")
    suspend fun getActiveOrder(@Header("Authorization") token: String): Response<OrderDto?>
    
    @GET("rider/orders/{orderId}")
    suspend fun getOrderDetails(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<OrderDto>
    
    @POST("rider/orders/{orderId}/accept")
    suspend fun acceptOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String
    ): Response<OrderDto>
    
    @POST("rider/orders/{orderId}/reject")
    suspend fun rejectOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: RejectOrderRequest
    ): Response<MessageResponse>
    
    @PUT("rider/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<OrderDto>
    
    @POST("rider/orders/{orderId}/complete")
    suspend fun completeDelivery(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: String,
        @Body request: CompleteDeliveryRequest
    ): Response<DeliveryCompletionResponse>
    
    // History endpoints
    @GET("rider/orders/history")
    suspend fun getDeliveryHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("status") status: String? = null
    ): Response<DeliveryHistoryResponse>
    
    // Earnings endpoints
    @GET("rider/earnings")
    suspend fun getEarnings(
        @Header("Authorization") token: String,
        @Query("period") period: String = "today"
    ): Response<EarningsDto>
    
    @GET("rider/earnings/summary")
    suspend fun getEarningsSummary(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<EarningsSummaryDto>
    
    @GET("rider/earnings/history")
    suspend fun getEarningsHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<EarningsHistoryResponse>
    
    @POST("rider/earnings/withdraw")
    suspend fun requestWithdrawal(
        @Header("Authorization") token: String,
        @Body request: WithdrawalRequest
    ): Response<WithdrawalResponse>
    
    @GET("rider/earnings/withdrawals")
    suspend fun getWithdrawalHistory(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<WithdrawalHistoryResponse>
    
    // Stats endpoints
    @GET("rider/stats")
    suspend fun getStats(
        @Header("Authorization") token: String,
        @Query("period") period: String = "week"
    ): Response<StatsDto>
    
    @GET("rider/stats/leaderboard")
    suspend fun getLeaderboard(
        @Header("Authorization") token: String,
        @Query("period") period: String = "week",
        @Query("limit") limit: Int = 10
    ): Response<LeaderboardResponse>
    
    // FCM token
    @POST("rider/fcm-token")
    suspend fun registerFcmToken(
        @Header("Authorization") token: String,
        @Body request: FcmTokenRequest
    ): Response<MessageResponse>
}
