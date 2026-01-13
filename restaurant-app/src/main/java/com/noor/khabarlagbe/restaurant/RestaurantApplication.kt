package com.noor.khabarlagbe.restaurant

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RestaurantApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize app components
    }
}
