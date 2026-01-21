package com.noor.khabarlagbe.presentation.components.map

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.noor.khabarlagbe.BuildConfig

/**
 * Delivery Tracking Map Component
 * Displays a Mapbox map with rider location and delivery destination
 * for real-time order tracking.
 */
@Composable
fun DeliveryTrackingMap(
    riderLatitude: Double,
    riderLongitude: Double,
    destinationLatitude: Double,
    destinationLongitude: Double,
    restaurantLatitude: Double? = null,
    restaurantLongitude: Double? = null,
    modifier: Modifier = Modifier
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var riderAnnotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    
    // Create custom marker bitmaps
    val riderMarkerBitmap = remember { createRiderMarkerBitmap() }
    val destinationMarkerBitmap = remember { createDestinationMarkerBitmap() }
    val restaurantMarkerBitmap = remember { createRestaurantMarkerBitmap() }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                factory = { context ->
                    MapView(context).apply {
                        mapView = this
                        
                        mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
                            // Set initial camera position to show rider
                            mapboxMap.setCamera(
                                CameraOptions.Builder()
                                    .center(Point.fromLngLat(riderLongitude, riderLatitude))
                                    .zoom(14.0)
                                    .build()
                            )
                            
                            // Create annotation manager for markers
                            val annotationApi = annotations
                            riderAnnotationManager = annotationApi.createPointAnnotationManager()
                            
                            // Add destination marker
                            riderAnnotationManager?.create(
                                PointAnnotationOptions()
                                    .withPoint(Point.fromLngLat(destinationLongitude, destinationLatitude))
                                    .withIconImage(destinationMarkerBitmap)
                            )
                            
                            // Add restaurant marker if available
                            if (restaurantLatitude != null && restaurantLongitude != null) {
                                riderAnnotationManager?.create(
                                    PointAnnotationOptions()
                                        .withPoint(Point.fromLngLat(restaurantLongitude, restaurantLatitude))
                                        .withIconImage(restaurantMarkerBitmap)
                                )
                            }
                            
                            // Add rider marker
                            riderAnnotationManager?.create(
                                PointAnnotationOptions()
                                    .withPoint(Point.fromLngLat(riderLongitude, riderLatitude))
                                    .withIconImage(riderMarkerBitmap)
                            )
                        }
                    }
                },
                update = { view ->
                    // Update rider position when coordinates change
                    riderAnnotationManager?.let { manager ->
                        manager.deleteAll()
                        
                        // Re-add destination marker
                        manager.create(
                            PointAnnotationOptions()
                                .withPoint(Point.fromLngLat(destinationLongitude, destinationLatitude))
                                .withIconImage(destinationMarkerBitmap)
                        )
                        
                        // Re-add restaurant marker if available
                        if (restaurantLatitude != null && restaurantLongitude != null) {
                            manager.create(
                                PointAnnotationOptions()
                                    .withPoint(Point.fromLngLat(restaurantLongitude, restaurantLatitude))
                                    .withIconImage(restaurantMarkerBitmap)
                            )
                        }
                        
                        // Re-add rider marker with new position
                        manager.create(
                            PointAnnotationOptions()
                                .withPoint(Point.fromLngLat(riderLongitude, riderLatitude))
                                .withIconImage(riderMarkerBitmap)
                        )
                        
                        // Animate camera to follow rider
                        view.mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(riderLongitude, riderLatitude))
                                .zoom(15.0)
                                .build()
                        )
                    }
                }
            )
            
            // Map Legend
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    LegendItem(color = Color(0xFF4CAF50), label = "Rider")
                    LegendItem(color = Color(0xFF2196F3), label = "Your Location")
                    if (restaurantLatitude != null) {
                        LegendItem(color = Color(0xFFFF5722), label = "Restaurant")
                    }
                }
            }
        }
    }
    
    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDestroy()
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            shape = RoundedCornerShape(6.dp),
            color = color
        ) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Create a custom rider marker bitmap
 */
private fun createRiderMarkerBitmap(): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    // Draw outer circle (green)
    val outerPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#4CAF50")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, outerPaint)
    
    // Draw inner circle (white)
    val innerPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 4f, innerPaint)
    
    return bitmap
}

/**
 * Create a custom destination marker bitmap
 */
private fun createDestinationMarkerBitmap(): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    // Draw outer circle (blue)
    val outerPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#2196F3")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, outerPaint)
    
    // Draw inner circle (white)
    val innerPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 4f, innerPaint)
    
    return bitmap
}

/**
 * Create a custom restaurant marker bitmap
 */
private fun createRestaurantMarkerBitmap(): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    // Draw outer circle (orange)
    val outerPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#FF5722")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, outerPaint)
    
    // Draw inner circle (white)
    val innerPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 4f, innerPaint)
    
    return bitmap
}
