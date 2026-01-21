package com.noor.khabarlagbe.restaurant.service

import android.util.Log
import com.noor.khabarlagbe.restaurant.domain.model.Order
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

sealed class SocketEvent {
    data class NewOrder(val orderData: JSONObject) : SocketEvent()
    data class OrderUpdated(val orderData: JSONObject) : SocketEvent()
    data class OrderCancelled(val orderId: String, val reason: String) : SocketEvent()
    data class RiderAssigned(val orderId: String, val riderData: JSONObject) : SocketEvent()
    data class RiderArrived(val orderId: String) : SocketEvent()
    object Connected : SocketEvent()
    object Disconnected : SocketEvent()
    data class Error(val message: String) : SocketEvent()
}

@Singleton
class RestaurantSocketManager @Inject constructor(
    private val authRepository: RestaurantAuthRepository
) {
    companion object {
        private const val TAG = "RestaurantSocketManager"
        private const val SOCKET_URL = "https://socket.khabarlagbe.com"
        
        // Events to listen
        private const val EVENT_NEW_ORDER = "new_order"
        private const val EVENT_ORDER_UPDATED = "order_updated"
        private const val EVENT_ORDER_CANCELLED = "order_cancelled"
        private const val EVENT_RIDER_ASSIGNED = "rider_assigned"
        private const val EVENT_RIDER_ARRIVED = "rider_arrived"
        
        // Events to emit
        private const val EVENT_JOIN_RESTAURANT = "join_restaurant"
        private const val EVENT_LEAVE_RESTAURANT = "leave_restaurant"
        private const val EVENT_ORDER_ACCEPTED = "order_accepted"
        private const val EVENT_ORDER_REJECTED = "order_rejected"
        private const val EVENT_ORDER_PREPARING = "order_preparing"
        private const val EVENT_ORDER_READY = "order_ready"
        private const val EVENT_RESTAURANT_ONLINE = "restaurant_online"
        private const val EVENT_RESTAURANT_OFFLINE = "restaurant_offline"
    }
    
    private var socket: Socket? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val _events = MutableSharedFlow<SocketEvent>()
    val events: SharedFlow<SocketEvent> = _events.asSharedFlow()
    
    private var isConnected = false
    private var restaurantId: String? = null
    
    fun connect(restaurantId: String) {
        if (isConnected && this.restaurantId == restaurantId) {
            Log.d(TAG, "Already connected to restaurant: $restaurantId")
            return
        }
        
        this.restaurantId = restaurantId
        val token = authRepository.getToken() ?: return
        
        try {
            val options = IO.Options().apply {
                auth = mapOf("token" to token)
                reconnection = true
                reconnectionDelay = 1000
                reconnectionAttempts = 10
                timeout = 20000
            }
            
            socket = IO.socket(SOCKET_URL, options).apply {
                on(Socket.EVENT_CONNECT, onConnect)
                on(Socket.EVENT_DISCONNECT, onDisconnect)
                on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                on(EVENT_NEW_ORDER, onNewOrder)
                on(EVENT_ORDER_UPDATED, onOrderUpdated)
                on(EVENT_ORDER_CANCELLED, onOrderCancelled)
                on(EVENT_RIDER_ASSIGNED, onRiderAssigned)
                on(EVENT_RIDER_ARRIVED, onRiderArrived)
                connect()
            }
            
            Log.d(TAG, "Connecting to socket server...")
        } catch (e: Exception) {
            Log.e(TAG, "Socket connection error", e)
            emitEvent(SocketEvent.Error(e.message ?: "Connection failed"))
        }
    }
    
    fun disconnect() {
        restaurantId?.let { id ->
            socket?.emit(EVENT_LEAVE_RESTAURANT, JSONObject().put("restaurantId", id))
        }
        socket?.disconnect()
        socket?.off()
        socket = null
        isConnected = false
        restaurantId = null
        Log.d(TAG, "Disconnected from socket server")
    }
    
    private val onConnect = Emitter.Listener {
        Log.d(TAG, "Socket connected")
        isConnected = true
        emitEvent(SocketEvent.Connected)
        
        restaurantId?.let { id ->
            socket?.emit(EVENT_JOIN_RESTAURANT, JSONObject().apply {
                put("restaurantId", id)
            })
        }
    }
    
    private val onDisconnect = Emitter.Listener {
        Log.d(TAG, "Socket disconnected")
        isConnected = false
        emitEvent(SocketEvent.Disconnected)
    }
    
    private val onConnectError = Emitter.Listener { args ->
        val error = args.firstOrNull()?.toString() ?: "Unknown error"
        Log.e(TAG, "Socket connect error: $error")
        emitEvent(SocketEvent.Error(error))
    }
    
    private val onNewOrder = Emitter.Listener { args ->
        try {
            val data = args.firstOrNull() as? JSONObject ?: return@Listener
            Log.d(TAG, "New order received: ${data.optString("id")}")
            emitEvent(SocketEvent.NewOrder(data))
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing new order", e)
        }
    }
    
    private val onOrderUpdated = Emitter.Listener { args ->
        try {
            val data = args.firstOrNull() as? JSONObject ?: return@Listener
            Log.d(TAG, "Order updated: ${data.optString("id")}")
            emitEvent(SocketEvent.OrderUpdated(data))
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing order update", e)
        }
    }
    
    private val onOrderCancelled = Emitter.Listener { args ->
        try {
            val data = args.firstOrNull() as? JSONObject ?: return@Listener
            val orderId = data.optString("orderId")
            val reason = data.optString("reason", "")
            Log.d(TAG, "Order cancelled: $orderId")
            emitEvent(SocketEvent.OrderCancelled(orderId, reason))
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing order cancellation", e)
        }
    }
    
    private val onRiderAssigned = Emitter.Listener { args ->
        try {
            val data = args.firstOrNull() as? JSONObject ?: return@Listener
            val orderId = data.optString("orderId")
            val riderData = data.optJSONObject("rider") ?: JSONObject()
            Log.d(TAG, "Rider assigned to order: $orderId")
            emitEvent(SocketEvent.RiderAssigned(orderId, riderData))
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing rider assignment", e)
        }
    }
    
    private val onRiderArrived = Emitter.Listener { args ->
        try {
            val data = args.firstOrNull() as? JSONObject ?: return@Listener
            val orderId = data.optString("orderId")
            Log.d(TAG, "Rider arrived for order: $orderId")
            emitEvent(SocketEvent.RiderArrived(orderId))
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing rider arrival", e)
        }
    }
    
    fun emitOrderAccepted(orderId: String, estimatedPrepTime: Int) {
        socket?.emit(EVENT_ORDER_ACCEPTED, JSONObject().apply {
            put("orderId", orderId)
            put("estimatedPrepTime", estimatedPrepTime)
        })
    }
    
    fun emitOrderRejected(orderId: String, reason: String) {
        socket?.emit(EVENT_ORDER_REJECTED, JSONObject().apply {
            put("orderId", orderId)
            put("reason", reason)
        })
    }
    
    fun emitOrderPreparing(orderId: String) {
        socket?.emit(EVENT_ORDER_PREPARING, JSONObject().apply {
            put("orderId", orderId)
        })
    }
    
    fun emitOrderReady(orderId: String) {
        socket?.emit(EVENT_ORDER_READY, JSONObject().apply {
            put("orderId", orderId)
        })
    }
    
    fun setRestaurantOnline() {
        restaurantId?.let { id ->
            socket?.emit(EVENT_RESTAURANT_ONLINE, JSONObject().apply {
                put("restaurantId", id)
            })
        }
    }
    
    fun setRestaurantOffline() {
        restaurantId?.let { id ->
            socket?.emit(EVENT_RESTAURANT_OFFLINE, JSONObject().apply {
                put("restaurantId", id)
            })
        }
    }
    
    private fun emitEvent(event: SocketEvent) {
        scope.launch {
            _events.emit(event)
        }
    }
    
    fun isConnected(): Boolean = isConnected
}
