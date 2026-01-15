package com.noor.khabarlagbe.util

import android.util.Log
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * NetworkErrorHandler - Handles different types of network errors
 * Provides user-friendly error messages and error type classification
 */
object NetworkErrorHandler {
    
    private const val TAG = "NetworkErrorHandler"
    
    /**
     * Parse exception and return appropriate error type
     */
    fun handleError(exception: Throwable): NetworkError {
        Log.e(TAG, "Network error occurred", exception)
        
        return when (exception) {
            is HttpException -> handleHttpException(exception)
            is UnknownHostException -> NetworkError.NoInternet(
                "ইন্টারনেট সংযোগ নেই। দয়া করে আপনার সংযোগ চেক করুন।"
            )
            is SocketTimeoutException -> NetworkError.Timeout(
                "সংযোগ টাইমআউট হয়েছে। আবার চেষ্টা করুন।"
            )
            is IOException -> NetworkError.Network(
                "নেটওয়ার্ক সমস্যা হয়েছে। আবার চেষ্টা করুন।"
            )
            else -> NetworkError.Unknown(
                exception.message ?: "একটি অজানা ত্রুটি ঘটেছে।"
            )
        }
    }
    
    /**
     * Handle HTTP exceptions based on status code
     */
    private fun handleHttpException(exception: HttpException): NetworkError {
        return when (exception.code()) {
            400 -> NetworkError.BadRequest(
                parseErrorMessage(exception) ?: "অবৈধ অনুরোধ। দয়া করে তথ্য চেক করুন।"
            )
            401 -> NetworkError.Unauthorized(
                "আপনার সেশন মেয়াদ উত্তীর্ণ হয়েছে। দয়া করে আবার লগইন করুন।"
            )
            403 -> NetworkError.Forbidden(
                "এই সংস্থানে অ্যাক্সেস নিষিদ্ধ।"
            )
            404 -> NetworkError.NotFound(
                "অনুরোধকৃত তথ্য পাওয়া যায়নি।"
            )
            408 -> NetworkError.Timeout(
                "অনুরোধ টাইমআউট হয়েছে। আবার চেষ্টা করুন।"
            )
            422 -> NetworkError.ValidationError(
                parseErrorMessage(exception) ?: "তথ্য যাচাইকরণ ব্যর্থ হয়েছে।"
            )
            429 -> NetworkError.TooManyRequests(
                "অনেক বেশি অনুরোধ। অনুগ্রহ করে কিছুক্ষণ পরে চেষ্টা করুন।"
            )
            in 500..599 -> NetworkError.ServerError(
                "সার্ভার সমস্যা হয়েছে। অনুগ্রহ করে পরে আবার চেষ্টা করুন।"
            )
            else -> NetworkError.Unknown(
                "একটি ত্রুটি ঘটেছে (কোড: ${exception.code()})"
            )
        }
    }
    
    /**
     * Parse error message from HTTP exception
     */
    private fun parseErrorMessage(exception: HttpException): String? {
        return try {
            exception.response()?.errorBody()?.string()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get user-friendly error message
     */
    fun getErrorMessage(error: NetworkError): String {
        return when (error) {
            is NetworkError.NoInternet -> error.message
            is NetworkError.Timeout -> error.message
            is NetworkError.Network -> error.message
            is NetworkError.BadRequest -> error.message
            is NetworkError.Unauthorized -> error.message
            is NetworkError.Forbidden -> error.message
            is NetworkError.NotFound -> error.message
            is NetworkError.ValidationError -> error.message
            is NetworkError.TooManyRequests -> error.message
            is NetworkError.ServerError -> error.message
            is NetworkError.Unknown -> error.message
        }
    }
    
    /**
     * Check if error requires authentication
     */
    fun requiresAuthentication(error: NetworkError): Boolean {
        return error is NetworkError.Unauthorized
    }
    
    /**
     * Check if error is retryable
     */
    fun isRetryable(error: NetworkError): Boolean {
        return when (error) {
            is NetworkError.NoInternet -> true
            is NetworkError.Timeout -> true
            is NetworkError.Network -> true
            is NetworkError.ServerError -> true
            is NetworkError.TooManyRequests -> true
            else -> false
        }
    }
}

/**
 * Sealed class representing different types of network errors
 */
sealed class NetworkError(open val message: String) {
    
    data class NoInternet(
        override val message: String
    ) : NetworkError(message)
    
    data class Timeout(
        override val message: String
    ) : NetworkError(message)
    
    data class Network(
        override val message: String
    ) : NetworkError(message)
    
    data class BadRequest(
        override val message: String
    ) : NetworkError(message)
    
    data class Unauthorized(
        override val message: String
    ) : NetworkError(message)
    
    data class Forbidden(
        override val message: String
    ) : NetworkError(message)
    
    data class NotFound(
        override val message: String
    ) : NetworkError(message)
    
    data class ValidationError(
        override val message: String
    ) : NetworkError(message)
    
    data class TooManyRequests(
        override val message: String
    ) : NetworkError(message)
    
    data class ServerError(
        override val message: String
    ) : NetworkError(message)
    
    data class Unknown(
        override val message: String
    ) : NetworkError(message)
}

/**
 * Result wrapper for network operations
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val error: NetworkError) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Extension function to handle network calls safely
 */
suspend fun <T> safeNetworkCall(
    call: suspend () -> T
): NetworkResult<T> {
    return try {
        NetworkResult.Success(call())
    } catch (e: Exception) {
        NetworkResult.Error(NetworkErrorHandler.handleError(e))
    }
}

/**
 * Extension function to map NetworkResult
 */
fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> NetworkResult.Error(error)
        is NetworkResult.Loading -> NetworkResult.Loading
    }
}

/**
 * Extension function to get data or null
 */
fun <T> NetworkResult<T>.getOrNull(): T? {
    return when (this) {
        is NetworkResult.Success -> data
        else -> null
    }
}

/**
 * Extension function to get error or null
 */
fun <T> NetworkResult<T>.getErrorOrNull(): NetworkError? {
    return when (this) {
        is NetworkResult.Error -> error
        else -> null
    }
}
