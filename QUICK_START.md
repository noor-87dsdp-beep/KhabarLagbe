# KhabarLagbe - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### 1. Prerequisites
- Android Studio Koala or newer
- JDK 17
- Git

### 2. Clone and Open
```bash
git clone https://github.com/noor-87dsdp-beep/KhabarLagbe.git
cd KhabarLagbe
```

Open the project in Android Studio (File → Open → Select KhabarLagbe folder)

### 3. Build and Run
Wait for Gradle sync to complete, then:
- Click the "Run" button (green triangle) in Android Studio, OR
- Use command line:
  ```bash
  ./gradlew assembleDebug
  ./gradlew installDebug
  ```

### 4. What You'll See
The app launches with:
- **Home Screen**: Displays sample restaurants with images, ratings, delivery info
- **Navigation**: Tap restaurants to see details, use cart icon to view cart
- **Functional Screens**: Login, Register, Cart, Checkout, Order Tracking, Profile

## 🎯 Key Features Available Now

✅ **Full Navigation Flow**
- All main screens are accessible
- Material 3 design throughout
- Smooth transitions between screens

✅ **UI Components**
- Restaurant cards with images and info
- Cart with item management
- Checkout with payment options
- Order tracking with timeline
- User profile management

✅ **Sample Data**
- Pre-populated restaurants
- Sample menu items
- Mock cart items
- All ready for testing

## 🔧 Optional Setup (For Advanced Features)

### Enable Firebase (Optional)
Currently commented out. To enable:
1. Get `google-services.json` from Firebase Console
2. Place in `app/` directory
3. Uncomment Firebase deps in `app/build.gradle.kts`

### Enable Mapbox (Optional)
Currently commented out. To enable:
1. Get tokens from mapbox.com
2. Add to `local.properties`:
   ```properties
   MAPBOX_ACCESS_TOKEN=your_token
   MAPBOX_DOWNLOADS_TOKEN=your_downloads_token
   ```
3. Uncomment Mapbox deps in `app/build.gradle.kts`

## 📱 Testing the App

### Main User Flow
1. **Launch app** → Lands on Home screen
2. **Browse restaurants** → Scroll through featured restaurants
3. **Tap a restaurant** → View menu with categories
4. **Add items** → Tap "ADD" buttons (currently shows sample behavior)
5. **View cart** → Tap cart icon in top bar
6. **Checkout** → Tap "Proceed to Checkout"
7. **Track order** → After placing order, see timeline

### Navigation Test
- Tap Profile icon → See user profile
- Tap Login button → See login form
- Register link → See registration form
- All screens are reachable and functional

## 🏗️ Project Structure

```
KhabarLagbe/
├── app/                    # Main Android user app ✅ COMPLETE
│   ├── src/main/
│   │   ├── java/.../
│   │   │   ├── presentation/  # UI screens
│   │   │   ├── domain/        # Models
│   │   │   ├── di/            # Dependency injection
│   │   │   └── navigation/    # Navigation
│   │   └── res/               # Resources
│   └── build.gradle.kts
├── backend/                # API server 🚧 STRUCTURE READY
│   ├── src/index.js       # Express server stub
│   └── package.json
├── rider-app/             # Delivery app 🚧 PLANNED
├── restaurant-app/        # Restaurant dashboard 🚧 PLANNED
└── admin-panel/           # Admin dashboard 🚧 PLANNED
```

## 🐛 Troubleshooting

### Build Fails
```bash
./gradlew clean
./gradlew build
```

### Gradle Sync Issues
1. File → Invalidate Caches → Restart
2. Delete `.gradle` folder
3. Sync again

### APK Not Installing
```bash
adb uninstall com.noor.khabarlagbe
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📚 Learn More

- Full setup: `SETUP.md`
- Build details: `BUILD_STATUS.md`
- Architecture: `ARCHITECTURE.md`
- API docs: `API.md`

## 🎉 You're Ready!

The app is fully buildable and runnable. All screens are implemented with Material 3 UI. Start exploring the code and building new features!

**Happy Coding! 🚀**
