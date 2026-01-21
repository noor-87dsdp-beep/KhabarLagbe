package com.noor.khabarlagbe.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleOrderViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Initial)
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val _scheduledOrders = MutableStateFlow<List<ScheduledOrder>>(emptyList())
    val scheduledOrders: StateFlow<List<ScheduledOrder>> = _scheduledOrders.asStateFlow()

    private val _mealPlans = MutableStateFlow<List<MealPlan>>(emptyList())
    val mealPlans: StateFlow<List<MealPlan>> = _mealPlans.asStateFlow()

    init {
        loadScheduledOrders()
        loadMealPlans()
    }

    fun loadScheduledOrders() {
        viewModelScope.launch {
            _scheduledOrders.value = getSampleScheduledOrders()
        }
    }

    fun loadMealPlans() {
        viewModelScope.launch {
            _mealPlans.value = getSampleMealPlans()
        }
    }

    fun scheduleOrder(
        restaurantId: String,
        restaurantName: String,
        items: List<CartItem>,
        scheduledTime: Long,
        address: Address,
        isRecurring: Boolean = false,
        recurringPattern: RecurringPattern? = null
    ) {
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            kotlinx.coroutines.delay(500)
            
            val newOrder = ScheduledOrder(
                id = "so_${System.currentTimeMillis()}",
                userId = "user_1",
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                items = items,
                scheduledTime = scheduledTime,
                deliveryAddress = address,
                status = ScheduledOrderStatus.SCHEDULED,
                isRecurring = isRecurring,
                recurringPattern = recurringPattern
            )
            
            _scheduledOrders.value = listOf(newOrder) + _scheduledOrders.value
            _uiState.value = ScheduleUiState.Scheduled(newOrder)
        }
    }

    fun cancelScheduledOrder(orderId: String) {
        viewModelScope.launch {
            _scheduledOrders.value = _scheduledOrders.value.map { order ->
                if (order.id == orderId) order.copy(status = ScheduledOrderStatus.CANCELLED)
                else order
            }
        }
    }

    fun createMealPlan(name: String, meals: List<PlannedMeal>) {
        viewModelScope.launch {
            val newPlan = MealPlan(
                id = "mp_${System.currentTimeMillis()}",
                userId = "user_1",
                name = name,
                meals = meals,
                isActive = true,
                startDate = System.currentTimeMillis()
            )
            _mealPlans.value = listOf(newPlan) + _mealPlans.value
        }
    }

    fun toggleMealPlan(planId: String) {
        viewModelScope.launch {
            _mealPlans.value = _mealPlans.value.map { plan ->
                if (plan.id == planId) plan.copy(isActive = !plan.isActive)
                else plan
            }
        }
    }

    private fun getSampleScheduledOrders(): List<ScheduledOrder> = listOf(
        ScheduledOrder(
            id = "so_1",
            userId = "user_1",
            restaurantId = "rest_1",
            restaurantName = "Star Kabab",
            items = emptyList(),
            scheduledTime = System.currentTimeMillis() + 86400000,
            deliveryAddress = Address(
                id = "addr_1", label = "Home", houseNo = "123", roadNo = "5",
                area = "Gulshan", thana = "Gulshan", district = "Dhaka",
                division = "Dhaka", postalCode = "1212", latitude = 23.7925, longitude = 90.4078
            ),
            status = ScheduledOrderStatus.SCHEDULED,
            isRecurring = false
        ),
        ScheduledOrder(
            id = "so_2",
            userId = "user_1",
            restaurantId = "rest_2",
            restaurantName = "Pizza Hut",
            items = emptyList(),
            scheduledTime = System.currentTimeMillis() + 172800000,
            deliveryAddress = Address(
                id = "addr_1", label = "Home", houseNo = "123", roadNo = "5",
                area = "Gulshan", thana = "Gulshan", district = "Dhaka",
                division = "Dhaka", postalCode = "1212", latitude = 23.7925, longitude = 90.4078
            ),
            status = ScheduledOrderStatus.SCHEDULED,
            isRecurring = true,
            recurringPattern = RecurringPattern(
                frequency = RecurringFrequency.WEEKLY,
                dayOfWeek = listOf(6),
                timeOfDay = "19:00",
                startDate = System.currentTimeMillis()
            )
        )
    )

    private fun getSampleMealPlans(): List<MealPlan> = listOf(
        MealPlan(
            id = "mp_1",
            userId = "user_1",
            name = "Weekday Lunch Plan",
            description = "Healthy lunches Monday to Friday",
            meals = listOf(
                PlannedMeal("pm_1", 2, MealType.LUNCH, "rest_3", "Madchef", emptyList(), 250.0),
                PlannedMeal("pm_2", 3, MealType.LUNCH, "rest_4", "Chillox", emptyList(), 300.0),
                PlannedMeal("pm_3", 4, MealType.LUNCH, "rest_1", "Star Kabab", emptyList(), 350.0)
            ),
            isActive = true,
            startDate = System.currentTimeMillis()
        )
    )
}

sealed class ScheduleUiState {
    object Initial : ScheduleUiState()
    object Loading : ScheduleUiState()
    data class Scheduled(val order: ScheduledOrder) : ScheduleUiState()
    data class Error(val message: String) : ScheduleUiState()
}
