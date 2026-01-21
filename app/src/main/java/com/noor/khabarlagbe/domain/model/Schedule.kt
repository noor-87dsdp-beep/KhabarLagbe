package com.noor.khabarlagbe.domain.model

data class ScheduledOrder(
    val id: String,
    val userId: String,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<CartItem>,
    val scheduledTime: Long,
    val deliveryAddress: Address,
    val status: ScheduledOrderStatus,
    val isRecurring: Boolean = false,
    val recurringPattern: RecurringPattern? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ScheduledOrderStatus {
    SCHEDULED,
    PROCESSING,
    CONVERTED_TO_ORDER,
    CANCELLED,
    FAILED
}

data class RecurringPattern(
    val frequency: RecurringFrequency,
    val dayOfWeek: List<Int> = emptyList(), // 1-7 for Sunday-Saturday
    val timeOfDay: String, // HH:mm format
    val startDate: Long,
    val endDate: Long? = null,
    val maxOccurrences: Int? = null,
    val currentOccurrences: Int = 0
)

enum class RecurringFrequency {
    DAILY,
    WEEKLY,
    BIWEEKLY,
    MONTHLY
}

data class MealPlan(
    val id: String,
    val userId: String,
    val name: String,
    val description: String? = null,
    val meals: List<PlannedMeal>,
    val isActive: Boolean = true,
    val startDate: Long,
    val endDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class PlannedMeal(
    val id: String,
    val dayOfWeek: Int, // 1-7
    val mealType: MealType,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<CartItem>,
    val estimatedCost: Double
)

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}
