package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.service.LocationUpdate
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    
    val locationUpdates: Flow<LocationUpdate>
    val isTracking: Flow<Boolean>
    
    suspend fun startLocationTracking()
    
    suspend fun stopLocationTracking()
    
    suspend fun getCurrentLocation(): LocationUpdate?
    
    suspend fun updateLocationToServer(
        latitude: Double,
        longitude: Double,
        bearing: Float? = null,
        speed: Float? = null
    ): Result<Unit>
    
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double
    
    fun estimateTime(distanceKm: Double, speedKmh: Double = 25.0): Int
}
