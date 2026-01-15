# Repository Layer Implementation

This directory contains the complete implementation of the repository pattern for the KhabarLagbe User App data layer.

## Architecture Overview

The repository layer acts as a single source of truth for data access, abstracting the data sources (API, Room database, DataStore) from the domain layer and ViewModels.

```
ViewModels → Repository Interfaces (domain/) → Repository Implementations (data/repository/) → Data Sources (API/Room/DataStore)
```

## Structure

```
data/
├── local/
│   ├── dao/              # Room DAOs for database operations
│   ├── entity/           # Room entities for database tables
│   └── preferences/      # DataStore preferences manager
├── remote/
│   ├── api/             # Retrofit API interfaces
│   └── dto/             # Data Transfer Objects for API responses
├── mappers/             # Extension functions to map between DTOs, Entities, and Domain models
├── repository/          # Repository implementations
└── util/                # Utility classes (Resource wrapper)
```

## Repository Implementations

### 1. AuthRepositoryImpl
**Location**: `data/repository/AuthRepositoryImpl.kt`

Handles authentication operations:
- Login with email/password
- Login with phone/OTP
- User registration
- OTP sending and verification
- Logout
- Token management via DataStore
- User caching in Room

**Dependencies**:
- `AuthApi` - For authentication API calls
- `UserDao` - For local user caching
- `AppPreferences` - For token storage

**Key Features**:
- Stores auth token in DataStore for persistence
- Caches user data in Room for offline access
- Provides reactive user state via Flow

### 2. RestaurantRepositoryImpl
**Location**: `data/repository/RestaurantRepositoryImpl.kt`

Handles restaurant data operations:
- Fetch all restaurants
- Get restaurant details with menu
- Search restaurants
- Filter by category
- Get nearby restaurants
- Manage favorites (add/remove)
- Check favorite status

**Dependencies**:
- `RestaurantApi` - For restaurant data from backend
- `FavoriteDao` - For local favorites caching

**Key Features**:
- Reactive restaurant list via Flow
- Location-based filtering
- Favorites persistence in Room
- Comprehensive search and filtering

### 3. CartRepositoryImpl
**Location**: `data/repository/CartRepositoryImpl.kt`

Handles shopping cart operations:
- Add items to cart
- Update item quantity
- Remove items
- Clear cart
- Apply/remove promo codes
- Calculate totals (subtotal, tax, delivery fee)

**Dependencies**:
- `CartDao` - For cart persistence in Room

**Key Features**:
- Fully local cart using Room
- Reactive cart updates via Flow
- Automatic price calculations
- Promo code validation (stub for API integration)
- Supports customizations serialization

**Constants**:
- Tax Rate: 5%
- Delivery Fee: 30 BDT (fixed)

### 4. OrderRepositoryImpl
**Location**: `data/repository/OrderRepositoryImpl.kt`

Handles order operations:
- Place new orders
- Get order details
- Fetch order history
- Get active orders
- Cancel orders
- Track orders in real-time
- Rate completed orders

**Dependencies**:
- `OrderApi` - For order management API
- `AppPreferences` - For auth token

**Key Features**:
- Real-time order tracking via Flow (polling every 10s)
- Proper error handling for network issues
- Support for order status filtering
- Rating system integration

**Note**: Real-time tracking currently uses polling. Consider integrating WebSocket/SSE for production.

### 5. UserRepositoryImpl
**Location**: `data/repository/UserRepositoryImpl.kt`

Handles user profile and address management:
- Get user profile
- Update profile (name, phone, image)
- Manage addresses (CRUD operations)
- Set default address
- Upload profile image (stub)

**Dependencies**:
- `AuthApi` - For profile updates
- `UserDao` - For user caching
- `AddressDao` - For address management
- `AppPreferences` - For user ID

**Key Features**:
- Reactive profile data via Flow
- Complete address management
- Default address handling
- Profile image upload placeholder

## Data Mappers

### Purpose
Mapper functions convert between different data representations:
- DTOs (from API) → Domain Models
- Entities (from Room) → Domain Models
- Domain Models → Entities/DTOs

### Available Mappers

#### AuthMapper.kt
- `UserDto.toDomainModel()` - Convert API user to domain model
- `UserDto.toEntity()` - Convert API user to Room entity
- `UserEntity.toDomainModel()` - Convert Room entity to domain model

#### RestaurantMapper.kt
- `RestaurantDto.toDomainModel()` - Convert API restaurant to domain model
- `RestaurantDto.toFavoriteEntity()` - Convert to favorite entity
- `FavoriteEntity.toDomainModel()` - Convert favorite entity to domain model
- `MenuCategoryDto.toDomainModel()` - Convert menu category
- `MenuItemDto.toDomainModel()` - Convert menu item
- `CustomizationOptionDto.toDomainModel()` - Convert customization options

