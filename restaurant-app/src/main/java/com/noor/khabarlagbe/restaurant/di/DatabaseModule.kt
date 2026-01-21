package com.noor.khabarlagbe.restaurant.di

import android.content.Context
import androidx.room.Room
import com.noor.khabarlagbe.restaurant.data.local.RestaurantDatabase
import com.noor.khabarlagbe.restaurant.data.local.dao.*
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
    fun provideRestaurantDatabase(@ApplicationContext context: Context): RestaurantDatabase {
        return Room.databaseBuilder(
            context,
            RestaurantDatabase::class.java,
            RestaurantDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRestaurantDao(database: RestaurantDatabase): RestaurantDao {
        return database.restaurantDao()
    }
    
    @Provides
    @Singleton
    fun provideOrderDao(database: RestaurantDatabase): OrderDao {
        return database.orderDao()
    }
    
    @Provides
    @Singleton
    fun provideOrderItemDao(database: RestaurantDatabase): OrderItemDao {
        return database.orderItemDao()
    }
    
    @Provides
    @Singleton
    fun provideMenuCategoryDao(database: RestaurantDatabase): MenuCategoryDao {
        return database.menuCategoryDao()
    }
    
    @Provides
    @Singleton
    fun provideMenuItemDao(database: RestaurantDatabase): MenuItemDao {
        return database.menuItemDao()
    }
    
    @Provides
    @Singleton
    fun provideReviewDao(database: RestaurantDatabase): ReviewDao {
        return database.reviewDao()
    }
}
