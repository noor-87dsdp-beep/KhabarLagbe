package com.noor.khabarlagbe.rider.data.remote.api

import com.noor.khabarlagbe.rider.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface RiderApi {
    // Authentication
    @POST("api/rider/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
    
    @POST("api/rider/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
    
    @POST("api/rider/auth/logout")
    suspend fun logout(): Response<Unit>
    
    @GET("api/rider/auth/me")
    suspend fun getCurrentRider(): Response<Rider>
    
    @PUT("api/rider/status")
    suspend fun updateOnlineStatus(
        @Body request: OnlineStatusRequest
    ): Response<Unit>
    
    // Orders
    @GET("api/rider/orders/available")
    suspend fun getAvailableOrders(): Response<List<RiderOrder>>
    
    @POST("api/rider/orders/{orderId}/accept")
    suspend fun acceptOrder(@Path("orderId") orderId: String): Response<RiderOrder>
    
    @POST("api/rider/orders/{orderId}/reject")
    suspend fun rejectOrder(@Path("orderId") orderId: String): Response<Unit>
    
    @PUT("api/rider/orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body request: OrderStatusRequest
    ): Response<RiderOrder>
    
    @GET("api/rider/orders/active")
    suspend fun getActiveOrder(): Response<RiderOrder?>
    
    @POST("api/rider/orders/{orderId}/verify-pickup")
    suspend fun verifyPickupOtp(
        @Path("orderId") orderId: String,
        @Body request: OtpRequest
    ): Response<OtpResponse>
    
    @POST("api/rider/orders/{orderId}/verify-delivery")
    suspend fun verifyDeliveryOtp(
        @Path("orderId") orderId: String,
        @Body request: OtpRequest
    ): Response<OtpResponse>
    
    @POST("api/rider/orders/{orderId}/complete")
    suspend fun completeDelivery(@Path("orderId") orderId: String): Response<RiderOrder>
    
    @POST("api/rider/orders/{orderId}/report-issue")
    suspend fun reportIssue(
        @Path("orderId") orderId: String,
        @Body request: IssueRequest
    ): Response<Unit>
    
    // Earnings
    @GET("api/rider/earnings/today")
    suspend fun getTodayEarnings(): Response<Earnings>
    
    @GET("api/rider/earnings")
    suspend fun getEarningsByDateRange(
        @Query("startDate") startDate: Long,
        @Query("endDate") endDate: Long
    ): Response<List<EarningsEntry>>
    
    @GET("api/rider/earnings/history")
    suspend fun getTransactionHistory(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<EarningsEntry>>
    
    // Profile
    @GET("api/rider/profile")
    suspend fun getRiderProfile(): Response<Rider>
    
    @PUT("api/rider/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<Rider>
    
    @POST("api/rider/profile/photo")
    suspend fun updateProfilePhoto(@Body request: PhotoUploadRequest): Response<PhotoUploadResponse>
    
    @GET("api/rider/vehicle")
    suspend fun getVehicleDetails(): Response<Vehicle?>
    
    @PUT("api/rider/vehicle")
    suspend fun updateVehicleDetails(@Body vehicle: Vehicle): Response<Vehicle>
    
    @GET("api/rider/documents")
    suspend fun getDocuments(): Response<List<RiderDocument>>
    
    @POST("api/rider/documents")
    suspend fun uploadDocument(@Body document: RiderDocument): Response<RiderDocument>
    
    @GET("api/rider/bank-details")
    suspend fun getBankDetails(): Response<BankDetails?>
    
    @PUT("api/rider/bank-details")
    suspend fun updateBankDetails(@Body bankDetails: BankDetails): Response<BankDetails>
    
    @GET("api/rider/stats")
    suspend fun getDeliveryStats(): Response<DeliveryStats>
    
    // History
    @GET("api/rider/history")
    suspend fun getDeliveryHistory(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<RiderOrder>>
    
    @GET("api/rider/history/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: String): Response<RiderOrder>
    
    @GET("api/rider/history/search")
    suspend fun searchOrders(@Query("query") query: String): Response<List<RiderOrder>>
    
    @GET("api/rider/history/by-date")
    suspend fun getOrdersByDateRange(
        @Query("startDate") startDate: Long,
        @Query("endDate") endDate: Long
    ): Response<List<RiderOrder>>
    
    // Location
    @POST("api/rider/location")
    suspend fun sendLocationUpdate(@Body location: Location): Response<Unit>
}

// Request/Response DTOs
data class LoginRequest(val phone: String, val password: String)
data class RegisterRequest(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val vehicleType: String,
    val vehicleMake: String,
    val vehicleModel: String,
    val plateNumber: String,
    val nidNumber: String,
    val licenseNumber: String
)
data class AuthResponse(val rider: Rider, val token: String)
data class OnlineStatusRequest(val isOnline: Boolean)
data class OrderStatusRequest(val status: OrderStatus)
data class OtpRequest(val otp: String)
data class OtpResponse(val verified: Boolean, val message: String)
data class IssueRequest(val issue: String)
data class UpdateProfileRequest(val name: String, val email: String?, val phone: String)
data class PhotoUploadRequest(val photoBase64: String)
data class PhotoUploadResponse(val photoUrl: String)
