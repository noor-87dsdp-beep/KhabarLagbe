package com.noor.khabarlagbe.di

import android.content.Context
import androidx.room.Room
import com.noor.khabarlagbe.data.local.KhabarLagbeDatabase
import com.noor.khabarlagbe.data.local.dao.*
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
    fun provideKhabarLagbeDatabase(@ApplicationContext context: Context): KhabarLagbeDatabase {
        return Room.databaseBuilder(
            context,
            KhabarLagbeDatabase::class.java,
            "khabarlagbe_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideUserDao(database: KhabarLagbeDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    @Singleton
    fun provideAddressDao(database: KhabarLagbeDatabase): AddressDao {
        return database.addressDao()
    }
    
    @Provides
    @Singleton
    fun provideCartDao(database: KhabarLagbeDatabase): CartDao {
        return database.cartDao()
    }
    
    @Provides
    @Singleton
    fun provideFavoriteDao(database: KhabarLagbeDatabase): FavoriteDao {
        return database.favoriteDao()
    }
    
    @Provides
    @Singleton
    fun provideRecentSearchDao(database: KhabarLagbeDatabase): RecentSearchDao {
        return database.recentSearchDao()
    }
}

