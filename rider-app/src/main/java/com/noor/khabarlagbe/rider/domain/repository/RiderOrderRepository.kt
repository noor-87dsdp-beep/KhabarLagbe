package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import kotlinx.coroutines.flow.Flow

interface RiderOrderRepository {
    suspend fun getAvailableOrders(): Result<List<RiderOrder>>
    fun observeAvailableOrders(): Flow<List<RiderOrder>>
    suspend fun acceptOrder(orderId: String): Result<RiderOrder>
    suspend fun rejectOrder(orderId: String): Result<Unit>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<RiderOrder>
    suspend fun getActiveOrder(): Result<RiderOrder?>
    fun observeActiveOrder(): Flow<RiderOrder?>
    suspend fun verifyPickupOtp(orderId: String, otp: String): Result<Boolean>
    suspend fun verifyDeliveryOtp(orderId: String, otp: String): Result<Boolean>
    suspend fun completeDelivery(orderId: String): Result<RiderOrder>
    suspend fun reportIssue(orderId: String, issue: String): Result<Unit>
}
