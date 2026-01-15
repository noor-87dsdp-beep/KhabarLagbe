package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import kotlinx.coroutines.flow.Flow

interface RiderHistoryRepository {
    suspend fun getDeliveryHistory(page: Int, pageSize: Int): Result<List<RiderOrder>>
    suspend fun getOrderById(orderId: String): Result<RiderOrder>
    suspend fun searchOrders(query: String): Result<List<RiderOrder>>
    suspend fun getOrdersByDateRange(startDate: Long, endDate: Long): Result<List<RiderOrder>>
    fun observeDeliveryHistory(): Flow<List<RiderOrder>>
}
