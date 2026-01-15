package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationUpdates(): Flow<Location>
    suspend fun sendLocationUpdate(location: Location): Result<Unit>
    suspend fun getLastKnownLocation(): Location?
    suspend fun startLocationTracking(): Result<Unit>
    suspend fun stopLocationTracking(): Result<Unit>
}
