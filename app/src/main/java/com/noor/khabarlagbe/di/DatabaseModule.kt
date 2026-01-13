package com.noor.khabarlagbe.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database Module for Room Database
 * 
 * This is a stub implementation. To enable:
 * 1. Uncomment Room dependencies in build.gradle.kts
 * 2. Create AppDatabase with @Database annotation
 * 3. Define DAOs and Entities
 * 4. Provide database instance here
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    // TODO: Uncomment when Room is ready
    // @Provides
    // @Singleton
    // fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
    //     return Room.databaseBuilder(
    //         context,
    //         AppDatabase::class.java,
    //         "khabarlagbe_database"
    //     ).build()
    // }
}
