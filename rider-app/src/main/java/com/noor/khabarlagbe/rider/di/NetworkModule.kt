package com.noor.khabarlagbe.rider.di

import com.noor.khabarlagbe.rider.data.remote.api.RiderApi
import com.noor.khabarlagbe.rider.data.repository.*
import com.noor.khabarlagbe.rider.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.khabarlagbe.com/") // Replace with actual API URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRiderApi(retrofit: Retrofit): RiderApi {
        return retrofit.create(RiderApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideRiderAuthRepository(api: RiderApi): RiderAuthRepository {
        return RiderAuthRepositoryImpl(api)
    }
    
    @Provides
    @Singleton
    fun provideRiderOrderRepository(api: RiderApi): RiderOrderRepository {
        return RiderOrderRepositoryImpl(api)
    }
    
    @Provides
    @Singleton
    fun provideRiderEarningsRepository(api: RiderApi): RiderEarningsRepository {
        return RiderEarningsRepositoryImpl(api)
    }
    
    @Provides
    @Singleton
    fun provideRiderProfileRepository(api: RiderApi): RiderProfileRepository {
        return RiderProfileRepositoryImpl(api)
    }
    
    @Provides
    @Singleton
    fun provideRiderHistoryRepository(api: RiderApi): RiderHistoryRepository {
        return RiderHistoryRepositoryImpl(api)
    }
    
    @Provides
    @Singleton
    fun provideLocationRepository(api: RiderApi): LocationRepository {
        return LocationRepositoryImpl(api)
    }
}
