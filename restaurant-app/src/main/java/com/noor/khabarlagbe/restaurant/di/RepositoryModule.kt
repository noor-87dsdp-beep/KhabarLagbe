package com.noor.khabarlagbe.restaurant.di

import com.noor.khabarlagbe.restaurant.data.repository.*
import com.noor.khabarlagbe.restaurant.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindRestaurantAuthRepository(
        impl: RestaurantAuthRepositoryImpl
    ): RestaurantAuthRepository
    
    @Binds
    @Singleton
    abstract fun bindRestaurantOrderRepository(
        impl: RestaurantOrderRepositoryImpl
    ): RestaurantOrderRepository
    
    @Binds
    @Singleton
    abstract fun bindMenuRepository(
        impl: MenuRepositoryImpl
    ): MenuRepository
    
    @Binds
    @Singleton
    abstract fun bindReportsRepository(
        impl: ReportsRepositoryImpl
    ): ReportsRepository
    
    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        impl: ReviewRepositoryImpl
    ): ReviewRepository
    
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
