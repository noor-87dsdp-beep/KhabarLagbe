package com.noor.khabarlagbe.rider.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Location Service for Rider App
 * Provides real-time GPS location updates for delivery tracking.
 * Runs as a foreground service to ensure continuous tracking.
 * 
 * Battery Impact Note:
 * - Uses PRIORITY_HIGH_ACCURACY for precise delivery tracking
 * - Updates every 5 seconds (suitable for active deliveries)
 * - Should only run during active delivery sessions
 * - Automatically stopped when delivery completes
 * 
 * Lifecycle Management:
 * - LocationCallback is created in onCreate() and properly cleaned up in onDestroy()
 * - Service scope is cancelled in onDestroy() to prevent memory leaks
 */
class LocationService : Service() {
    
    private val tag = "LocationService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    
    private val _locationUpdates = MutableSharedFlow<LocationUpdate>(replay = 1)
    
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "rider_location_channel"
        private const val NOTIFICATION_ID = 1001
        private const val LOCATION_UPDATE_INTERVAL = 5000L // 5 seconds
        private const val FASTEST_LOCATION_INTERVAL = 3000L // 3 seconds
        
        // Static flow to share location updates with other components
        private val _sharedLocationUpdates = MutableSharedFlow<LocationUpdate>(replay = 1)
        val locationUpdates: SharedFlow<LocationUpdate> = _sharedLocationUpdates.asSharedFlow()
        
        private var isRunning = false
        
        fun isServiceRunning() = isRunning
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "LocationService created")
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val update = LocationUpdate(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        bearing = location.bearing,
                        speed = location.speed,
                        timestamp = location.time
                    )
                    
                    serviceScope.launch {
                        _locationUpdates.emit(update)
                        _sharedLocationUpdates.emit(update)
                    }
                    
                    Log.d(tag, "Location update: ${location.latitude}, ${location.longitude}")
                }
            }
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "LocationService started")
        
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        startLocationUpdates()
        isRunning = true
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "LocationService destroyed")
        
        stopLocationUpdates()
        serviceScope.cancel()
        isRunning = false
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when the app is tracking your location for deliveries"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("KhabarLagbe Rider")
            .setContentText("Tracking your location for delivery")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(tag, "Location permission not granted")
            return
        }
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_LOCATION_INTERVAL)
            setWaitForAccurateLocation(false)
        }.build()
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        
        Log.d(tag, "Location updates started")
    }
    
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d(tag, "Location updates stopped")
    }
}

/**
 * Data class representing a location update
 */
data class LocationUpdate(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val bearing: Float,
    val speed: Float,
    val timestamp: Long
)
