package com.noor.khabarlagbe.restaurant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noor.khabarlagbe.restaurant.data.local.dao.*
import com.noor.khabarlagbe.restaurant.data.local.entity.*

@Database(
    entities = [
        RestaurantEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        MenuCategoryEntity::class,
        MenuItemEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RestaurantDatabase : RoomDatabase() {
    
    abstract fun restaurantDao(): RestaurantDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun menuCategoryDao(): MenuCategoryDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun reviewDao(): ReviewDao
    
    companion object {
        const val DATABASE_NAME = "restaurant_database"
    }
}
