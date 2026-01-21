package com.noor.khabarlagbe.rider.presentation.components

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiderSocketManager @Inject constructor() {
    
    private var socket: Socket? = null
    private val TAG = "RiderSocketManager"
    
    companion object {
        private const val SOCKET_URL = "https://api.khabarlagbe.com"
        
        // Events to emit
        const val EVENT_JOIN_RIDER = "rider:join"
        const val EVENT_LOCATION_UPDATE = "rider:location_update"
        const val EVENT_ORDER_ACCEPT = "order:accept"
        const val EVENT_ORDER_REJECT = "order:reject"
        const val EVENT_STATUS_UPDATE = "order:status_update"
        const val EVENT_GO_ONLINE = "rider:go_online"
        const val EVENT_GO_OFFLINE = "rider:go_offline"
        
        // Events to listen
        const val EVENT_NEW_ORDER = "order:new"
        const val EVENT_ORDER_ASSIGNED = "order:assigned"
        const val EVENT_ORDER_CANCELLED = "order:cancelled"
        const val EVENT_ORDER_UPDATED = "order:updated"
        const val EVENT_CUSTOMER_LOCATION = "customer:location"
    }
    
    fun connect(riderId: String, token: String) {
        try {
            val options = IO.Options().apply {
                forceNew = true
                reconnection = true
                reconnectionAttempts = 10
                reconnectionDelay = 1000
                timeout = 10000
                auth = mapOf("token" to token)
            }
            
            socket = IO.socket(SOCKET_URL, options)
            
            socket?.on(Socket.EVENT_CONNECT) {
                Log.d(TAG, "Socket connected")
                joinRiderRoom(riderId)
            }
            
            socket?.on(Socket.EVENT_DISCONNECT) {
                Log.d(TAG, "Socket disconnected")
            }
            
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e(TAG, "Socket connection error: ${args.firstOrNull()}")
            }
            
            socket?.connect()
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting socket: ${e.message}")
        }
    }
    
    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
    
    private fun joinRiderRoom(riderId: String) {
        socket?.emit(EVENT_JOIN_RIDER, JSONObject().apply {
            put("riderId", riderId)
        })
    }
    
    fun updateLocation(latitude: Double, longitude: Double, bearing: Float? = null, speed: Float? = null) {
        socket?.emit(EVENT_LOCATION_UPDATE, JSONObject().apply {
            put("latitude", latitude)
            put("longitude", longitude)
            bearing?.let { put("bearing", it) }
            speed?.let { put("speed", it) }
            put("timestamp", System.currentTimeMillis())
        })
    }
    
    fun goOnline() {
        socket?.emit(EVENT_GO_ONLINE, JSONObject())
    }
    
    fun goOffline() {
        socket?.emit(EVENT_GO_OFFLINE, JSONObject())
    }
    
    fun acceptOrder(orderId: String) {
        socket?.emit(EVENT_ORDER_ACCEPT, JSONObject().apply {
            put("orderId", orderId)
        })
    }
    
    fun rejectOrder(orderId: String, reason: String? = null) {
        socket?.emit(EVENT_ORDER_REJECT, JSONObject().apply {
            put("orderId", orderId)
            reason?.let { put("reason", it) }
        })
    }
    
    fun updateOrderStatus(orderId: String, status: String) {
        socket?.emit(EVENT_STATUS_UPDATE, JSONObject().apply {
            put("orderId", orderId)
            put("status", status)
        })
    }
    
    fun observeNewOrders(): Flow<NewOrderEvent> = callbackFlow {
        val listener = Emitter.Listener { args ->
            try {
                val data = args.firstOrNull() as? JSONObject
                data?.let {
                    val event = NewOrderEvent(
                        orderId = it.getString("orderId"),
                        restaurantName = it.optString("restaurantName", ""),
                        deliveryAddress = it.optString("deliveryAddress", ""),
                        distance = it.optDouble("distance", 0.0),
                        deliveryFee = it.optDouble("deliveryFee", 0.0),
                        estimatedTime = it.optInt("estimatedTime", 0)
                    )
                    trySend(event)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing new order: ${e.message}")
            }
        }
        
        socket?.on(EVENT_NEW_ORDER, listener)
        
        awaitClose {
            socket?.off(EVENT_NEW_ORDER, listener)
        }
    }
    
    fun observeOrderCancelled(): Flow<String> = callbackFlow {
        val listener = Emitter.Listener { args ->
            try {
                val data = args.firstOrNull() as? JSONObject
                data?.let {
                    val orderId = it.getString("orderId")
                    trySend(orderId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing cancelled order: ${e.message}")
            }
        }
        
        socket?.on(EVENT_ORDER_CANCELLED, listener)
        
        awaitClose {
            socket?.off(EVENT_ORDER_CANCELLED, listener)
        }
    }
    
    fun observeCustomerLocation(): Flow<CustomerLocationEvent> = callbackFlow {
        val listener = Emitter.Listener { args ->
            try {
                val data = args.firstOrNull() as? JSONObject
                data?.let {
                    val event = CustomerLocationEvent(
                        customerId = it.getString("customerId"),
                        latitude = it.getDouble("latitude"),
                        longitude = it.getDouble("longitude")
                    )
                    trySend(event)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing customer location: ${e.message}")
            }
        }
        
        socket?.on(EVENT_CUSTOMER_LOCATION, listener)
        
        awaitClose {
            socket?.off(EVENT_CUSTOMER_LOCATION, listener)
        }
    }
    
    fun isConnected(): Boolean = socket?.connected() == true
}

data class NewOrderEvent(
    val orderId: String,
    val restaurantName: String,
    val deliveryAddress: String,
    val distance: Double,
    val deliveryFee: Double,
    val estimatedTime: Int
)

data class CustomerLocationEvent(
    val customerId: String,
    val latitude: Double,
    val longitude: Double
)
