# Mapbox ClassNotFoundException Fix Guide

## Problem
App crashes on startup with:
```
java.lang.ClassNotFoundException: com.mapbox.navigation.core.internal.MapboxNavigationSDKInitializer
NoClassDefFoundError: Failed resolution of: Lcom/mapbox/common/BaseMapboxInitializer
```

## Root Cause
The Mapbox Navigation SDK automatically initializes via `androidx.startup.InitializationProvider`, but the base classes from `mapbox-common` were not explicitly included or were being stripped by R8/ProGuard.

## Fixes Applied

### 1. Explicit Dependency
Added `com.mapbox.common:common:24.0.0` explicitly to ensure base classes are always present.

**Location:** `app/build.gradle.kts`
```kotlin
// Mapbox
implementation(libs.mapbox.maps.android)
implementation(libs.mapbox.maps.compose)
implementation(libs.mapbox.navigation.android)
implementation(libs.mapbox.common)  // ✅ Explicit dependency
```

**Location:** `gradle/libs.versions.toml`
```toml
mapbox-common = { group = "com.mapbox.common", name = "common", version.ref = "mapboxCommon" }
```

### 2. Enhanced ProGuard Rules
Added comprehensive keep rules for:
- `BaseMapboxInitializer` (the missing class)
- `MapboxNavigationSDKInitializer`
- All Mapbox initialization and startup classes
- Androidx Startup Initializer classes

**Location:** `app/proguard-rules.pro`
```proguard
# CRITICAL: Keep Mapbox Initialization classes (fixes ClassNotFoundException)
-keep class com.mapbox.common.** { *; }
-keep class com.mapbox.common.BaseMapboxInitializer { *; }
-keep class com.mapbox.navigation.core.internal.MapboxNavigationSDKInitializer { *; }
-keep class * extends com.mapbox.common.BaseMapboxInitializer { *; }

# Keep Mapbox ContentProvider and Startup Initializers
-keep class * extends androidx.startup.Initializer { *; }
-keep class androidx.startup.InitializationProvider { *; }
```

### 3. Version Compatibility
Using Mapbox versions:
- Maps: 11.0.0
- Navigation: 2.19.0 (stable)
- Common: 24.0.0

## Verification Steps

### 1. Clean Build
```bash
./gradlew clean
./gradlew :app:assembleDebug
```

### 2. Check Dependencies
```bash
./gradlew :app:dependencies --configuration debugRuntimeClasspath | grep mapbox
```

You should see:
```
+--- com.mapbox.maps:android:11.0.0
+--- com.mapbox.navigation:android:2.19.0
+--- com.mapbox.common:common:24.0.0
```

### 3. Test App Launch
Install and launch the app. Check logcat for:
```
✅ No ClassNotFoundException
✅ No NoClassDefFoundError
✅ Mapbox SDK initialized successfully
```

## Still Having Issues?

### Issue: "ClassNotFoundException" persists
**Solution:**
1. Delete build cache: `rm -rf app/build .gradle`
2. Sync project: `./gradlew --stop && ./gradlew clean`
3. Rebuild: `./gradlew :app:assembleDebug`

### Issue: "401 Unauthorized" during build
**Solution:**
- This is a different issue - your MAPBOX_DOWNLOADS_TOKEN is missing
- See [MAPBOX_AUTHENTICATION_GUIDE.md](./MAPBOX_AUTHENTICATION_GUIDE.md)

### Issue: Multiple dex files define Mapbox classes
**Solution:**
The dependency exclusions in `build.gradle.kts` already handle this:
```kotlin
configurations.all {
    exclude(group = "com.mapbox.common", module = "okhttp")
}
```

## Prevention

### Always Include mapbox-common Explicitly
When using Mapbox Navigation, always add:
```kotlin
implementation(libs.mapbox.common)
```

Don't rely on transitive dependencies alone.

### Keep ProGuard Rules Updated
When updating Mapbox SDKs, check their release notes for new ProGuard rules.

## Technical Details

### Auto-Initialization
Mapbox Navigation SDK v3+ uses AndroidX Startup library for automatic initialization:

1. SDK declares `InitializationProvider` in its manifest
2. Provider launches `MapboxNavigationSDKInitializer`
3. Initializer extends `BaseMapboxInitializer` from mapbox-common
4. If any class in this chain is missing → crash

### Why Explicit Dependency Matters
- Gradle may resolve mapbox-common transitively at compile time
- R8/ProGuard may strip "unused" classes at build time
- Explicit dependency signals to Gradle and R8: "This is required"

## References
- [Mapbox Installation Docs](https://docs.mapbox.com/android/navigation/guides/install/)
- [AndroidX Startup](https://developer.android.com/topic/libraries/app-startup)
- [R8 Keep Rules](https://developer.android.com/studio/build/shrink-code#keep-code)
