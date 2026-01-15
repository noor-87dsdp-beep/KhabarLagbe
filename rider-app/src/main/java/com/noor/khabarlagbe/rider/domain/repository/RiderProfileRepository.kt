package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.BankDetails
import com.noor.khabarlagbe.rider.domain.model.DeliveryStats
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.model.RiderDocument
import com.noor.khabarlagbe.rider.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface RiderProfileRepository {
    suspend fun getRiderProfile(): Result<Rider>
    fun observeProfile(): Flow<Rider>
    suspend fun updateProfile(
        name: String,
        email: String?,
        phone: String
    ): Result<Rider>
    suspend fun updateProfilePhoto(photoUri: String): Result<String>
    suspend fun getVehicleDetails(): Result<Vehicle?>
    suspend fun updateVehicleDetails(vehicle: Vehicle): Result<Vehicle>
    suspend fun getDocuments(): Result<List<RiderDocument>>
    suspend fun uploadDocument(document: RiderDocument): Result<RiderDocument>
    suspend fun getBankDetails(): Result<BankDetails?>
    suspend fun updateBankDetails(bankDetails: BankDetails): Result<BankDetails>
    suspend fun getDeliveryStats(): Result<DeliveryStats>
}