#### OrderMapper.kt
- `OrderDto.toDomainModel()` - Convert API order to domain model
- `OrderItemDto.toDomainModel()` - Convert order item
- `RiderDto.toDomainModel()` - Convert rider info
- `OrderTrackingDto.toDomainModel()` - Convert tracking data
- `String.toOrderStatus()` - Parse order status enum
- `String.toPaymentMethod()` - Parse payment method enum
- `String.toPaymentStatus()` - Parse payment status enum

#### AddressMapper.kt
- `AddressDto.toDomainModel()` - Convert API address to domain model
- `AddressEntity.toDomainModel()` - Convert Room entity to domain model
- `Address.toEntity()` - Convert domain model to Room entity
- `Address.toDto()` - Convert domain model to API DTO

## Utilities

### Resource.kt
A sealed class for wrapping operation results with three states:
- `Resource.Success<T>` - Successful operation with data
- `Resource.Error` - Failed operation with error message
- `Resource.Loading` - Operation in progress

**Helper Function**:
```kotlin
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T>
```

### AppPreferences.kt
DataStore-based preferences manager for storing:
- Authentication token
- Refresh token
- User ID

**Key Methods**:
- `saveAuthToken(token: String)`
- `getAuthToken(): Flow<String?>`
- `saveUserId(userId: String)`
- `getUserId(): String?`
- `clearAuthData()`

## Dependency Injection

### RepositoryModule.kt
Hilt module that binds repository interfaces to implementations:
```kotlin
@Binds
@Singleton
abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
// ... other bindings
```

### PreferencesModule.kt
Provides AppPreferences instance:
```kotlin
@Provides
@Singleton
fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences
```

## Usage in ViewModels

### Example: Using AuthRepository
```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                // Handle success
            }.onFailure { error ->
                // Handle error
            }
        }
    }
    
    val currentUser: Flow<User?> = authRepository.getCurrentUser()
}
```

### Example: Using RestaurantRepository
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    
    val restaurants: Flow<List<Restaurant>> = 
        restaurantRepository.getRestaurants()
    
    fun toggleFavorite(restaurantId: String) {
        viewModelScope.launch {
            restaurantRepository.toggleFavorite(restaurantId)
        }
    }
}
```

### Example: Using CartRepository
```kotlin
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {
    
    val cart: Flow<Cart?> = cartRepository.getCart()
    
    fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(itemId, quantity)
        }
    }
}
```

## Error Handling

All repository methods return `Result<T>` for operations that can fail:
```kotlin
result.onSuccess { data ->
    // Handle successful result
}.onFailure { exception ->
    // Handle error - exception.message contains user-friendly message
}
```

Flow-based methods emit empty lists or null on errors to prevent crashes.

## Testing Considerations

### Unit Testing
- Mock API interfaces (AuthApi, RestaurantApi, OrderApi)
- Mock DAOs (UserDao, FavoriteDao, CartDao, AddressDao)
- Mock AppPreferences
- Test each repository method independently

### Integration Testing
- Test with real Room database (in-memory)
- Test mapper functions for data transformation
- Test DataStore operations

## Future Enhancements

1. **Real-time Order Tracking**
   - Replace polling with WebSocket/SSE
   - Integrate Socket.IO for live updates

2. **Promo Code API Integration**
   - Connect validatePromoCode to backend
   - Add promo code listing API

3. **Image Upload**
   - Implement actual multipart upload for profile images
   - Add progress tracking

4. **Caching Strategy**
   - Add timestamp-based cache invalidation
   - Implement proper offline-first strategy

5. **Error Analytics**
   - Log errors to analytics service
   - Track API failure rates

6. **Request Retry Logic**
   - Add exponential backoff for failed requests
   - Implement network connectivity checks

## Dependencies Required

Ensure these dependencies are in your `build.gradle.kts`:
```kotlin
// Hilt
implementation("com.google.dagger:hilt-android:2.48")
ksp("com.google.dagger:hilt-android-compiler:2.48")

// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Room
implementation("androidx.room:room-runtime:2.6.0")
implementation("androidx.room:room-ktx:2.6.0")
ksp("androidx.room:room-compiler:2.6.0")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.0.0")

// Kotlin Serialization
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
```

## Notes

- All repository implementations are annotated with `@Singleton` to ensure single instance
- All repository methods are designed to be called from ViewModels within `viewModelScope`
- Flow-based methods automatically handle lifecycle and cancellation
- Auth token is automatically attached to API calls that require authentication
- Cart calculations include 5% tax and 30 BDT delivery fee (configurable)

## Contributing

When adding new repository methods:
1. Add interface method to domain repository
2. Implement in repository implementation
3. Handle errors properly with Result wrapper
4. Add appropriate mappers if needed
5. Update this README with changes
6. Write unit tests for new functionality
