package com.noor.khabarlagbe.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * ImageUploadHelper - Utility for handling image selection and upload
 * Supports gallery selection, camera capture, compression, and conversion
 */
object ImageUploadHelper {
    
    private const val TAG = "ImageUploadHelper"
    private const val MAX_IMAGE_SIZE = 1024 // Max width/height in pixels
    private const val COMPRESSION_QUALITY = 80 // JPEG compression quality (0-100)
    
    /**
     * Compress bitmap to reduce file size
     */
    fun compressBitmap(bitmap: Bitmap, maxSize: Int = MAX_IMAGE_SIZE): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }
        
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        
        if (width > height) {
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            newHeight = maxSize
            newWidth = (maxSize * ratio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    /**
     * Convert bitmap to Base64 string
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = COMPRESSION_QUALITY): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    
    /**
     * Convert Uri to Bitmap
     */
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to Bitmap", e)
            null
        }
    }
    
    /**
     * Convert Uri to File
     */
    fun uriToFile(context: Context, uri: Uri, fileName: String = "temp_image.jpg"): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            file
        } catch (e: Exception) {
            Log.e(TAG, "Error converting URI to File", e)
            null
        }
    }
    
    /**
     * Process image for upload (compress and convert to Base64)
     */
    fun processImageForUpload(
        context: Context,
        uri: Uri,
        maxSize: Int = MAX_IMAGE_SIZE,
        quality: Int = COMPRESSION_QUALITY
    ): ImageUploadResult {
        return try {
            val bitmap = uriToBitmap(context, uri)
                ?: return ImageUploadResult.Error("Failed to load image")
            
            val compressedBitmap = compressBitmap(bitmap, maxSize)
            val base64 = bitmapToBase64(compressedBitmap, quality)
            val file = uriToFile(context, uri)
            
            ImageUploadResult.Success(
                base64 = base64,
                file = file,
                bitmap = compressedBitmap
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image", e)
            ImageUploadResult.Error(e.message ?: "Unknown error occurred")
        }
    }
    
    /**
     * Get file size in KB
     */
    fun getFileSizeInKB(file: File): Long {
        return file.length() / 1024
    }
    
    /**
     * Get Base64 size in KB
     */
    fun getBase64SizeInKB(base64: String): Long {
        return (base64.length * 3L / 4L) / 1024
    }
}

/**
 * Result of image upload processing
 */
sealed class ImageUploadResult {
    data class Success(
        val base64: String,
        val file: File?,
        val bitmap: Bitmap
    ) : ImageUploadResult()
    
    data class Error(val message: String) : ImageUploadResult()
}

/**
 * Composable launcher for selecting image from gallery
 */
@Composable
fun rememberGalleryLauncher(
    onImageSelected: (Uri) -> Unit
): ManagedActivityResultLauncher<String, Uri?> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }
}

/**
 * Composable launcher for capturing image with camera
 */
@Composable
fun rememberCameraLauncher(
    context: Context,
    onImageCaptured: (Uri) -> Unit
): Pair<ManagedActivityResultLauncher<Uri, Boolean>, Uri> {
    val imageUri = remember {
        val file = File(context.cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
        Uri.fromFile(file)
    }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onImageCaptured(imageUri)
        }
    }
    
    return Pair(launcher, imageUri)
}

/**
 * Image picker options
 */
enum class ImagePickerOption {
    GALLERY,
    CAMERA
}

/**
 * State holder for image picker
 */
data class ImagePickerState(
    val selectedUri: Uri? = null,
    val processedResult: ImageUploadResult? = null,
    val isProcessing: Boolean = false,
    val error: String? = null
)

/**
 * Helper function to handle image selection from multiple sources
 */
fun handleImageSelection(
    context: Context,
    uri: Uri,
    onSuccess: (ImageUploadResult.Success) -> Unit,
    onError: (String) -> Unit
) {
    when (val result = ImageUploadHelper.processImageForUpload(context, uri)) {
        is ImageUploadResult.Success -> {
            Log.d("ImageUploadHelper", "Image processed successfully: ${result.file?.length()?.div(1024)} KB")
            onSuccess(result)
        }
        is ImageUploadResult.Error -> {
            Log.e("ImageUploadHelper", "Error processing image: ${result.message}")
            onError(result.message)
        }
    }
}
