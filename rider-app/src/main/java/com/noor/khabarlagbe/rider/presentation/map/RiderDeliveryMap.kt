package com.noor.khabarlagbe.rider.presentation.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
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
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.noor.khabarlagbe.rider.BuildConfig

/**
 * Rider Delivery Map Component
 * Full-screen map for riders showing:
 * - Current location (GPS)
 * - Restaurant pickup location
 * - Customer delivery destination
 * - Navigation route
 */
@Composable
fun RiderDeliveryMap(
    currentLatitude: Double,
    currentLongitude: Double,
    restaurantLatitude: Double,
    restaurantLongitude: Double,
    customerLatitude: Double,
    customerLongitude: Double,
    showRoute: Boolean = true,
    isPickedUp: Boolean = false,
    modifier: Modifier = Modifier,
    onMapReady: () -> Unit = {}
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var pointAnnotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var polylineAnnotationManager by remember { mutableStateOf<PolylineAnnotationManager?>(null) }
    
    // Create custom marker bitmaps
    val riderMarkerBitmap = remember { createRiderMarkerBitmap() }
    val restaurantMarkerBitmap = remember { createRestaurantMarkerBitmap() }
    val customerMarkerBitmap = remember { createCustomerMarkerBitmap() }
    
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    mapView = this
                    
                    mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
                        // Enable location component to show user's current location
                        location.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                        }
                        
                        // Set initial camera position
                        mapboxMap.setCamera(
                            CameraOptions.Builder()
                                .center(Point.fromLngLat(currentLongitude, currentLatitude))
                                .zoom(14.0)
                                .build()
                        )
                        
                        // Create annotation managers
                        val annotationApi = annotations
                        pointAnnotationManager = annotationApi.createPointAnnotationManager()
                        polylineAnnotationManager = annotationApi.createPolylineAnnotationManager()
                        
                        // Add markers
                        addMarkers(
                            pointAnnotationManager!!,
                            currentLatitude, currentLongitude,
                            restaurantLatitude, restaurantLongitude,
                            customerLatitude, customerLongitude,
                            riderMarkerBitmap, restaurantMarkerBitmap, customerMarkerBitmap,
                            isPickedUp
                        )
                        
                        // Add route line if enabled
                        if (showRoute) {
                            addRouteLine(
                                polylineAnnotationManager!!,
                                currentLatitude, currentLongitude,
                                restaurantLatitude, restaurantLongitude,
                                customerLatitude, customerLongitude,
                                isPickedUp
                            )
                        }
                        
                        onMapReady()
                    }
                }
            },
            update = { view ->
                // Update markers when position changes
                pointAnnotationManager?.let { manager ->
                    manager.deleteAll()
                    addMarkers(
                        manager,
                        currentLatitude, currentLongitude,
                        restaurantLatitude, restaurantLongitude,
                        customerLatitude, customerLongitude,
                        riderMarkerBitmap, restaurantMarkerBitmap, customerMarkerBitmap,
                        isPickedUp
                    )
                }
                
                // Update route line
                if (showRoute) {
                    polylineAnnotationManager?.let { manager ->
                        manager.deleteAll()
                        addRouteLine(
                            manager,
                            currentLatitude, currentLongitude,
                            restaurantLatitude, restaurantLongitude,
                            customerLatitude, customerLongitude,
                            isPickedUp
                        )
                    }
                }
                
                // Update camera to follow rider
                view.mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(currentLongitude, currentLatitude))
                        .build()
                )
            }
        )
        
        // Map Legend
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Navigation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LegendItem(color = Color(0xFF4CAF50), label = "Your Location")
                if (!isPickedUp) {
                    LegendItem(color = Color(0xFFFF5722), label = "Restaurant")
                }
                LegendItem(color = Color(0xFF2196F3), label = "Customer")
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
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun addMarkers(
    manager: PointAnnotationManager,
    currentLat: Double, currentLng: Double,
    restaurantLat: Double, restaurantLng: Double,
    customerLat: Double, customerLng: Double,
    riderBitmap: Bitmap, restaurantBitmap: Bitmap, customerBitmap: Bitmap,
    isPickedUp: Boolean
) {
    // Add rider marker
    manager.create(
        PointAnnotationOptions()
            .withPoint(Point.fromLngLat(currentLng, currentLat))
            .withIconImage(riderBitmap)
    )
    
    // Add restaurant marker (only if not picked up)
    if (!isPickedUp) {
        manager.create(
            PointAnnotationOptions()
                .withPoint(Point.fromLngLat(restaurantLng, restaurantLat))
                .withIconImage(restaurantBitmap)
        )
    }
    
    // Add customer marker
    manager.create(
        PointAnnotationOptions()
            .withPoint(Point.fromLngLat(customerLng, customerLat))
            .withIconImage(customerBitmap)
    )
}

private fun addRouteLine(
    manager: PolylineAnnotationManager,
    currentLat: Double, currentLng: Double,
    restaurantLat: Double, restaurantLng: Double,
    customerLat: Double, customerLng: Double,
    isPickedUp: Boolean
) {
    val routePoints = if (isPickedUp) {
        // Direct route to customer after pickup
        listOf(
            Point.fromLngLat(currentLng, currentLat),
            Point.fromLngLat(customerLng, customerLat)
        )
    } else {
        // Route: Current -> Restaurant -> Customer
        listOf(
            Point.fromLngLat(currentLng, currentLat),
            Point.fromLngLat(restaurantLng, restaurantLat),
            Point.fromLngLat(customerLng, customerLat)
        )
    }
    
    manager.create(
        PolylineAnnotationOptions()
            .withPoints(routePoints)
            .withLineColor("#4CAF50")
            .withLineWidth(4.0)
    )
}

/**
 * Create a custom rider marker bitmap (green circle)
 */
private fun createRiderMarkerBitmap(): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    val outerPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#4CAF50")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, outerPaint)
    
    val innerPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 4f, innerPaint)
    
    return bitmap
}

/**
 * Create a custom restaurant marker bitmap (orange circle)
 */
private fun createRestaurantMarkerBitmap(): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    val outerPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#FF5722")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, outerPaint)
    
    val innerPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 4f, innerPaint)
    
    return bitmap
}

/**
 * Create a custom customer marker bitmap (blue circle)
 */
private fun createCustomerMarkerBitmap(): Bitmap {
    val size = 60
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    val outerPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#2196F3")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4, outerPaint)
    
    val innerPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 4f, innerPaint)
    
    return bitmap
}
