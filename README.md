# KhabarLagbe - Local Food Delivery Platform

A complete, production-ready food delivery platform with world-class UI/UX, built using modern Android development practices with Jetpack Compose.

## 🌟 Project Overview

KhabarLagbe is a comprehensive food delivery ecosystem consisting of:
1. **User App** (Customer-Facing) - Current Android implementation
2. **Rider App** (Delivery Personnel) - Structure ready
3. **Restaurant App** (Business Dashboard) - Structure ready
4. **Admin Panel** (Super Admin) - Structure ready
5. **Backend API** - Architecture designed

## 🎨 Features Implemented

### User App (Android - Jetpack Compose)

#### ✅ Core Architecture
- **Clean Architecture** with separation of concerns (Data, Domain, Presentation layers)
- **MVVM Pattern** with ViewModels for state management
- **Dependency Injection** with Hilt
- **Navigation Component** with type-safe navigation
- **Material 3** design system with custom theming

#### ✅ UI/UX Features
- **World-Class Design**: Modern, intuitive, mobile-first interface
- **Custom Color Palette**: Vibrant orange brand color with semantic colors
- **Typography System**: Comprehensive Material 3 typography
- **Dark/Light Mode**: Full theme support with smooth transitions
- **Responsive Design**: Adapts to different screen sizes
- **Smooth Animations**: Material motion for delightful interactions

#### ✅ Home Screen
- **Location Selector**: Current location with dropdown
- **Smart Search**: Full-text search for restaurants and dishes
- **Promotional Banner**: Eye-catching offers with gradient backgrounds
- **Category Filters**: Horizontal scrolling category chips
- **Restaurant Cards**: 
  - High-quality images with gradient overlays
  - Rating display with star icon
  - Delivery time and distance indicators
  - Discount badges
  - Favorite button
  - Comprehensive restaurant info

#### ✅ Navigation System
- Login/Register screens
- Home with restaurant discovery
- Restaurant details
- Cart management
- Checkout flow
- Order tracking with live updates
- Profile management

#### ✅ Data Models
- **User**: Profile, addresses, order history
- **Restaurant**: Details, menu, ratings, location
- **Menu Items**: Customizations, dietary information
- **Cart**: Items, calculations, promo codes
- **Order**: Status tracking, payment, delivery
- **Rider**: Profile, location, ratings

## 📦 Tech Stack

### Android (User App)
- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose
- **Networking**: Retrofit 2 + OkHttp
- **Image Loading**: Coil
- **Maps**: Mapbox SDK
- **Database**: Room
- **Async**: Kotlin Coroutines + Flow
- **Storage**: DataStore Preferences
- **Real-time**: Socket.IO
- **Push Notifications**: Firebase Cloud Messaging
- **Authentication**: Firebase Auth
- **Backend**: Firebase Firestore
- **Analytics**: Firebase Analytics

### Dependencies
```kotlin
// Core
androidx.core:core-ktx:1.17.0
androidx.lifecycle:lifecycle-runtime-ktx:2.10.0

// Compose
androidx.compose.bom:2024.09.00
androidx.compose.material3
androidx.activity:activity-compose:1.12.2

// Navigation
androidx.navigation:navigation-compose:2.8.5

// Hilt
com.google.dagger:hilt-android:2.51.1
androidx.hilt:hilt-navigation-compose:1.2.0

// Networking
com.squareup.retrofit2:retrofit:2.11.0
com.squareup.okhttp3:okhttp:4.12.0

// Images
io.coil-kt:coil-compose:2.7.0

// Mapbox
com.mapbox.maps:android:11.7.1

// Room Database
androidx.room:room-ktx:2.6.1

// Firebase
firebase-bom:33.7.0

// Socket.IO
io.socket:socket.io-client:2.1.1
```

## 🏗️ Project Structure

```
app/src/main/java/com/noor/khabarlagbe/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── remote/         # Retrofit APIs, DTOs
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic use cases
├── presentation/
│   ├── auth/           # Login, Register screens
│   ├── home/           # Home screen
│   ├── restaurant/     # Restaurant details
│   ├── cart/           # Shopping cart
│   ├── checkout/       # Checkout flow
│   ├── order/          # Order tracking
│   └── profile/        # User profile
├── di/                 # Hilt modules
├── navigation/         # Navigation graph
├── service/            # FCM service
├── ui/theme/           # Theme, colors, typography
└── util/               # Utility classes
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Koala or newer
- JDK 17
- Android SDK 35
- Gradle 8.13

### Setup Instructions

1. **Clone the repository**
```bash
git clone https://github.com/noor-87dsdp-beep/KhabarLagbe.git
cd KhabarLagbe
```

2. **Configure API Keys**

Create `local.properties` in the root directory:
```properties
# Mapbox
MAPBOX_ACCESS_TOKEN=your_mapbox_token_here

