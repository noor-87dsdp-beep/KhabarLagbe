# KhabarLagbe CI/CD Setup & Configuration

## Build Status

All CI/CD pipelines should now pass successfully:
- ✅ Backend (Node.js)
- ✅ Admin Panel (React + TypeScript)
- ✅ Android User App (Kotlin + Jetpack Compose)
- ✅ Android Rider App (Kotlin + Jetpack Compose)
- ✅ Android Restaurant App (Kotlin + Jetpack Compose)

## Recent CI Fixes

### 1. Node.js Projects (Backend & Admin Panel)
**Issue**: Missing `package-lock.json` files causing `npm ci` to fail.

**Fix**: Generated `package-lock.json` files by running `npm install` in both directories.

**Files Changed**:
- `/backend/package-lock.json` (generated)
- `/admin-panel/package-lock.json` (generated)
- `/admin-panel/postcss.config.js` → renamed to `postcss.config.cjs` (fixed ES module issue)
- `/admin-panel/src/vite-env.d.ts` (created for TypeScript definitions)
- `/admin-panel/src/pages/restaurants/RestaurantManagement.tsx` (removed unused import)

### 2. Android Projects (All Apps)
**Issue**: Mapbox SDK dependencies required authentication token that wasn't available in CI.

**Fix**: Temporarily commented out Mapbox dependencies with clear instructions for enabling them later.

**Files Changed**:
- `/gradle/libs.versions.toml` (updated Mapbox version reference)
- `/settings.gradle.kts` (added commented Mapbox repository configuration)
- `/gradle.properties` (added Mapbox setup instructions)
- `/app/build.gradle.kts` (commented out Mapbox dependencies)
- `/rider-app/build.gradle.kts` (commented out Mapbox dependencies)
- `/restaurant-app/build.gradle.kts` (commented out Mapbox dependencies)

## Enabling Mapbox SDK

Mapbox SDK is required for map features like:
- Address selection and location picking
- Restaurant map view
- Live order tracking with rider location
- Turn-by-turn navigation for riders
- Delivery zone visualization

### Steps to Enable Mapbox:

1. **Get a Mapbox Access Token**:
   - Go to https://account.mapbox.com/
   - Sign up or log in
   - Navigate to "Access tokens"
   - Create a new token with these scopes:
     - Downloads: Read
   - Copy the token (starts with `sk.`)

2. **Configure the Token**:
   
   **For Local Development**:
   Add to `/gradle.properties`:
   ```properties
   MAPBOX_DOWNLOADS_TOKEN=your_secret_token_here
   ```
   
   **For CI/CD**:
   Add as a GitHub Secret:
   - Go to repository Settings → Secrets and variables → Actions
   - Add new secret: `MAPBOX_DOWNLOADS_TOKEN`
   - Set value to your token

3. **Uncomment Mapbox Repository** in `/settings.gradle.kts`:
   ```kotlin
   maven {
       url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
       credentials {
           username = "mapbox"
           password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN")
               .orElse(providers.environmentVariable("MAPBOX_DOWNLOADS_TOKEN"))
               .orElse("")
               .get()
       }
       authentication {
           create<BasicAuthentication>("basic")
       }
   }
   ```

4. **Uncomment Mapbox Dependencies** in:
   - `/app/build.gradle.kts`
   - `/rider-app/build.gradle.kts`
   - `/restaurant-app/build.gradle.kts`
   
   Change:
   ```kotlin
   // implementation(libs.mapbox.maps.android)
   // implementation(libs.mapbox.maps.compose)
   ```
   To:
   ```kotlin
   implementation(libs.mapbox.maps.android)
   implementation(libs.mapbox.maps.compose)
   ```

5. **Update CI Workflow** (if needed):
   In `.github/workflows/deploy.yml`, ensure the token is passed:
   ```yaml
   - name: Build User App
     run: ./gradlew :app:assembleDebug
     env:
       MAPBOX_DOWNLOADS_TOKEN: ${{ secrets.MAPBOX_DOWNLOADS_TOKEN }}
   ```

6. **Add Public Mapbox Token** for Runtime:
   - Create a public token at https://account.mapbox.com/access-tokens/
   - Add to `local.properties` (for local dev):
     ```
     MAPBOX_ACCESS_TOKEN=pk.your_public_token_here
     ```
   - Update build.gradle.kts to inject as BuildConfig
   - Add to CI secrets as `MAPBOX_ACCESS_TOKEN`

## Development Setup

### Backend
```bash
cd backend
npm install
npm run dev
```

### Admin Panel
```bash
cd admin-panel
npm install
npm run dev
```

### Android Apps
```bash
# Build all apps
./gradlew build

# Build specific app
./gradlew :app:assembleDebug           # User app
./gradlew :rider-app:assembleDebug      # Rider app
./gradlew :restaurant-app:assembleDebug # Restaurant app
```

## Production Readiness

Before deploying to production, ensure:
- [ ] Mapbox token is configured (see above)
- [ ] Firebase `google-services.json` files are added to each app module
- [ ] Backend `.env` file is configured with production values
- [ ] Admin panel `.env` file points to production API
- [ ] All secrets are stored securely in CI/CD environment
- [ ] SSL certificates are configured for backend API
- [ ] Push notification certificates/keys are configured

## Support

For issues or questions:
- Check GitHub Issues
- Review individual README files in each module
- Consult the API documentation in `/backend/README_API.md`
