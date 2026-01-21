package com.noor.khabarlagbe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KhabarLagbeApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Mapbox auto-initializes via ContentProvider (androidx.startup.InitializationProvider)
        // No manual initialization needed for Mapbox SDK v11+
        // The SDK initializes MapboxNavigationSDKInitializer automatically
        // If initialization fails, check:
        // 1. MAPBOX_ACCESS_TOKEN is set in local.properties or gradle.properties
        // 2. mapbox-common dependency is present in build.gradle.kts
        // 3. ProGuard rules keep Mapbox initialization classes
    }
}
