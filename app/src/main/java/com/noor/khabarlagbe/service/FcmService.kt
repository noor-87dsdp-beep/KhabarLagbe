package com.noor.khabarlagbe.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.noor.khabarlagbe.MainActivity
import com.noor.khabarlagbe.R

/**
 * Firebase Cloud Messaging Service
 * 
 * NOTE: This is a conditional implementation. Firebase dependencies are commented out
 * in build.gradle.kts. To fully enable:
 * 1. Uncomment Firebase dependencies in build.gradle.kts
 * 2. Add google-services.json to app/ directory
 * 3. Uncomment the FirebaseMessagingService extension below
 * 4. Update AndroidManifest.xml service declaration
 * 
 * For now, this provides the structure with stub implementations.
 */
class FcmService : android.app.Service() {
    
    companion object {
        private const val TAG = "FcmService"
        
        // Notification Channels
        private const val CHANNEL_ORDER_UPDATES = "order_updates"
        private const val CHANNEL_PROMOTIONS = "promotions"
        private const val CHANNEL_RIDER_UPDATES = "rider_updates"
        private const val CHANNEL_GENERAL = "general"
        
        // Notification IDs
        private const val NOTIFICATION_ID_ORDER = 1001
        private const val NOTIFICATION_ID_PROMOTION = 1002
        private const val NOTIFICATION_ID_RIDER = 1003
        private const val NOTIFICATION_ID_GENERAL = 1004
    }
    
    override fun onBind(intent: Intent?) = null
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "FcmService created")
        createNotificationChannels(this)
    }
    
    /**
     * Create notification channels for Android O+
     * Channels must be created before showing notifications
     */
    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Order Updates Channel
            val orderChannel = NotificationChannel(
                CHANNEL_ORDER_UPDATES,
                "অর্ডার আপডেট",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "আপনার অর্ডারের স্ট্যাটাস আপডেট"
                enableVibration(true)
            }
            
            // Promotions Channel
            val promoChannel = NotificationChannel(
                CHANNEL_PROMOTIONS,
                "অফার ও প্রমোশন",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "বিশেষ অফার এবং ডিসকাউন্ট"
            }
            
            // Rider Updates Channel
            val riderChannel = NotificationChannel(
                CHANNEL_RIDER_UPDATES,
                "রাইডার আপডেট",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "ডেলিভারি রাইডার এবং ট্র্যাকিং আপডেট"
                enableVibration(true)
            }
            
            // General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "সাধারণ বিজ্ঞপ্তি",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "সাধারণ অ্যাপ বিজ্ঞপ্তি"
            }
            
            notificationManager.createNotificationChannels(
                listOf(orderChannel, promoChannel, riderChannel, generalChannel)
            )
            
            Log.d(TAG, "Notification channels created")
        }
    }
    
    /**
     * Show notification for order updates
     */
    private fun showOrderNotification(
        context: Context,
        title: String,
        message: String,
        orderId: String? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            orderId?.let { putExtra("orderId", it) }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // TODO: Replace ic_launcher_foreground with a dedicated notification icon
        // Create drawable/ic_notification.xml with proper sizing (24dp) and contrast
        val notification = NotificationCompat.Builder(context, CHANNEL_ORDER_UPDATES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_ORDER, notification)
    }
    
    /**
     * Show notification for promotions
     */
    private fun showPromotionNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_PROMOTIONS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_PROMOTION, notification)
    }
    
    /**
     * Show notification for rider assignment
     */
    private fun showRiderNotification(
        context: Context,
        title: String,
        message: String,
        orderId: String? = null
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            orderId?.let { putExtra("orderId", it) }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_RIDER_UPDATES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_RIDER, notification)
    }
    
    /**
     * Parse notification data and show appropriate notification
     */
    private fun handleNotification(data: Map<String, String>) {
        val type = data["type"] ?: "general"
        val title = data["title"] ?: "KhabarLagbe"
        val message = data["message"] ?: ""
        val orderId = data["orderId"]
        
        when (type) {
            "order_update" -> showOrderNotification(this, title, message, orderId)
            "promotion" -> showPromotionNotification(this, title, message)
            "rider_assigned" -> showRiderNotification(this, title, message, orderId)
            else -> showGeneralNotification(this, title, message)
        }
        
        Log.d(TAG, "Notification handled: type=$type, title=$title")
    }
    
    /**
     * Show general notification
     */
    private fun showGeneralNotification(
        context: Context,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_GENERAL, notification)
    }
}

/* 
 * IMPORTANT: Uncomment the code below when Firebase is fully configured
 * 
 * Replace the class above with this implementation:
 *
class FcmService : FirebaseMessagingService() {
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        // Send token to your server
        sendTokenToServer(token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        // Handle notification
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleNotification(remoteMessage.data)
        }
        
        // Handle notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message notification body: ${it.body}")
            showGeneralNotification(
                this,
                it.title ?: "KhabarLagbe",
                it.body ?: ""
            )
        }
    }
    
    private fun sendTokenToServer(token: String) {
        // TODO: Implement token upload to your backend
        Log.d(TAG, "Sending token to server: $token")
    }
}
*/
