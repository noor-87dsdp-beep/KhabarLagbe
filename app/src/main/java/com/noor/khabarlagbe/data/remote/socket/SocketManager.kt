package com.noor.khabarlagbe.data.remote.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SocketManager handles real-time communication via Socket.IO
 * Manages connection lifecycle, event listening, and error handling
 */
@Singleton
class SocketManager @Inject constructor() {
    
    private var socket: Socket? = null
    private var isConnected = false
    
    companion object {
        private const val TAG = "SocketManager"
        private const val BASE_URL = "http://localhost:3000"
        
        // Socket Events
        const val EVENT_CONNECT = Socket.EVENT_CONNECT
        const val EVENT_DISCONNECT = Socket.EVENT_DISCONNECT
        const val EVENT_CONNECT_ERROR = Socket.EVENT_CONNECT_ERROR
        
        // Custom Events
        const val EVENT_ORDER_STATUS_UPDATE = "order:status_update"
        const val EVENT_ORDER_RIDER_ASSIGNED = "order:rider_assigned"
        const val EVENT_RIDER_LOCATION_UPDATE = "rider:location_update"
        
        // Reconnection settings
        private const val MAX_RECONNECTION_ATTEMPTS = 5
        private const val RECONNECTION_DELAY = 1000L
    }
    
    /**
     * Initialize socket connection with authentication token
     */
    fun connect(authToken: String? = null) {
        if (isConnected) {
            Log.d(TAG, "Socket already connected")
            return
        }
        
        try {
            val options = IO.Options().apply {
                // Authentication
                if (!authToken.isNullOrEmpty()) {
                    auth = mapOf("token" to authToken)
                }
                
                // Reconnection settings
                reconnection = true
                reconnectionAttempts = MAX_RECONNECTION_ATTEMPTS
                reconnectionDelay = RECONNECTION_DELAY
                
                // Connection timeout
                timeout = 10000
                
                // Transport options
                transports = arrayOf("websocket", "polling")
            }
            
            socket = IO.socket(BASE_URL, options)
            
            socket?.apply {
                // Connection events
                on(EVENT_CONNECT) {
                    isConnected = true
                    Log.d(TAG, "Socket connected successfully")
                }
                
                on(EVENT_DISCONNECT) {
                    isConnected = false
                    Log.d(TAG, "Socket disconnected")
                }
                
                on(EVENT_CONNECT_ERROR) { args ->
                    isConnected = false
                    val error = args.firstOrNull()
                    Log.e(TAG, "Socket connection error: $error")
                }
                
                connect()
            }
            
        } catch (e: URISyntaxException) {
            Log.e(TAG, "Invalid socket URL", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing socket", e)
        }
    }
    
    /**
     * Disconnect socket connection
     */
    fun disconnect() {
        try {
            socket?.apply {
                off() // Remove all listeners
                disconnect()
                close()
            }
            socket = null
            isConnected = false
            Log.d(TAG, "Socket disconnected and cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting socket", e)
        }
    }
    
    /**
     * Emit an event to the server
     */
    fun emitEvent(event: String, data: JSONObject? = null) {
        if (!isConnected) {
            Log.w(TAG, "Cannot emit event '$event': Socket not connected")
            return
        }
        
        try {
            if (data != null) {
                socket?.emit(event, data)
            } else {
                socket?.emit(event)
            }
            Log.d(TAG, "Event emitted: $event")
        } catch (e: Exception) {
            Log.e(TAG, "Error emitting event '$event'", e)
        }
    }
    
    /**
     * Listen to an event from the server
     * Returns a Flow that emits received data
     */
    fun listenToEvent(event: String): Flow<JSONObject?> = callbackFlow {
        val listener = Emitter.Listener { args ->
            try {
                val data = args.firstOrNull() as? JSONObject
                trySend(data)
                Log.d(TAG, "Event received: $event, data: $data")
            } catch (e: Exception) {
                Log.e(TAG, "Error processing event '$event'", e)
                trySend(null)
            }
        }
        
        socket?.on(event, listener)
        
        awaitClose {
            socket?.off(event, listener)
            Log.d(TAG, "Stopped listening to event: $event")
        }
    }
    
    /**
     * Check if socket is currently connected
     */
    fun isConnected(): Boolean = isConnected
    
    /**
     * Reconnect to socket server
     */
    fun reconnect(authToken: String? = null) {
        disconnect()
        connect(authToken)
    }
    
    /**
     * Listen to order status updates
     */
    fun listenToOrderStatusUpdates(): Flow<JSONObject?> {
        return listenToEvent(EVENT_ORDER_STATUS_UPDATE)
    }
    
    /**
     * Listen to rider assignment updates
     */
    fun listenToRiderAssigned(): Flow<JSONObject?> {
        return listenToEvent(EVENT_ORDER_RIDER_ASSIGNED)
    }
    
    /**
     * Listen to rider location updates
     */
    fun listenToRiderLocationUpdates(): Flow<JSONObject?> {
        return listenToEvent(EVENT_RIDER_LOCATION_UPDATE)
    }
    
    /**
     * Join a specific order room for targeted updates
     */
    fun joinOrderRoom(orderId: String) {
        val data = JSONObject().apply {
            put("orderId", orderId)
        }
        emitEvent("join:order", data)
    }
    
    /**
     * Leave a specific order room
     */
    fun leaveOrderRoom(orderId: String) {
        val data = JSONObject().apply {
            put("orderId", orderId)
        }
        emitEvent("leave:order", data)
    }
}
