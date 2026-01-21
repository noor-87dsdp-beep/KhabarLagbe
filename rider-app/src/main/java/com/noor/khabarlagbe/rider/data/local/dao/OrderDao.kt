package com.noor.khabarlagbe.rider.data.local.dao

import androidx.room.*
import com.noor.khabarlagbe.rider.data.local.entity.DeliveryHistoryEntity
import com.noor.khabarlagbe.rider.data.local.entity.EarningsEntity
import com.noor.khabarlagbe.rider.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    
    // Active Order
    @Query("SELECT * FROM orders WHERE status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY createdAt DESC LIMIT 1")
    fun getActiveOrder(): Flow<OrderEntity?>
    
    @Query("SELECT * FROM orders WHERE status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY createdAt DESC LIMIT 1")
    suspend fun getActiveOrderSync(): OrderEntity?
    
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderById(orderId: String): Flow<OrderEntity?>
    
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderByIdSync(orderId: String): OrderEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)
    
    @Update
    suspend fun updateOrder(order: OrderEntity)
    
    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
    
    @Query("UPDATE orders SET status = :status, pickedUpAt = :pickedUpAt WHERE id = :orderId")
    suspend fun markAsPickedUp(orderId: String, status: String, pickedUpAt: Long)
    
    @Query("UPDATE orders SET status = :status, deliveredAt = :deliveredAt WHERE id = :orderId")
    suspend fun markAsDelivered(orderId: String, status: String, deliveredAt: Long)
    
    @Delete
    suspend fun deleteOrder(order: OrderEntity)
    
    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: String)
    
    @Query("DELETE FROM orders WHERE status IN ('DELIVERED', 'CANCELLED')")
    suspend fun clearCompletedOrders()
    
    @Query("DELETE FROM orders")
    suspend fun clearAll()
}

@Dao
interface DeliveryHistoryDao {
    
    @Query("SELECT * FROM delivery_history ORDER BY completedAt DESC")
    fun getAllDeliveries(): Flow<List<DeliveryHistoryEntity>>
    
    @Query("SELECT * FROM delivery_history ORDER BY completedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getDeliveriesPaginated(limit: Int, offset: Int): List<DeliveryHistoryEntity>
    
    @Query("SELECT * FROM delivery_history WHERE completedAt BETWEEN :startDate AND :endDate ORDER BY completedAt DESC")
    fun getDeliveriesByDateRange(startDate: Long, endDate: Long): Flow<List<DeliveryHistoryEntity>>
    
    @Query("SELECT * FROM delivery_history WHERE restaurantName LIKE '%' || :query || '%' OR customerName LIKE '%' || :query || '%' OR deliveryAddress LIKE '%' || :query || '%' ORDER BY completedAt DESC")
    fun searchDeliveries(query: String): Flow<List<DeliveryHistoryEntity>>
    
    @Query("SELECT COUNT(*) FROM delivery_history")
    suspend fun getTotalCount(): Int
    
    @Query("SELECT COUNT(*) FROM delivery_history WHERE completedAt BETWEEN :startDate AND :endDate")
    suspend fun getCountByDateRange(startDate: Long, endDate: Long): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryHistoryEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveries(deliveries: List<DeliveryHistoryEntity>)
    
    @Delete
    suspend fun deleteDelivery(delivery: DeliveryHistoryEntity)
    
    @Query("DELETE FROM delivery_history")
    suspend fun clearAll()
}

@Dao
interface EarningsDao {
    
    @Query("SELECT * FROM earnings ORDER BY date DESC")
    fun getAllEarnings(): Flow<List<EarningsEntity>>
    
    @Query("SELECT * FROM earnings WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEarningsByDateRange(startDate: Long, endDate: Long): Flow<List<EarningsEntity>>
    
    @Query("SELECT SUM(amount + tip) FROM earnings WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalEarningsByDateRange(startDate: Long, endDate: Long): Double?
    
    @Query("SELECT COUNT(*) FROM earnings WHERE type = 'DELIVERY' AND date BETWEEN :startDate AND :endDate")
    suspend fun getDeliveryCountByDateRange(startDate: Long, endDate: Long): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarning(earning: EarningsEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEarnings(earnings: List<EarningsEntity>)
    
    @Delete
    suspend fun deleteEarning(earning: EarningsEntity)
    
    @Query("DELETE FROM earnings")
    suspend fun clearAll()
}
