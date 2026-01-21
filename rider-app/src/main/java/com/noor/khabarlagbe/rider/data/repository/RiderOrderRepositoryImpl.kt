package com.noor.khabarlagbe.rider.data.repository

import com.noor.khabarlagbe.rider.data.api.RiderApi
import com.noor.khabarlagbe.rider.data.dto.*
import com.noor.khabarlagbe.rider.data.local.dao.DeliveryHistoryDao
import com.noor.khabarlagbe.rider.data.local.dao.OrderDao
import com.noor.khabarlagbe.rider.data.local.entity.DeliveryHistoryEntity
import com.noor.khabarlagbe.rider.data.local.entity.OrderEntity
import com.noor.khabarlagbe.rider.domain.model.OrderStatus
import com.noor.khabarlagbe.rider.domain.model.RiderOrder
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiderOrderRepositoryImpl @Inject constructor(
    private val riderApi: RiderApi,
    private val orderDao: OrderDao,
    private val deliveryHistoryDao: DeliveryHistoryDao,
    private val authRepository: RiderAuthRepository
) : RiderOrderRepository {
    
    override val activeOrder: Flow<RiderOrder?> = orderDao.getActiveOrder().map { it?.toDomain() }
    
    override suspend fun getAvailableOrders(
        latitude: Double,
        longitude: Double,
        radius: Double
    ): Result<List<RiderOrder>> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getAvailableOrders(
                    "Bearer $token",
                    latitude,
                    longitude,
                    radius
                )
                if (response.isSuccessful && response.body() != null) {
                    val orders = response.body()!!.orders.map { it.toDomain() }
                    Result.success(orders)
                } else {
                    Result.failure(Exception("Failed to fetch available orders"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveOrder(): Result<RiderOrder?> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getActiveOrder("Bearer $token")
                if (response.isSuccessful) {
                    val orderDto = response.body()
                    if (orderDto != null) {
                        val order = orderDto.toDomain()
                        orderDao.insertOrder(OrderEntity.fromDomain(order))
                        Result.success(order)
                    } else {
                        Result.success(null)
                    }
                } else {
                    val localOrder = orderDao.getActiveOrderSync()?.toDomain()
                    Result.success(localOrder)
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            val localOrder = orderDao.getActiveOrderSync()?.toDomain()
            Result.success(localOrder)
        }
    }
    
    override suspend fun getOrderDetails(orderId: String): Result<RiderOrder> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getOrderDetails("Bearer $token", orderId)
                if (response.isSuccessful && response.body() != null) {
                    val order = response.body()!!.toDomain()
                    orderDao.insertOrder(OrderEntity.fromDomain(order))
                    Result.success(order)
                } else {
                    val localOrder = orderDao.getOrderByIdSync(orderId)?.toDomain()
                    if (localOrder != null) {
                        Result.success(localOrder)
                    } else {
                        Result.failure(Exception("Order not found"))
                    }
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            val localOrder = orderDao.getOrderByIdSync(orderId)?.toDomain()
            if (localOrder != null) {
                Result.success(localOrder)
            } else {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun acceptOrder(orderId: String): Result<RiderOrder> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.acceptOrder("Bearer $token", orderId)
                if (response.isSuccessful && response.body() != null) {
                    val order = response.body()!!.toDomain()
                    orderDao.insertOrder(OrderEntity.fromDomain(order))
                    Result.success(order)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Failed to accept order"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun rejectOrder(orderId: String, reason: String?): Result<Unit> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.rejectOrder(
                    "Bearer $token",
                    orderId,
                    RejectOrderRequest(reason)
                )
                if (response.isSuccessful) {
                    orderDao.deleteOrderById(orderId)
                    Result.success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Failed to reject order"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<RiderOrder> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.updateOrderStatus(
                    "Bearer $token",
                    orderId,
                    UpdateOrderStatusRequest(status.name)
                )
                if (response.isSuccessful && response.body() != null) {
                    val order = response.body()!!.toDomain()
                    orderDao.updateOrderStatus(orderId, status.name)
                    Result.success(order)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Failed to update order status"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            orderDao.updateOrderStatus(orderId, status.name)
            val localOrder = orderDao.getOrderByIdSync(orderId)?.toDomain()
            if (localOrder != null) {
                Result.success(localOrder)
            } else {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun markAsPickedUp(orderId: String): Result<RiderOrder> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.updateOrderStatus(
                    "Bearer $token",
                    orderId,
                    UpdateOrderStatusRequest(OrderStatus.PICKED_UP.name)
                )
                if (response.isSuccessful && response.body() != null) {
                    val order = response.body()!!.toDomain()
                    orderDao.markAsPickedUp(orderId, OrderStatus.PICKED_UP.name, System.currentTimeMillis())
                    Result.success(order)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Failed to mark as picked up"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            orderDao.markAsPickedUp(orderId, OrderStatus.PICKED_UP.name, System.currentTimeMillis())
            val localOrder = orderDao.getOrderByIdSync(orderId)?.toDomain()
            if (localOrder != null) {
                Result.success(localOrder)
            } else {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun markAsDelivered(
        orderId: String,
        proofImageUrl: String?,
        signature: String?,
        notes: String?
    ): Result<RiderOrder> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.completeDelivery(
                    "Bearer $token",
                    orderId,
                    CompleteDeliveryRequest(proofImageUrl, signature, notes)
                )
                if (response.isSuccessful && response.body() != null) {
                    val completionResponse = response.body()!!
                    val order = completionResponse.order.toDomain()
                    
                    orderDao.markAsDelivered(orderId, OrderStatus.DELIVERED.name, System.currentTimeMillis())
                    
                    val historyEntry = DeliveryHistoryEntity(
                        id = order.id,
                        orderNumber = null,
                        restaurantName = order.restaurantName,
                        customerName = order.customerName,
                        deliveryAddress = order.deliveryAddress,
                        totalAmount = order.totalAmount,
                        deliveryFee = order.deliveryFee,
                        tip = completionResponse.earnings.tip,
                        distance = order.distance,
                        status = OrderStatus.DELIVERED.name,
                        completedAt = System.currentTimeMillis(),
                        rating = null
                    )
                    deliveryHistoryDao.insertDelivery(historyEntry)
                    
                    Result.success(order)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Failed to complete delivery"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            orderDao.markAsDelivered(orderId, OrderStatus.DELIVERED.name, System.currentTimeMillis())
            val localOrder = orderDao.getOrderByIdSync(orderId)?.toDomain()
            if (localOrder != null) {
                Result.success(localOrder)
            } else {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun getDeliveryHistory(
        page: Int,
        limit: Int,
        startDate: String?,
        endDate: String?,
        status: String?
    ): Result<List<RiderOrder>> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getDeliveryHistory(
                    "Bearer $token",
                    page,
                    limit,
                    startDate,
                    endDate,
                    status
                )
                if (response.isSuccessful && response.body() != null) {
                    val orders = response.body()!!.deliveries.map { it.toDomain() }
                    
                    val historyEntities = orders.map { order ->
                        DeliveryHistoryEntity(
                            id = order.id,
                            orderNumber = null,
                            restaurantName = order.restaurantName,
                            customerName = order.customerName,
                            deliveryAddress = order.deliveryAddress,
                            totalAmount = order.totalAmount,
                            deliveryFee = order.deliveryFee,
                            tip = 0.0,
                            distance = order.distance,
                            status = order.status.name,
                            completedAt = order.deliveredAt ?: order.createdAt,
                            rating = null
                        )
                    }
                    deliveryHistoryDao.insertDeliveries(historyEntities)
                    
                    Result.success(orders)
                } else {
                    Result.failure(Exception("Failed to fetch delivery history"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getLocalDeliveryHistory(): Flow<List<RiderOrder>> {
        return deliveryHistoryDao.getAllDeliveries().map { entities ->
            entities.map { entity ->
                RiderOrder(
                    id = entity.id,
                    restaurantId = "",
                    restaurantName = entity.restaurantName,
                    restaurantAddress = "",
                    restaurantLocation = com.noor.khabarlagbe.rider.domain.model.Location(0.0, 0.0),
                    customerId = "",
                    customerName = entity.customerName,
                    customerPhone = "",
                    deliveryAddress = entity.deliveryAddress,
                    deliveryLocation = com.noor.khabarlagbe.rider.domain.model.Location(0.0, 0.0),
                    items = emptyList(),
                    totalAmount = entity.totalAmount,
                    deliveryFee = entity.deliveryFee,
                    distance = entity.distance,
                    estimatedTime = 0,
                    status = try { OrderStatus.valueOf(entity.status) } catch (e: Exception) { OrderStatus.DELIVERED },
                    createdAt = entity.completedAt,
                    acceptedAt = null,
                    pickedUpAt = null,
                    deliveredAt = entity.completedAt
                )
            }
        }
    }
    
    override suspend fun searchDeliveryHistory(query: String): List<RiderOrder> {
        return deliveryHistoryDao.searchDeliveries(query).first().map { entity ->
            RiderOrder(
                id = entity.id,
                restaurantId = "",
                restaurantName = entity.restaurantName,
                restaurantAddress = "",
                restaurantLocation = com.noor.khabarlagbe.rider.domain.model.Location(0.0, 0.0),
                customerId = "",
                customerName = entity.customerName,
                customerPhone = "",
                deliveryAddress = entity.deliveryAddress,
                deliveryLocation = com.noor.khabarlagbe.rider.domain.model.Location(0.0, 0.0),
                items = emptyList(),
                totalAmount = entity.totalAmount,
                deliveryFee = entity.deliveryFee,
                distance = entity.distance,
                estimatedTime = 0,
                status = try { OrderStatus.valueOf(entity.status) } catch (e: Exception) { OrderStatus.DELIVERED },
                createdAt = entity.completedAt,
                acceptedAt = null,
                pickedUpAt = null,
                deliveredAt = entity.completedAt
            )
        }
    }
    
    override suspend fun syncOrders() {
        try {
            getActiveOrder()
        } catch (e: Exception) {
            // Ignore sync errors
        }
    }
}
