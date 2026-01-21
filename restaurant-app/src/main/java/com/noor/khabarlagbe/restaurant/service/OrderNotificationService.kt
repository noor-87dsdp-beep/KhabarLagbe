package com.noor.khabarlagbe.restaurant.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.noor.khabarlagbe.restaurant.MainActivity
import com.noor.khabarlagbe.restaurant.domain.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderNotificationService : FirebaseMessagingService() {
    
    companion object {
        private const val TAG = "OrderNotificationService"
        private const val CHANNEL_ID_ORDERS = "restaurant_orders"
        private const val CHANNEL_ID_GENERAL = "restaurant_general"
        private const val CHANNEL_NAME_ORDERS = "New Orders"
        private const val CHANNEL_NAME_GENERAL = "General Notifications"
        
        const val NOTIFICATION_TYPE_NEW_ORDER = "new_order"
        const val NOTIFICATION_TYPE_ORDER_CANCELLED = "order_cancelled"
        const val NOTIFICATION_TYPE_RIDER_ASSIGNED = "rider_assigned"
        const val NOTIFICATION_TYPE_RIDER_ARRIVED = "rider_arrived"
        const val NOTIFICATION_TYPE_GENERAL = "general"
    }
    
    @Inject
    lateinit var settingsRepository: SettingsRepository
    
    @Inject
    lateinit var soundManager: SoundManager
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        
        serviceScope.launch {
            try {
                settingsRepository.registerFcmToken(token)
                Log.d(TAG, "FCM token registered successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register FCM token", e)
            }
        }
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        val data = remoteMessage.data
        val notificationType = data["type"] ?: NOTIFICATION_TYPE_GENERAL
        
        when (notificationType) {
            NOTIFICATION_TYPE_NEW_ORDER -> handleNewOrderNotification(data)
            NOTIFICATION_TYPE_ORDER_CANCELLED -> handleOrderCancelledNotification(data)
            NOTIFICATION_TYPE_RIDER_ASSIGNED -> handleRiderAssignedNotification(data)
            NOTIFICATION_TYPE_RIDER_ARRIVED -> handleRiderArrivedNotification(data)
            else -> handleGeneralNotification(data, remoteMessage.notification)
        }
    }
    
    private fun handleNewOrderNotification(data: Map<String, String>) {
        val orderId = data["orderId"] ?: return
        val orderNumber = data["orderNumber"] ?: "New Order"
        val customerName = data["customerName"] ?: "Customer"
        val totalAmount = data["totalAmount"] ?: "0"
        val itemCount = data["itemCount"] ?: "0"
        
        soundManager.playNewOrderSound()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "order_details")
            putExtra("orderId", orderId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, orderId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_ORDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("নতুন অর্ডার! #$orderNumber")
            .setContentText("$customerName - $itemCount আইটেম - ৳$totalAmount")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("গ্রাহক: $customerName\nআইটেম: $itemCount টি\nমোট: ৳$totalAmount\n\nঅর্ডার গ্রহণ করতে ট্যাপ করুন")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(orderId.hashCode(), notification)
    }
    
    private fun handleOrderCancelledNotification(data: Map<String, String>) {
        val orderId = data["orderId"] ?: return
        val orderNumber = data["orderNumber"] ?: ""
        val reason = data["reason"] ?: "গ্রাহক বাতিল করেছেন"
        
        soundManager.playOrderUpdateSound()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "orders")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, orderId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_ORDERS)
            .setSmallIcon(android.R.drawable.ic_delete)
            .setContentTitle("অর্ডার বাতিল #$orderNumber")
            .setContentText(reason)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(orderId.hashCode(), notification)
    }
    
    private fun handleRiderAssignedNotification(data: Map<String, String>) {
        val orderId = data["orderId"] ?: return
        val orderNumber = data["orderNumber"] ?: ""
        val riderName = data["riderName"] ?: "রাইডার"
        val riderPhone = data["riderPhone"] ?: ""
        
        soundManager.playOrderUpdateSound()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "order_details")
            putExtra("orderId", orderId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, orderId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_ORDERS)
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setContentTitle("রাইডার এসাইন হয়েছে #$orderNumber")
            .setContentText("$riderName - $riderPhone")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(orderId.hashCode(), notification)
    }
    
    private fun handleRiderArrivedNotification(data: Map<String, String>) {
        val orderId = data["orderId"] ?: return
        val orderNumber = data["orderNumber"] ?: ""
        val riderName = data["riderName"] ?: "রাইডার"
        
        soundManager.playNewOrderSound()
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "order_details")
            putExtra("orderId", orderId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, orderId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_ORDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("রাইডার এসে গেছে! #$orderNumber")
            .setContentText("$riderName অর্ডার নিতে অপেক্ষা করছে")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(orderId.hashCode(), notification)
    }
    
    private fun handleGeneralNotification(data: Map<String, String>, notification: RemoteMessage.Notification?) {
        val title = notification?.title ?: data["title"] ?: "KhabarLagbe Restaurant"
        val body = notification?.body ?: data["body"] ?: ""
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder)
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Orders channel (high priority with sound and vibration)
            val ordersChannel = NotificationChannel(
                CHANNEL_ID_ORDERS,
                CHANNEL_NAME_ORDERS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new orders and order updates"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    android.media.AudioAttributes.Builder()
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(ordersChannel)
            
            // General channel
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                CHANNEL_NAME_GENERAL,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications from KhabarLagbe"
            }
            notificationManager.createNotificationChannel(generalChannel)
        }
    }
}
