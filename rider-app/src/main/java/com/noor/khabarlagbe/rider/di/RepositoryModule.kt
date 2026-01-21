package com.noor.khabarlagbe.rider.di

import com.noor.khabarlagbe.rider.data.repository.*
import com.noor.khabarlagbe.rider.domain.repository.*
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
    abstract fun bindRiderAuthRepository(
        impl: RiderAuthRepositoryImpl
    ): RiderAuthRepository
    
    @Binds
    @Singleton
    abstract fun bindRiderOrderRepository(
        impl: RiderOrderRepositoryImpl
    ): RiderOrderRepository
    
    @Binds
    @Singleton
    abstract fun bindRiderEarningsRepository(
        impl: RiderEarningsRepositoryImpl
    ): RiderEarningsRepository
    
    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository
}
