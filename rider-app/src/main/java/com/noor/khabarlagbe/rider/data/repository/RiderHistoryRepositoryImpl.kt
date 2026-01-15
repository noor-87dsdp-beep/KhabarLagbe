package com.noor.khabarlagbe.rider.data.repository

import com.noor.khabarlagbe.rider.data.remote.api.RiderApi
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.RiderHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiderHistoryRepositoryImpl @Inject constructor(
    private val api: RiderApi
) : RiderHistoryRepository {
    
    private val _deliveryHistory = MutableStateFlow<List<RiderOrder>>(emptyList())
    
    override suspend fun getDeliveryHistory(page: Int, pageSize: Int): Result<List<RiderOrder>> {
        return try {
            val response = api.getDeliveryHistory(page, pageSize)
            if (response.isSuccessful && response.body() != null) {
                if (page == 1) {
                    _deliveryHistory.value = response.body()!!
                } else {
                    _deliveryHistory.value = _deliveryHistory.value + response.body()!!
                }
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("ইতিহাস লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOrderById(orderId: String): Result<RiderOrder> {
        return try {
            val response = api.getOrderById(orderId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("অর্ডার বিস্তারিত লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchOrders(query: String): Result<List<RiderOrder>> {
        return try {
            val response = api.searchOrders(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("অনুসন্ধান ব্যর্থ হয়েছে"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getOrdersByDateRange(startDate: Long, endDate: Long): Result<List<RiderOrder>> {
        return try {
            val response = api.getOrdersByDateRange(startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("অর্ডার লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeDeliveryHistory(): Flow<List<RiderOrder>> = _deliveryHistory.asStateFlow()
}
