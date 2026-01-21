package com.noor.khabarlagbe.restaurant.data.repository

import com.noor.khabarlagbe.restaurant.data.api.RestaurantApi
import com.noor.khabarlagbe.restaurant.data.dto.*
import com.noor.khabarlagbe.restaurant.data.local.dao.OrderDao
import com.noor.khabarlagbe.restaurant.data.local.dao.OrderItemDao
import com.noor.khabarlagbe.restaurant.data.local.entity.OrderEntity
import com.noor.khabarlagbe.restaurant.data.local.entity.OrderItemEntity
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantOrderRepositoryImpl @Inject constructor(
    private val api: RestaurantApi,
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val authRepository: RestaurantAuthRepository
) : RestaurantOrderRepository {
    
    private fun getAuthToken(): String {
        return "Bearer ${authRepository.getToken() ?: ""}"
    }
    
    override fun getOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getOrdersByStatus(status: OrderStatusEnum): Flow<List<Order>> {
        return orderDao.getOrdersByStatus(status.toApiString()).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val response = api.getOrderById(getAuthToken(), orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()!!.data!!.toDomain()
                orderDao.insertOrder(order.toEntity())
                orderItemDao.deleteOrderItems(orderId)
                orderItemDao.insertOrderItems(order.items.map { it.toEntity(orderId) })
                Result.success(order)
            } else {
                val cached = orderDao.getOrderById(orderId)
                if (cached != null) {
                    Result.success(cached.toDomain())
                } else {
                    Result.failure(Exception(response.body()?.message ?: "Order not found"))
                }
            }
        } catch (e: Exception) {
            val cached = orderDao.getOrderById(orderId)
            if (cached != null) {
                Result.success(cached.toDomain())
            } else {
                Result.failure(e)
            }
        }
    }
    
    override fun observeOrder(orderId: String): Flow<Order?> {
        return orderDao.observeOrder(orderId).map { it?.toDomain() }
    }
    
    override suspend fun acceptOrder(orderId: String, estimatedPrepTime: Int): Result<Order> {
        return try {
            val request = UpdateOrderStatusRequestDto(
                status = OrderStatus.ACCEPTED,
                estimatedPrepTime = estimatedPrepTime
            )
            val response = api.acceptOrder(getAuthToken(), orderId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()!!.data!!.toDomain()
                orderDao.insertOrder(order.toEntity())
                Result.success(order)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to accept order"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun rejectOrder(orderId: String, reason: String): Result<Order> {
        return try {
            val response = api.rejectOrder(getAuthToken(), orderId, RejectOrderRequestDto(reason))
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()!!.data!!.toDomain()
                orderDao.insertOrder(order.toEntity())
                Result.success(order)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to reject order"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markPreparing(orderId: String): Result<Order> {
        return try {
            val response = api.markPreparing(getAuthToken(), orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()!!.data!!.toDomain()
                orderDao.insertOrder(order.toEntity())
                Result.success(order)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update order"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markReady(orderId: String): Result<Order> {
        return try {
            val response = api.markReady(getAuthToken(), orderId)
            if (response.isSuccessful && response.body()?.success == true) {
                val order = response.body()!!.data!!.toDomain()
                orderDao.insertOrder(order.toEntity())
                Result.success(order)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to update order"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshOrders(status: String?): Result<List<Order>> {
        return try {
            val response = api.getOrders(getAuthToken(), status = status)
            if (response.isSuccessful && response.body()?.success == true) {
                val orders = response.body()!!.data!!.orders.map { it.toDomain() }
                orderDao.insertOrders(orders.map { it.toEntity() })
                orders.forEach { order ->
                    orderItemDao.deleteOrderItems(order.id)
                    orderItemDao.insertOrderItems(order.items.map { it.toEntity(order.id) })
                }
                Result.success(orders)
            } else {
                Result.failure(Exception("Failed to fetch orders"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRestaurantStats(): Result<RestaurantStats> {
        return try {
            val response = api.getStats(getAuthToken())
            if (response.isSuccessful && response.body()?.success == true) {
                val stats = response.body()!!.data!!
                Result.success(
                    RestaurantStats(
                        todayOrders = stats.todayOrders,
                        todayRevenue = stats.todayRevenue,
                        averageRating = stats.averageRating,
                        totalReviews = stats.totalReviews,
                        pendingOrders = stats.pendingOrders,
                        preparingOrders = stats.preparingOrders,
                        readyOrders = stats.readyOrders,
                        completedOrders = stats.completedOrders
                    )
                )
            } else {
                Result.failure(Exception("Failed to fetch stats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun OrderDto.toDomain(): Order {
        return Order(
            id = id,
            orderNumber = orderNumber,
            customerId = customerId,
            customerName = customerName,
            customerPhone = customerPhone,
            customerAddress = Address(
                street = customerAddress.street,
                city = customerAddress.city,
                area = customerAddress.area,
                postalCode = customerAddress.postalCode,
                latitude = customerAddress.latitude,
                longitude = customerAddress.longitude
            ),
            items = items.map { it.toDomain() },
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            packagingCharge = packagingCharge,
            discount = discount,
            total = total,
            status = OrderStatusEnum.fromString(status),
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            specialInstructions = specialInstructions,
            estimatedPrepTime = estimatedPrepTime,
            riderId = riderId,
            riderName = riderName,
            riderPhone = riderPhone,
            timeline = timeline.map { OrderTimeline(it.status, it.timestamp, it.note) },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun OrderItemDto.toDomain(): OrderItem {
        return OrderItem(
            id = id,
            menuItemId = menuItemId,
            name = name,
            nameEn = nameEn,
            quantity = quantity,
            price = price,
            totalPrice = totalPrice,
            customizations = customizations?.map { OrderCustomization(it.name, it.option, it.price) },
            specialInstructions = specialInstructions
        )
    }
    
    private fun Order.toEntity(): OrderEntity {
        return OrderEntity(
            id = id,
            orderNumber = orderNumber,
            customerId = customerId,
            customerName = customerName,
            customerPhone = customerPhone,
            customerStreet = customerAddress.street,
            customerCity = customerAddress.city,
            customerArea = customerAddress.area,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            packagingCharge = packagingCharge,
            discount = discount,
            total = total,
            status = status.toApiString(),
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            specialInstructions = specialInstructions,
            estimatedPrepTime = estimatedPrepTime,
            riderId = riderId,
            riderName = riderName,
            riderPhone = riderPhone,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun OrderItem.toEntity(orderId: String): OrderItemEntity {
        return OrderItemEntity(
            id = id,
            orderId = orderId,
            menuItemId = menuItemId,
            name = name,
            nameEn = nameEn,
            quantity = quantity,
            price = price,
            totalPrice = totalPrice,
            specialInstructions = specialInstructions
        )
    }
    
    private fun OrderEntity.toDomain(): Order {
        return Order(
            id = id,
            orderNumber = orderNumber,
            customerId = customerId,
            customerName = customerName,
            customerPhone = customerPhone,
            customerAddress = Address(customerStreet, customerCity, customerArea, "", 0.0, 0.0),
            items = emptyList(),
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            packagingCharge = packagingCharge,
            discount = discount,
            total = total,
            status = OrderStatusEnum.fromString(status),
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            specialInstructions = specialInstructions,
            estimatedPrepTime = estimatedPrepTime,
            riderId = riderId,
            riderName = riderName,
            riderPhone = riderPhone,
            timeline = emptyList(),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
