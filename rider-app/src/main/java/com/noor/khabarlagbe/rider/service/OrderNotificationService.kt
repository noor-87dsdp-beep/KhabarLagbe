package com.noor.khabarlagbe.rider.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.noor.khabarlagbe.rider.MainActivity
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderNotificationService : FirebaseMessagingService() {
    
    @Inject
    lateinit var authRepository: RiderAuthRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    companion object {
        private const val TAG = "OrderNotificationService"
        private const val CHANNEL_ID_ORDERS = "rider_orders_channel"
        private const val CHANNEL_ID_GENERAL = "rider_general_channel"
        private const val NOTIFICATION_ID_ORDER = 2001
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        
        serviceScope.launch {
            try {
                authRepository.registerFcmToken(token)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register FCM token: ${e.message}")
            }
        }
    }
    
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message received: ${message.data}")
        
        val type = message.data["type"] ?: "general"
        
        when (type) {
            "new_order" -> handleNewOrderNotification(message)
            "order_cancelled" -> handleOrderCancelledNotification(message)
            "order_update" -> handleOrderUpdateNotification(message)
            "earnings" -> handleEarningsNotification(message)
            else -> handleGeneralNotification(message)
        }
    }
    
    private fun handleNewOrderNotification(message: RemoteMessage) {
        val orderId = message.data["orderId"] ?: return
        val restaurantName = message.data["restaurantName"] ?: "New Order"
        val deliveryFee = message.data["deliveryFee"] ?: "0"
        val distance = message.data["distance"] ?: "0"
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("orderId", orderId)
            putExtra("navigateTo", "order_details")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 
            orderId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_ORDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("নতুন অর্ডার!")
            .setContentText("$restaurantName • ৳$deliveryFee • $distance কিমি")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$restaurantName থেকে নতুন অর্ডার\nডেলিভারি ফি: ৳$deliveryFee\nদূরত্ব: $distance কিমি"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .addAction(
                android.R.drawable.ic_menu_view,
                "দেখুন",
                pendingIntent
            )
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_ORDER + orderId.hashCode(), notification)
    }
    
    private fun handleOrderCancelledNotification(message: RemoteMessage) {
        val orderId = message.data["orderId"] ?: return
        val reason = message.data["reason"] ?: "Order cancelled by customer"
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_ORDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("অর্ডার বাতিল")
            .setContentText("অর্ডার #${orderId.takeLast(6)} বাতিল হয়েছে")
            .setStyle(NotificationCompat.BigTextStyle().bigText(reason))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_ORDER + orderId.hashCode() + 1000, notification)
    }
    
    private fun handleOrderUpdateNotification(message: RemoteMessage) {
        val orderId = message.data["orderId"] ?: return
        val status = message.data["status"] ?: return
        val updateMessage = message.data["message"] ?: "Order status updated"
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("অর্ডার আপডেট")
            .setContentText(updateMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_ORDER + orderId.hashCode() + 2000, notification)
    }
    
    private fun handleEarningsNotification(message: RemoteMessage) {
        val amount = message.data["amount"] ?: return
        val title = message.data["title"] ?: "আয় জমা হয়েছে"
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateTo", "earnings")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("৳$amount জমা হয়েছে আপনার অ্যাকাউন্টে")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(3001, notification)
    }
    
    private fun handleGeneralNotification(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "KhabarLagbe Rider"
        val body = message.notification?.body ?: message.data["body"] ?: return
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(4001, notification)
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            // Orders channel (high priority)
            val ordersChannel = NotificationChannel(
                CHANNEL_ID_ORDERS,
                "New Orders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new delivery orders"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            
            // General channel
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications and updates"
            }
            
            notificationManager.createNotificationChannel(ordersChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }
}
