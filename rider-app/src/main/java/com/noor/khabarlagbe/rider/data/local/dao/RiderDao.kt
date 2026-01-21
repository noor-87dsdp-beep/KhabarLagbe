package com.noor.khabarlagbe.rider.data.local.dao

import androidx.room.*
import com.noor.khabarlagbe.rider.data.local.entity.RiderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiderDao {
    
    @Query("SELECT * FROM riders WHERE id = :riderId")
    fun getRiderById(riderId: String): Flow<RiderEntity?>
    
    @Query("SELECT * FROM riders LIMIT 1")
    fun getCurrentRider(): Flow<RiderEntity?>
    
    @Query("SELECT * FROM riders LIMIT 1")
    suspend fun getCurrentRiderSync(): RiderEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRider(rider: RiderEntity)
    
    @Update
    suspend fun updateRider(rider: RiderEntity)
    
    @Query("UPDATE riders SET isOnline = :isOnline WHERE id = :riderId")
    suspend fun updateOnlineStatus(riderId: String, isOnline: Boolean)
    
    @Query("UPDATE riders SET currentLatitude = :latitude, currentLongitude = :longitude WHERE id = :riderId")
    suspend fun updateLocation(riderId: String, latitude: Double, longitude: Double)
    
    @Query("UPDATE riders SET todayEarnings = :today, weeklyEarnings = :weekly, monthlyEarnings = :monthly WHERE id = :riderId")
    suspend fun updateEarnings(riderId: String, today: Double, weekly: Double, monthly: Double)
    
    @Query("DELETE FROM riders WHERE id = :riderId")
    suspend fun deleteRider(riderId: String)
    
    @Query("DELETE FROM riders")
    suspend fun clearAll()
}
