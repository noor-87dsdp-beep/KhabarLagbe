package com.noor.khabarlagbe.domain.model

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val imageUrl: String? = null,
    val actionUrl: String? = null,
    val data: Map<String, String> = emptyMap(),
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType {
    ORDER_UPDATE,
    PROMOTION,
    REWARD,
    SOCIAL,
    SYSTEM,
    REMINDER,
    DELIVERY
}

data class NotificationPreferences(
    val userId: String,
    val orderUpdates: Boolean = true,
    val promotions: Boolean = true,
    val rewards: Boolean = true,
    val socialActivity: Boolean = true,
    val reminders: Boolean = true,
    val newsletter: Boolean = false,
    val pushEnabled: Boolean = true,
    val emailEnabled: Boolean = true,
    val smsEnabled: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 22, // 10 PM
    val quietHoursEnd: Int = 8 // 8 AM
)
