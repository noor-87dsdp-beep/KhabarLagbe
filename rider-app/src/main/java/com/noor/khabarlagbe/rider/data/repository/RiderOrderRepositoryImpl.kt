package com.noor.khabarlagbe.rider.data.repository

import com.noor.khabarlagbe.rider.data.remote.api.IssueRequest
import com.noor.khabarlagbe.rider.data.remote.api.OrderStatusRequest
import com.noor.khabarlagbe.rider.data.remote.api.OtpRequest
import com.noor.khabarlagbe.rider.data.remote.api.RiderApi
import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiderOrderRepositoryImpl @Inject constructor(
    private val api: RiderApi
) : RiderOrderRepository {
    
    private val _availableOrders = MutableStateFlow<List<RiderOrder>>(emptyList())
    private val _activeOrder = MutableStateFlow<RiderOrder?>(null)
    
    override suspend fun getAvailableOrders(): Result<List<RiderOrder>> {
        return try {
            val response = api.getAvailableOrders()
            if (response.isSuccessful && response.body() != null) {
                _availableOrders.value = response.body()!!
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("অর্ডার লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeAvailableOrders(): Flow<List<RiderOrder>> = _availableOrders.asStateFlow()
    
    override suspend fun acceptOrder(orderId: String): Result<RiderOrder> {
        return try {
            val response = api.acceptOrder(orderId)
            if (response.isSuccessful && response.body() != null) {
                val order = response.body()!!
                _activeOrder.value = order
                _availableOrders.value = _availableOrders.value.filter { it.id != orderId }
                Result.success(order)
            } else {
                Result.failure(Exception("অর্ডার গ্রহণ করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun rejectOrder(orderId: String): Result<Unit> {
        return try {
            val response = api.rejectOrder(orderId)
            if (response.isSuccessful) {
                _availableOrders.value = _availableOrders.value.filter { it.id != orderId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("অর্ডার বাতিল করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<RiderOrder> {
        return try {
            val response = api.updateOrderStatus(orderId, OrderStatusRequest(status))
            if (response.isSuccessful && response.body() != null) {
                val order = response.body()!!
                _activeOrder.value = order
                Result.success(order)
            } else {
                Result.failure(Exception("স্ট্যাটাস আপডেট করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveOrder(): Result<RiderOrder?> {
        return try {
            val response = api.getActiveOrder()
            if (response.isSuccessful) {
                _activeOrder.value = response.body()
                Result.success(response.body())
            } else {
                Result.failure(Exception("সক্রিয় অর্ডার লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeActiveOrder(): Flow<RiderOrder?> = _activeOrder.asStateFlow()
    
    override suspend fun verifyPickupOtp(orderId: String, otp: String): Result<Boolean> {
        return try {
            val response = api.verifyPickupOtp(orderId, OtpRequest(otp))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.verified)
            } else {
                Result.failure(Exception("OTP যাচাই করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyDeliveryOtp(orderId: String, otp: String): Result<Boolean> {
        return try {
            val response = api.verifyDeliveryOtp(orderId, OtpRequest(otp))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.verified)
            } else {
                Result.failure(Exception("OTP যাচাই করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun completeDelivery(orderId: String): Result<RiderOrder> {
        return try {
            val response = api.completeDelivery(orderId)
            if (response.isSuccessful && response.body() != null) {
                _activeOrder.value = null
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("ডেলিভারি সম্পন্ন করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun reportIssue(orderId: String, issue: String): Result<Unit> {
        return try {
            val response = api.reportIssue(orderId, IssueRequest(issue))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("সমস্যা রিপোর্ট করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
