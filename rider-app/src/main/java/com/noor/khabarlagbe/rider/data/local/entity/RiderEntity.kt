package com.noor.khabarlagbe.rider.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noor.khabarlagbe.rider.domain.model.Rider
import com.noor.khabarlagbe.rider.domain.model.VehicleType
import com.noor.khabarlagbe.rider.domain.model.Location

@Entity(tableName = "riders")
data class RiderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val email: String?,
    val profileImageUrl: String?,
    val vehicleType: String,
    val vehicleNumber: String,
    val licenseNumber: String,
    val nidNumber: String,
    val isOnline: Boolean,
    val rating: Double,
    val totalDeliveries: Int,
    val todayEarnings: Double,
    val weeklyEarnings: Double,
    val monthlyEarnings: Double,
    val currentLatitude: Double?,
    val currentLongitude: Double?,
    val bankAccountName: String?,
    val bankAccountNumber: String?,
    val bankName: String?,
    val bikashNumber: String?,
    val nagadNumber: String?,
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Rider {
        return Rider(
            id = id,
            name = name,
            phone = phone,
            email = email,
            profileImageUrl = profileImageUrl,
            vehicleType = try { VehicleType.valueOf(vehicleType) } catch (e: Exception) { VehicleType.MOTORCYCLE },
            vehicleNumber = vehicleNumber,
            licenseNumber = licenseNumber,
            nidNumber = nidNumber,
            isOnline = isOnline,
            rating = rating,
            totalDeliveries = totalDeliveries,
            todayEarnings = todayEarnings,
            weeklyEarnings = weeklyEarnings,
            monthlyEarnings = monthlyEarnings,
            currentLocation = if (currentLatitude != null && currentLongitude != null) {
                Location(currentLatitude, currentLongitude)
            } else null
        )
    }

    companion object {
        fun fromDomain(rider: Rider): RiderEntity {
            return RiderEntity(
                id = rider.id,
                name = rider.name,
                phone = rider.phone,
                email = rider.email,
                profileImageUrl = rider.profileImageUrl,
                vehicleType = rider.vehicleType.name,
                vehicleNumber = rider.vehicleNumber,
                licenseNumber = rider.licenseNumber,
                nidNumber = rider.nidNumber,
                isOnline = rider.isOnline,
                rating = rider.rating,
                totalDeliveries = rider.totalDeliveries,
                todayEarnings = rider.todayEarnings,
                weeklyEarnings = rider.weeklyEarnings,
                monthlyEarnings = rider.monthlyEarnings,
                currentLatitude = rider.currentLocation?.latitude,
                currentLongitude = rider.currentLocation?.longitude,
                bankAccountName = null,
                bankAccountNumber = null,
                bankName = null,
                bikashNumber = null,
                nagadNumber = null
            )
        }
    }
}
