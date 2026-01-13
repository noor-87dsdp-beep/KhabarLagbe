package com.noor.khabarlagbe.rider

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RiderApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize app components
    }
}
