package com.noor.khabarlagbe.rider.di

import android.content.Context
import androidx.room.Room
import com.noor.khabarlagbe.rider.data.local.RiderDatabase
import com.noor.khabarlagbe.rider.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideRiderDatabase(
        @ApplicationContext context: Context
    ): RiderDatabase {
        return Room.databaseBuilder(
            context,
            RiderDatabase::class.java,
            RiderDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRiderDao(database: RiderDatabase): RiderDao {
        return database.riderDao()
    }
    
    @Provides
    @Singleton
    fun provideOrderDao(database: RiderDatabase): OrderDao {
        return database.orderDao()
    }
    
    @Provides
    @Singleton
    fun provideDeliveryHistoryDao(database: RiderDatabase): DeliveryHistoryDao {
        return database.deliveryHistoryDao()
    }
    
    @Provides
    @Singleton
    fun provideEarningsDao(database: RiderDatabase): EarningsDao {
        return database.earningsDao()
    }
}
