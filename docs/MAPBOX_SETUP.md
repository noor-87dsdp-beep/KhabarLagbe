# Mapbox Setup Guide

This guide explains how to set up Mapbox for all KhabarLagbe applications.

## Prerequisites

- A Mapbox account (sign up at https://www.mapbox.com/)
- Access to Mapbox Access Tokens page (https://account.mapbox.com/access-tokens/)

## 1. Get Mapbox Tokens

### Public Access Token
1. Go to https://account.mapbox.com/access-tokens/
2. Copy your "Default public token" (starts with `pk.`)
3. This token is used in client applications (Android apps, Admin panel)

### Secret Access Token
1. In the same page, click "Create a token"
2. Give it a name like "KhabarLagbe Downloads"
3. Select the following scopes:
   - `Downloads:Read` (required for Maven repository access)
4. Copy the secret token (starts with `sk.`)
5. ⚠️ **Important**: Save this token securely - it won't be shown again!

## 2. Configure Android Apps

### Option A: Using local.properties (Recommended for Development)

1. Copy `local.properties.example` to `local.properties`:
   ```bash
   cp local.properties.example local.properties
   ```

2. Edit `local.properties` and add your tokens:
   ```properties
   MAPBOX_ACCESS_TOKEN=pk.your_actual_public_token_here
   MAPBOX_DOWNLOADS_TOKEN=sk.your_actual_secret_token_here
   ```

3. The tokens will be automatically injected during build

### Option B: Using Environment Variables (CI/CD)

Set these environment variables:
```bash
export MAPBOX_ACCESS_TOKEN="pk.your_token"
export MAPBOX_DOWNLOADS_TOKEN="sk.your_token"
```

## 3. Configure Backend

1. Copy `.env.example` to `.env` in the `backend` directory:
   ```bash
   cd backend
   cp .env.example .env
   ```

2. Edit `.env` and add your Mapbox tokens:
   ```env
   MAPBOX_ACCESS_TOKEN=pk.your_actual_public_token_here
   MAPBOX_SECRET_TOKEN=sk.your_actual_secret_token_here
   ```

## 4. Configure Admin Panel

1. Copy `.env.example` to `.env` in the `admin-panel` directory:
   ```bash
   cd admin-panel
   cp .env.example .env
   ```

2. Edit `.env` and add your Mapbox public token:
   ```env
   VITE_MAPBOX_ACCESS_TOKEN=pk.your_actual_public_token_here
   ```

## 5. Verify Setup

### Backend
```bash
cd backend
npm install
npm run dev
```

The backend should start without errors. Check the console for any Mapbox-related warnings.

### Admin Panel
```bash
cd admin-panel
npm install
npm run dev
```

The admin panel should build successfully. Maps should load without errors when you navigate to zone management.

### Android Apps
```bash
./gradlew :app:assembleDebug
./gradlew :rider-app:assembleDebug
./gradlew :restaurant-app:assembleDebug
```

All apps should compile successfully. If you get Mapbox dependency errors, ensure:
1. `MAPBOX_DOWNLOADS_TOKEN` is correctly set
2. Your internet connection is working
3. Try running `./gradlew clean` and rebuild

## Features Enabled by Mapbox

### User App
- ✅ Address selection with map pin drop
- ✅ Restaurant map view showing nearby restaurants
- ✅ Live order tracking with rider location
- ✅ Address autocomplete (Bangladesh)
- ✅ Route visualization

### Rider App
- ✅ Turn-by-turn navigation
- ✅ Live location broadcasting
- ✅ Route optimization
- ✅ Distance calculation for earnings

### Restaurant App
- ✅ Restaurant location picker
- ✅ Delivery zone visualization
- ✅ View incoming order locations
- ✅ View assigned rider location

### Admin Panel
- ✅ Zone management with polygon drawing
- ✅ Live operations map
- ✅ All active riders (real-time)
- ✅ All active orders (real-time)
- ✅ Restaurant locations
- ✅ Heat map of orders

## Troubleshooting

### "401 Unauthorized" errors
- Check that your `MAPBOX_DOWNLOADS_TOKEN` is valid and has `Downloads:Read` scope
- Verify the token hasn't expired

### Maps not loading in apps
- Verify `MAPBOX_ACCESS_TOKEN` is correctly set
- Check internet connectivity
- Ensure the token has appropriate scopes

### Build failures
- Run `./gradlew clean`
- Delete `.gradle` cache: `rm -rf ~/.gradle/caches/`
- Verify tokens are in `local.properties` (not `.gitignore`d)

## Security Best Practices

1. **Never commit tokens to Git**
   - `local.properties` and `.env` are in `.gitignore`
   - Use environment variables in CI/CD

2. **Use token restrictions**
   - In Mapbox dashboard, add URL restrictions for public tokens
   - Restrict secret tokens to specific IPs if possible

3. **Rotate tokens regularly**
   - Change tokens every 90 days
   - Immediately rotate if exposed

4. **Monitor usage**
   - Check Mapbox dashboard for unusual activity
   - Set up usage alerts

## Additional Resources

- [Mapbox Android SDK Documentation](https://docs.mapbox.com/android/maps/guides/)
- [Mapbox GL JS Documentation](https://docs.mapbox.com/mapbox-gl-js/guides/)
- [Mapbox Access Tokens](https://docs.mapbox.com/help/getting-started/access-tokens/)
- [Mapbox Pricing](https://www.mapbox.com/pricing/)

## Free Tier Limits

Mapbox offers a generous free tier:
- 50,000 free map loads per month
- 100,000 free Geocoding requests per month
- 25,000 free Directions requests per month

These limits should be sufficient for development and testing. Monitor usage in production.
