package com.noor.khabarlagbe.rider.data.repository

import com.noor.khabarlagbe.rider.data.remote.api.PhotoUploadRequest
import com.noor.khabarlagbe.rider.data.remote.api.RiderApi
import com.noor.khabarlagbe.rider.data.remote.api.UpdateProfileRequest
import com.noor.khabarlagbe.rider.domain.model.*
import com.noor.khabarlagbe.rider.domain.repository.RiderProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiderProfileRepositoryImpl @Inject constructor(
    private val api: RiderApi
) : RiderProfileRepository {
    
    private val _profile = MutableStateFlow<Rider?>(null)
    
    override suspend fun getRiderProfile(): Result<Rider> {
        return try {
            val response = api.getRiderProfile()
            if (response.isSuccessful && response.body() != null) {
                _profile.value = response.body()
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("প্রোফাইল লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeProfile(): Flow<Rider> {
        return _profile.map { it ?: Rider(
            id = "",
            name = "",
            phone = "",
            email = null,
            profileImageUrl = null,
            vehicleType = VehicleType.MOTORCYCLE,
            vehicleNumber = "",
            licenseNumber = "",
            nidNumber = "",
            isOnline = false,
            rating = 0.0,
            totalDeliveries = 0,
            todayEarnings = 0.0,
            weeklyEarnings = 0.0,
            monthlyEarnings = 0.0,
            currentLocation = null
        )}
    }
    
    override suspend fun updateProfile(name: String, email: String?, phone: String): Result<Rider> {
        return try {
            val response = api.updateProfile(UpdateProfileRequest(name, email, phone))
            if (response.isSuccessful && response.body() != null) {
                _profile.value = response.body()
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("প্রোফাইল আপডেট করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProfilePhoto(photoUri: String): Result<String> {
        return try {
            val response = api.updateProfilePhoto(PhotoUploadRequest(photoUri))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.photoUrl)
            } else {
                Result.failure(Exception("ছবি আপলোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getVehicleDetails(): Result<Vehicle?> {
        return try {
            val response = api.getVehicleDetails()
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("গাড়ির তথ্য লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateVehicleDetails(vehicle: Vehicle): Result<Vehicle> {
        return try {
            val response = api.updateVehicleDetails(vehicle)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("গাড়ির তথ্য আপডেট করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDocuments(): Result<List<RiderDocument>> {
        return try {
            val response = api.getDocuments()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("ডকুমেন্ট লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun uploadDocument(document: RiderDocument): Result<RiderDocument> {
        return try {
            val response = api.uploadDocument(document)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("ডকুমেন্ট আপলোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getBankDetails(): Result<BankDetails?> {
        return try {
            val response = api.getBankDetails()
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("ব্যাংক তথ্য লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBankDetails(bankDetails: BankDetails): Result<BankDetails> {
        return try {
            val response = api.updateBankDetails(bankDetails)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("ব্যাংক তথ্য আপডেট করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDeliveryStats(): Result<DeliveryStats> {
        return try {
            val response = api.getDeliveryStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("পরিসংখ্যান লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
