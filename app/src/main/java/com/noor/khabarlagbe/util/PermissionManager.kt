package com.noor.khabarlagbe.util

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * PermissionManager - Helper for managing runtime permissions
 * Uses Accompanist Permissions library for permission handling
 */
object PermissionManager {
    
    // Permission groups
    val LOCATION_PERMISSIONS = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    
    val CAMERA_PERMISSION = Manifest.permission.CAMERA
    
    val NOTIFICATION_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.POST_NOTIFICATIONS
    } else {
        null
    }
    
    val MEDIA_IMAGES_PERMISSION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

/**
 * Request location permissions (FINE and COARSE)
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberLocationPermissionsState(): MultiplePermissionsState {
    return rememberMultiplePermissionsState(
        permissions = PermissionManager.LOCATION_PERMISSIONS
    )
}

/**
 * Request camera permission
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberCameraPermissionState(): PermissionState {
    return rememberPermissionState(
        permission = PermissionManager.CAMERA_PERMISSION
    )
}

/**
 * Request notification permission (Android 13+)
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberNotificationPermissionState(): PermissionState? {
    val permission = PermissionManager.NOTIFICATION_PERMISSION
    return if (permission != null) {
        rememberPermissionState(permission = permission)
    } else {
        null
    }
}

/**
 * Request media images permission
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberMediaImagesPermissionState(): PermissionState {
    return rememberPermissionState(
        permission = PermissionManager.MEDIA_IMAGES_PERMISSION
    )
}

/**
 * Check if location permissions are granted
 */
@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.isLocationPermissionGranted(): Boolean {
    return permissions.any { it.status.isGranted }
}

/**
 * Check if all permissions in a group are granted
 */
@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.areAllPermissionsGranted(): Boolean {
    return permissions.all { it.status.isGranted }
}

/**
 * Get permission rationale text based on permission type
 */
fun getPermissionRationaleText(permission: String): String {
    return when (permission) {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION -> {
            "অবস্থানের অনুমতি প্রয়োজন আপনার কাছাকাছি রেস্তোরাঁ খুঁজে পেতে এবং ডেলিভারি ট্র্যাক করতে।"
        }
        Manifest.permission.CAMERA -> {
            "ক্যামেরার অনুমতি প্রয়োজন প্রোফাইল ছবি আপডেট করতে।"
        }
        Manifest.permission.POST_NOTIFICATIONS -> {
            "নোটিফিকেশনের অনুমতি প্রয়োজন অর্ডার আপডেট এবং অফার সম্পর্কে জানতে।"
        }
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_EXTERNAL_STORAGE -> {
            "গ্যালারি থেকে ছবি নির্বাচন করতে মিডিয়া অনুমতি প্রয়োজন।"
        }
        else -> "এই ফিচারটি ব্যবহার করতে অনুমতি প্রয়োজন।"
    }
}

/**
 * Get permission denied text based on permission type
 */
fun getPermissionDeniedText(permission: String): String {
    return when (permission) {
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION -> {
            "অবস্থানের অনুমতি প্রত্যাখ্যান করা হয়েছে। অনুগ্রহ করে সেটিংস থেকে অনুমতি দিন।"
        }
        Manifest.permission.CAMERA -> {
            "ক্যামেরার অনুমতি প্রত্যাখ্যান করা হয়েছে। অনুগ্রহ করে সেটিংস থেকে অনুমতি দিন।"
        }
        Manifest.permission.POST_NOTIFICATIONS -> {
            "নোটিফিকেশনের অনুমতি প্রত্যাখ্যান করা হয়েছে। অনুগ্রহ করে সেটিংস থেকে অনুমতি দিন।"
        }
        else -> "অনুমতি প্রত্যাখ্যান করা হয়েছে। সেটিংস থেকে অনুমতি দিন।"
    }
}

/**
 * Composable to handle permission request with UI
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permissionState: PermissionState,
    rationaleText: String,
    deniedText: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    when {
        permissionState.status.isGranted -> {
            onPermissionGranted()
        }
        permissionState.status.shouldShowRationale -> {
            // Show rationale and request again
            // This would typically show a dialog explaining why the permission is needed
            onPermissionDenied()
        }
        else -> {
            // Permission not granted and no rationale should be shown
            onPermissionDenied()
        }
    }
}

/**
 * Composable to handle multiple permissions request
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultiplePermissionsHandler(
    permissionsState: MultiplePermissionsState,
    rationaleText: String,
    deniedText: String,
    onAllPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit = {}
) {
    when {
        permissionsState.areAllPermissionsGranted() -> {
            onAllPermissionsGranted()
        }
        permissionsState.permissions.any { it.status.shouldShowRationale } -> {
            // Show rationale and request again
            onPermissionsDenied()
        }
        else -> {
            // Permissions not granted and no rationale should be shown
            onPermissionsDenied()
        }
    }
}
