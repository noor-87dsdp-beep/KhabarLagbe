package com.noor.khabarlagbe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.noor.khabarlagbe.data.local.dao.*
import com.noor.khabarlagbe.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        AddressEntity::class,
        CartItemEntity::class,
        FavoriteEntity::class,
        RecentSearchEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KhabarLagbeDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun addressDao(): AddressDao
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentSearchDao(): RecentSearchDao
}
