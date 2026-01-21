# Mapbox Integration Setup Guide

## Overview
The KhabarLagbe apps include Mapbox integration for location services, maps, and navigation features for food delivery tracking.

## Current Implementation Status

### 🎯 Included Features (Work Without Mapbox Token)
The following features are **fully functional** using a placeholder implementation:
- **User App - Order Tracking Map**: Shows rider location, restaurant, and destination with distance/ETA calculations
- **Rider App - Delivery Map**: Full navigation visualization with pickup/delivery locations
- **Rider App - Delivery Screen**: Complete delivery workflow UI with status updates
- **Location Service**: GPS tracking service for riders (requires Play Services)

### 🗺️ Full Mapbox Features (Requires Token)
With a valid Mapbox token, you can enable:
- Interactive street-level maps
- Real-time map tile rendering
- Turn-by-turn navigation
- Custom map styling
- Advanced routing and traffic data

## Configuration Details

### Dependencies (Configured in Version Catalog)
- **Mapbox Maps SDK**: v11.0.0
- **Mapbox Compose Extension**: v11.0.0  
- **Mapbox Navigation SDK**: v2.19.0
- **Mapbox Common**: v24.0.0

### Files Modified for Map Integration
1. `gradle/libs.versions.toml` - Mapbox version definitions
2. `settings.gradle.kts` - Enabled Mapbox Maven repository with authentication
3. `app/build.gradle.kts` - BuildConfig fields and token management (dependencies commented)
4. `rider-app/build.gradle.kts` - Same configuration for rider app (dependencies commented)
5. `app/src/main/AndroidManifest.xml` - Mapbox token meta-data
6. `rider-app/src/main/AndroidManifest.xml` - Mapbox token meta-data

### Map Components Created
- `app/.../presentation/components/map/DeliveryTrackingMap.kt` - User app tracking component
- `rider-app/.../presentation/map/RiderDeliveryMap.kt` - Rider delivery map component  
- `rider-app/.../presentation/delivery/DeliveryScreen.kt` - Complete delivery workflow screen
- `rider-app/.../service/LocationService.kt` - GPS location tracking service

### Required Permissions (Already Declared)
```xml
<!-- User App -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Rider App (additional) -->
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
```

## Enabling Full Mapbox Maps

### 1. Get Mapbox Tokens
1. Sign up or log in at [Mapbox](https://account.mapbox.com/)
2. Navigate to [Access Tokens](https://account.mapbox.com/access-tokens/)
3. You need TWO tokens:
   - **Public Token** (starts with `pk.`) - For runtime SDK usage
   - **Secret/Downloads Token** (starts with `sk.`) - For Maven repository access
   - **IMPORTANT**: Your secret token MUST have the **DOWNLOADS:READ** scope enabled

### 2. Configure Tokens

**Option A: Global Configuration (Recommended)**
Add to `~/.gradle/gradle.properties`:
```properties
MAPBOX_ACCESS_TOKEN=pk.YOUR_PUBLIC_TOKEN_HERE
MAPBOX_DOWNLOADS_TOKEN=sk.YOUR_SECRET_TOKEN_HERE
```

**Option B: Local Configuration**
Create/edit `local.properties` in project root:
```properties
MAPBOX_ACCESS_TOKEN=pk.YOUR_PUBLIC_TOKEN_HERE
MAPBOX_DOWNLOADS_TOKEN=sk.YOUR_SECRET_TOKEN_HERE
```

### 3. Uncomment Dependencies

In `app/build.gradle.kts`:
```kotlin
// Change from:
// implementation(libs.mapbox.maps.android)
// implementation(libs.mapbox.maps.compose)
// implementation(libs.mapbox.navigation.android)
// implementation(libs.mapbox.common)

// To:
implementation(libs.mapbox.maps.android)
implementation(libs.mapbox.maps.compose)
implementation(libs.mapbox.navigation.android)
implementation(libs.mapbox.common)
```

Do the same in `rider-app/build.gradle.kts`.

### 4. Build the Project
```bash
./gradlew clean
./gradlew :app:assembleDebug :rider-app:assembleDebug
```

## Using Maps in Code

### Accessing Token
```kotlin
import com.noor.khabarlagbe.BuildConfig

val mapboxToken = BuildConfig.MAPBOX_PUBLIC_TOKEN
```

### Example: MapView Integration
```kotlin
AndroidView(
    factory = { context ->
        MapView(context).apply {
            mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
                mapboxMap.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(longitude, latitude))
                        .zoom(14.0)
                        .build()
                )
            }
        }
    }
)
```

## Troubleshooting

### Build fails with 401 Unauthorized
- Check that `MAPBOX_DOWNLOADS_TOKEN` starts with `sk.`
- Verify the token has **DOWNLOADS:READ** scope enabled
- Run `./gradlew --stop` then rebuild

### Map not displaying
- Verify `MAPBOX_ACCESS_TOKEN` starts with `pk.`
- Ensure location permissions are granted at runtime
- Check Logcat for Mapbox initialization errors

### Duplicate class conflicts
If you encounter duplicate class errors:
```
Duplicate class com.mapbox.common.module.okhttp.* found
```
The conflict resolution is already configured in `build.gradle.kts` files via:
```kotlin
configurations.all {
    exclude(group = "com.mapbox.common", module = "okhttp")
    resolutionStrategy {
        force("com.mapbox.common:common:24.0.0")
    }
}
```

## Resources

- [Mapbox Maps SDK for Android](https://docs.mapbox.com/android/maps/guides/)
- [Mapbox Navigation SDK for Android](https://docs.mapbox.com/android/navigation/guides/)
- [Mapbox Compose Extension](https://docs.mapbox.com/android/maps/guides/compose/)
- [Mapbox Access Tokens](https://account.mapbox.com/access-tokens/)
