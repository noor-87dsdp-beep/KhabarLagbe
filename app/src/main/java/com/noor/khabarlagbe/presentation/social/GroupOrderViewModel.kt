package com.noor.khabarlagbe.presentation.social

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
class GroupOrderViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<GroupOrderUiState>(GroupOrderUiState.Initial)
    val uiState: StateFlow<GroupOrderUiState> = _uiState.asStateFlow()

    private val _activeGroupOrders = MutableStateFlow<List<GroupOrder>>(emptyList())
    val activeGroupOrders: StateFlow<List<GroupOrder>> = _activeGroupOrders.asStateFlow()

    init {
        loadActiveGroupOrders()
    }

    fun loadActiveGroupOrders() {
        viewModelScope.launch {
            _activeGroupOrders.value = getSampleGroupOrders()
        }
    }

    fun createGroupOrder(restaurantId: String, restaurantName: String, deadline: Long) {
        viewModelScope.launch {
            _uiState.value = GroupOrderUiState.Loading
            kotlinx.coroutines.delay(500)
            
            val newGroupOrder = GroupOrder(
                id = "go_${System.currentTimeMillis()}",
                hostUserId = "user_1",
                hostName = "You",
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                status = GroupOrderStatus.OPEN,
                participants = listOf(
                    GroupOrderParticipant("user_1", "You", null, emptyList(), 0.0, false)
                ),
                deadline = deadline,
                shareCode = generateShareCode()
            )
            
            _activeGroupOrders.value = listOf(newGroupOrder) + _activeGroupOrders.value
            _uiState.value = GroupOrderUiState.Created(newGroupOrder)
        }
    }

    fun joinGroupOrder(shareCode: String) {
        viewModelScope.launch {
            _uiState.value = GroupOrderUiState.Loading
            kotlinx.coroutines.delay(500)
            
            val groupOrder = _activeGroupOrders.value.find { it.shareCode == shareCode }
            if (groupOrder != null) {
                _uiState.value = GroupOrderUiState.Joined(groupOrder)
            } else {
                _uiState.value = GroupOrderUiState.Error("Invalid share code")
            }
        }
    }

    fun addItemToGroupOrder(groupOrderId: String, item: CartItem) {
        viewModelScope.launch {
            _activeGroupOrders.value = _activeGroupOrders.value.map { order ->
                if (order.id == groupOrderId) {
                    val updatedParticipants = order.participants.map { participant ->
                        if (participant.userId == "user_1") {
                            participant.copy(
                                items = participant.items + item,
                                subtotal = participant.subtotal + item.totalPrice
                            )
                        } else participant
                    }
                    order.copy(participants = updatedParticipants)
                } else order
            }
        }
    }

    fun confirmParticipation(groupOrderId: String) {
        viewModelScope.launch {
            _activeGroupOrders.value = _activeGroupOrders.value.map { order ->
                if (order.id == groupOrderId) {
                    order.copy(
                        participants = order.participants.map { p ->
                            if (p.userId == "user_1") p.copy(hasConfirmed = true) else p
                        }
                    )
                } else order
            }
        }
    }

    fun submitGroupOrder(groupOrderId: String) {
        viewModelScope.launch {
            _activeGroupOrders.value = _activeGroupOrders.value.map { order ->
                if (order.id == groupOrderId) {
                    order.copy(status = GroupOrderStatus.SUBMITTED)
                } else order
            }
            _uiState.value = GroupOrderUiState.Submitted
        }
    }

    private fun generateShareCode(): String {
        val secureRandom = java.security.SecureRandom()
        return (100000 + secureRandom.nextInt(900000)).toString()
    }

    private fun getSampleGroupOrders(): List<GroupOrder> = listOf(
        GroupOrder(
            id = "go_1",
            hostUserId = "user_2",
            hostName = "Ahmed",
            restaurantId = "rest_1",
            restaurantName = "Star Kabab",
            status = GroupOrderStatus.OPEN,
            participants = listOf(
                GroupOrderParticipant("user_2", "Ahmed", null, emptyList(), 350.0, true),
                GroupOrderParticipant("user_3", "Sara", null, emptyList(), 280.0, false)
            ),
            deadline = System.currentTimeMillis() + 3600000,
            shareCode = "123456"
        )
    )
}

sealed class GroupOrderUiState {
    object Initial : GroupOrderUiState()
    object Loading : GroupOrderUiState()
    data class Created(val groupOrder: GroupOrder) : GroupOrderUiState()
    data class Joined(val groupOrder: GroupOrder) : GroupOrderUiState()
    object Submitted : GroupOrderUiState()
    data class Error(val message: String) : GroupOrderUiState()
}
