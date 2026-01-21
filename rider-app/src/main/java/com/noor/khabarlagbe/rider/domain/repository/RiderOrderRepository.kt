package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import kotlinx.coroutines.flow.Flow

interface RiderOrderRepository {
    
    val activeOrder: Flow<RiderOrder?>
    
    suspend fun getAvailableOrders(
        latitude: Double,
        longitude: Double,
        radius: Double = 5.0
    ): Result<List<RiderOrder>>
    
    suspend fun getActiveOrder(): Result<RiderOrder?>
    
    suspend fun getOrderDetails(orderId: String): Result<RiderOrder>
    
    suspend fun acceptOrder(orderId: String): Result<RiderOrder>
    
    suspend fun rejectOrder(orderId: String, reason: String?): Result<Unit>
    
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<RiderOrder>
    
    suspend fun markAsPickedUp(orderId: String): Result<RiderOrder>
    
    suspend fun markAsDelivered(
        orderId: String,
        proofImageUrl: String? = null,
        signature: String? = null,
        notes: String? = null
    ): Result<RiderOrder>
    
    suspend fun getDeliveryHistory(
        page: Int = 1,
        limit: Int = 20,
        startDate: String? = null,
        endDate: String? = null,
        status: String? = null
    ): Result<List<RiderOrder>>
    
    fun getLocalDeliveryHistory(): Flow<List<RiderOrder>>
    
    suspend fun searchDeliveryHistory(query: String): List<RiderOrder>
    
    suspend fun syncOrders()
}
