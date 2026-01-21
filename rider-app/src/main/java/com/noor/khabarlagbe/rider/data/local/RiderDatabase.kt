package com.noor.khabarlagbe.rider.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noor.khabarlagbe.rider.data.local.dao.*
import com.noor.khabarlagbe.rider.data.local.entity.*

@Database(
    entities = [
        RiderEntity::class,
        OrderEntity::class,
        DeliveryHistoryEntity::class,
        EarningsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RiderDatabase : RoomDatabase() {
    
    abstract fun riderDao(): RiderDao
    abstract fun orderDao(): OrderDao
    abstract fun deliveryHistoryDao(): DeliveryHistoryDao
    abstract fun earningsDao(): EarningsDao
    
    companion object {
        const val DATABASE_NAME = "rider_database"
    }
}