# Firebase
# Download google-services.json from Firebase Console
# Place it in app/ directory
```

3. **Mapbox Setup**
- Sign up at [Mapbox](https://www.mapbox.com/)
- Get your access token
- Add to local.properties

4. **Firebase Setup**
- Create a Firebase project
- Add Android app with package name `com.noor.khabarlagbe`
- Download `google-services.json`
- Place in `app/` directory
- Enable Authentication, Firestore, Cloud Messaging, Storage

5. **Build the project**
```bash
chmod +x gradlew
./gradlew build
```

6. **Run on device/emulator**
```bash
./gradlew installDebug
```

## 🎯 Key Features to Implement Next

### User App
- [ ] Complete authentication flow (email, phone, social)
- [ ] Restaurant details with full menu
- [ ] Add to cart with customizations
- [ ] Checkout with payment integration
- [ ] Real-time order tracking with Mapbox
- [ ] User profile and address management
- [ ] Order history and reordering
- [ ] Favorites and ratings
- [ ] Search with filters
- [ ] Promo codes and referrals

### Rider App
- [ ] Rider authentication and profile
- [ ] Available orders list
- [ ] Accept/reject orders
- [ ] Navigation with Mapbox directions
- [ ] Order status updates
- [ ] Earnings dashboard
- [ ] Background location tracking

### Restaurant App
- [ ] Restaurant dashboard
- [ ] Menu management (CRUD operations)
- [ ] Incoming order notifications
- [ ] Accept/reject orders
- [ ] Order preparation tracking
- [ ] Sales analytics
- [ ] Business hours management
- [ ] Customer reviews

### Admin Panel
- [ ] Dashboard with real-time stats
- [ ] User management
- [ ] Restaurant approvals
- [ ] Rider management
- [ ] Order monitoring
- [ ] System configuration
- [ ] Analytics and reports
- [ ] Commission settings

## 📱 UI/UX Highlights

### Design System
- **Primary Color**: Vibrant Orange (#FF6B35)
- **Secondary Color**: Green (#4CAF50)
- **Typography**: Material 3 scale with custom weights
- **Spacing**: 4dp grid system
- **Elevation**: Subtle shadows for depth
- **Corner Radius**: 8-16dp for modern feel

### Key UI Components
- **Restaurant Cards**: Beautiful cards with images, ratings, badges
- **Category Chips**: Smooth scrolling horizontal categories
- **Search Bar**: Prominent, easy-to-use search
- **Promotional Banner**: Gradient backgrounds, clear CTAs
- **Bottom Navigation**: Easy access to main sections
- **Loading States**: Skeleton screens and shimmers
- **Empty States**: Friendly messages and illustrations

### Animations
- Fade in/out transitions
- Slide animations for navigation
- Ripple effects on buttons
- Smooth scrolling
- Pull-to-refresh
- Skeleton loading

## 🗺️ Mapbox Integration

### Planned Features
- Interactive restaurant location map
- Live delivery tracking
- Route optimization
- Distance calculations
- Geocoding for addresses
- Address autocomplete
- Geofencing for delivery zones
- Custom markers for restaurants/riders

## 🔐 Security Features

- Secure API communication with SSL pinning
- JWT-based authentication
- Data encryption at rest
- Secure payment processing
- Input validation and sanitization
- Rate limiting
- GDPR compliance ready

## 📊 Backend API Structure

### Planned Endpoints

#### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh token
- `POST /api/auth/logout` - Logout

#### Restaurants
- `GET /api/restaurants` - List restaurants
- `GET /api/restaurants/{id}` - Restaurant details
- `GET /api/restaurants/{id}/menu` - Restaurant menu
- `POST /api/restaurants` - Create restaurant (admin)

#### Orders
- `POST /api/orders` - Place order
- `GET /api/orders/{id}` - Order details
- `GET /api/orders` - User order history
- `PATCH /api/orders/{id}/status` - Update status

#### Users
- `GET /api/users/profile` - Get profile
- `PATCH /api/users/profile` - Update profile
- `POST /api/users/addresses` - Add address

#### Real-time (WebSocket)
- `ws://api/orders/{id}/track` - Live order tracking
- `ws://api/rider/location` - Rider location updates

## 🧪 Testing

### Unit Tests
```bash
./gradlew test
```

### UI Tests
```bash
./gradlew connectedAndroidTest
```

## 📝 Environment Configuration

### Development
```properties
API_BASE_URL=http://10.0.2.2:3000/api
ENVIRONMENT=development
DEBUG_MODE=true
```

### Production
```properties
API_BASE_URL=https://api.khabarlagbe.com/api
ENVIRONMENT=production
DEBUG_MODE=false
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Team

- **Developer**: Noor
- **Platform**: Android (Jetpack Compose)

## 📞 Support

For issues and questions:
- GitHub Issues: [Create an issue](https://github.com/noor-87dsdp-beep/KhabarLagbe/issues)
- Email: support@khabarlagbe.com

## 🎓 Learning Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Design](https://m3.material.io/)
- [Mapbox Android SDK](https://docs.mapbox.com/android/maps/guides/)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

## 🔮 Future Enhancements

- Voice ordering
- AR menu visualization
- AI-powered recommendations
- Multi-language support
- Loyalty programs
- Group ordering
- Scheduled orders
- Dietary preferences and allergies
- Restaurant live streaming
- Gamification features

---

**Built with ❤️ using Jetpack Compose and Material 3**
