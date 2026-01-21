package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.Rider
import kotlinx.coroutines.flow.Flow

interface RiderAuthRepository {
    
    val isLoggedIn: Flow<Boolean>
    val currentRider: Flow<Rider?>
    
    suspend fun login(phone: String, password: String): Result<Rider>
    
    suspend fun register(
        name: String,
        phone: String,
        email: String?,
        password: String,
        vehicleType: String,
        vehicleNumber: String,
        licenseNumber: String,
        nidNumber: String,
        bankAccountName: String?,
        bankAccountNumber: String?,
        bankName: String?,
        bikashNumber: String?,
        nagadNumber: String?
    ): Result<Rider>
    
    suspend fun verifyOtp(phone: String, otp: String): Result<Rider>
    
    suspend fun resendOtp(phone: String): Result<Unit>
    
    suspend fun logout(): Result<Unit>
    
    suspend fun refreshToken(): Result<Unit>
    
    suspend fun getAccessToken(): String?
    
    suspend fun saveAccessToken(token: String)
    
    suspend fun clearSession()
    
    suspend fun registerFcmToken(token: String): Result<Unit>
}
