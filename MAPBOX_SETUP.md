# Mapbox Integration Setup Guide

## Overview
The KhabarLagbe User App now includes Mapbox integration for location services, maps, and navigation features.

## Configuration Details

### Dependencies (Already Added)
- **Mapbox Maps SDK**: v11.0.0
- **Mapbox Compose Extension**: v11.0.0  
- **Mapbox Navigation SDK**: v2.17.0

### Files Modified
1. `gradle/libs.versions.toml` - Updated Mapbox version to 11.0.0 and added Navigation SDK
2. `settings.gradle.kts` - Enabled Mapbox Maven repository
3. `app/build.gradle.kts` - Enabled Mapbox dependencies and added BuildConfig fields
4. `app/src/main/AndroidManifest.xml` - Added Mapbox token meta-data
5. `gradle.properties` - Updated documentation

### Required Permissions (Already Declared)
The following permissions are already declared in `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## Setup Instructions

### 1. Get Mapbox Tokens
1. Sign up or log in at [Mapbox](https://account.mapbox.com/)
2. Navigate to [Access Tokens](https://account.mapbox.com/access-tokens/)
3. You need TWO tokens:
   - **Public Token** (starts with `pk.`) - For runtime SDK usage
   - **Secret/Downloads Token** (starts with `sk.`) - For Maven repository access
   - **IMPORTANT**: Your secret token MUST have the **DOWNLOADS:READ** scope enabled

### 2. Configure Tokens Locally

Create or edit `local.properties` file in the project root:

```properties
# Mapbox Configuration
MAPBOX_ACCESS_TOKEN=pk.YOUR_PUBLIC_TOKEN_HERE
MAPBOX_DOWNLOADS_TOKEN=sk.YOUR_SECRET_TOKEN_HERE
```

**Note**: The `local.properties` file is gitignored and will NOT be committed to the repository.

### 3. Build the Project

```bash
./gradlew clean
./gradlew :app:assembleDebug
```

## Accessing Mapbox Token in Code

The public token is available via BuildConfig:

```kotlin
import com.noor.khabarlagbe.BuildConfig

val mapboxToken = BuildConfig.MAPBOX_PUBLIC_TOKEN
```

The token is also injected into the AndroidManifest as meta-data for Mapbox SDK initialization.

## Troubleshooting

### Build fails with 401 Unauthorized
- Check that `MAPBOX_DOWNLOADS_TOKEN` is correctly set in `local.properties`
- Ensure the token starts with `sk.` and is a valid secret token
- Verify you have download permissions for the token

### Map not displaying
- Check that `MAPBOX_ACCESS_TOKEN` is correctly set
- Ensure the token starts with `pk.` and is a valid public token
- Verify location permissions are granted at runtime

### Gradle sync issues
- Run `./gradlew --stop` to stop all Gradle daemons
- Delete `.gradle` folder and rebuild
- Check that both tokens are set in `local.properties`

## Common Issues and Solutions

### 401 Unauthorized Error
If you see this error:
```
Could not GET 'https://api.mapbox.com/downloads/v2/releases/maven/...'
Received status code 401 from server: Unauthorized
```

**Solution:**
1. Verify your secret token has **DOWNLOADS:READ** scope:
   - Go to https://account.mapbox.com/access-tokens/
   - Click on your secret token
   - Ensure "DOWNLOADS:READ" is checked
2. If not, create a new secret token with this scope enabled
3. Update `MAPBOX_DOWNLOADS_TOKEN` in `local.properties`
4. Run: `./gradlew --stop && ./gradlew clean`
5. Rebuild the project

### Token Not Being Read
If Gradle can't find your token:
1. Ensure `local.properties` is in the project root (same level as `settings.gradle.kts`)
2. Check there are no spaces around the `=` sign
3. Check there are no quotes around token values
4. Verify file format is UTF-8 without BOM

## Next Steps

With Mapbox integrated, you can now:
- Display interactive maps in the app
- Show restaurant locations
- Track delivery rider locations in real-time
- Implement turn-by-turn navigation
- Add custom map markers and styles

## Resources

- [Mapbox Maps SDK for Android](https://docs.mapbox.com/android/maps/guides/)
- [Mapbox Navigation SDK for Android](https://docs.mapbox.com/android/navigation/guides/)
- [Mapbox Compose Extension](https://docs.mapbox.com/android/maps/guides/compose/)
