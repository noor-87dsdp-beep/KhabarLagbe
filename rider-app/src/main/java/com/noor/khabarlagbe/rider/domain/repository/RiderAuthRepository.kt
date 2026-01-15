package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.Rider
import kotlinx.coroutines.flow.Flow

interface RiderAuthRepository {
    suspend fun login(phone: String, password: String): Result<Rider>
    suspend fun register(
        name: String,
        phone: String,
        email: String,
        password: String,
        vehicleType: String,
        vehicleMake: String,
        vehicleModel: String,
        plateNumber: String,
        nidNumber: String,
        licenseNumber: String
    ): Result<Rider>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentRider(): Result<Rider?>
    fun observeAuthState(): Flow<Rider?>
    suspend fun updateOnlineStatus(isOnline: Boolean): Result<Unit>
}
