# Component Library - Quick Start Guide

## 🚀 Getting Started

All components are located in:
```
app/src/main/java/com/noor/khabarlagbe/presentation/components/
```

Import them in your screen:
```kotlin
import com.noor.khabarlagbe.presentation.components.*
```

## 📋 Common Usage Patterns

### 1. Restaurant Listing Screen

```kotlin
@Composable
fun RestaurantListScreen(
    restaurants: List<Restaurant>,
    isLoading: Boolean,
    onRestaurantClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Column {
        // Search bar
        var searchQuery by remember { mutableStateOf("") }
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            showFilterButton = true,
            onFilterClick = { /* Show filters */ }
        )
        
        // Category filters
        var selectedCategory by remember { mutableStateOf("All") }
        FilterChipGroup(
            items = listOf("All", "Pizza", "Burger", "Asian", "Dessert"),
            selectedItem = selectedCategory,
            onItemSelect = { selectedCategory = it }
        )
        
        // Restaurant list
        when {
            isLoading -> RestaurantListSkeleton(count = 5)
            restaurants.isEmpty() -> EmptySearchState()
            else -> {
                LazyColumn {
                    items(restaurants) { restaurant ->
                        RestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.id) },
                            onFavoriteClick = { onFavoriteClick(restaurant.id) }
                        )
                    }
                }
            }
        }
    }
}
```

### 2. Menu Screen with Cart

```kotlin
@Composable
fun MenuScreen(
    menuItems: List<MenuItem>,
    cartItems: Map<String, Int>,
    onQuantityChange: (String, Int) -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = Primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CompactPriceSummary(
                            total = calculateTotal(),
                            itemCount = cartItems.values.sum()
                        )
                        
                        PrimaryButton(
                            text = "Checkout",
                            onClick = onCheckout,
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                MenuItemCard(
                    menuItem = item,
                    quantity = cartItems[item.id] ?: 0,
                    onQuantityChange = { qty ->
                        onQuantityChange(item.id, qty)
                    }
                )
            }
        }
    }
}
```

### 3. Checkout Screen

```kotlin
@Composable
fun CheckoutScreen(
    addresses: List<Address>,
    selectedAddressId: String?,
    subtotal: Double,
    deliveryFee: Double,
    discount: Double,
    onAddressSelect: (String) -> Unit,
    onPlaceOrder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Address Section
        Text(
            text = "Delivery Address",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (addresses.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.LocationOn,
                title = "No addresses saved",
                message = "Add a delivery address to continue",
                actionButtonText = "Add Address",
                onActionClick = { /* Navigate to add address */ }
            )
        } else {
            addresses.forEach { address ->
                AddressCard(
                    address = address,
                    isSelected = address.id == selectedAddressId,
                    onSelect = { onAddressSelect(address.id) },
                    showActions = false
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Price Breakdown
        PriceBreakdownCard(
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            promoCode = if (discount > 0) "FIRST30" else null
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Place Order Button
        PrimaryButton(
            text = "Place Order",
            onClick = onPlaceOrder,
            enabled = selectedAddressId != null
        )
    }
}
```

### 4. Order Tracking Screen

```kotlin
@Composable
fun OrderTrackingScreen(
    order: Order,
    currentStatus: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Order Info Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                RatingStars(
                    rating = order.restaurant.rating,
                    reviewCount = order.restaurant.totalReviews
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Order Status Timeline
        OrderStatusTimeline(
            currentStep = currentStatus,
            timestamps = order.statusTimestamps
        )
    }
}
```

### 5. Profile Screen with Error Handling

```kotlin
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onRetry: () -> Unit,
    onLogout: () -> Unit
) {
    when (uiState) {
        is ProfileUiState.Loading -> {
            LoadingIndicator(message = "Loading profile...")
        }
        
        is ProfileUiState.Error -> {
            if (uiState.isNetworkError) {
                NetworkErrorScreen(onRetry = onRetry)
            } else {
                ErrorScreen(
                    message = uiState.message,
                    onRetry = onRetry
                )
            }
        }
        
        is ProfileUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile content...
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SecondaryButton(
                    text = "Logout",
                    onClick = onLogout,
                    icon = Icons.Filled.ExitToApp
                )
            }
        }
    }
}
```

### 6. Form with Custom Text Fields

```kotlin
@Composable
fun EditProfileForm(
    name: String,
    email: String,
    phone: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomTextField(
            value = name,
            onValueChange = onNameChange,
            label = "Full Name",
            placeholder = "Enter your name",
            leadingIcon = Icons.Filled.Person,
            isError = name.isBlank(),
            errorMessage = if (name.isBlank()) "Name is required" else null
        )
        
        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Email",
            placeholder = "your@email.com",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email,
            isError = !email.contains("@"),
            errorMessage = if (!email.contains("@")) "Invalid email" else null
        )
        
        CustomTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = "Phone",
            placeholder = "+880 1XXX-XXXXXX",
            leadingIcon = Icons.Filled.Phone,
            keyboardType = KeyboardType.Phone,
            helperText = "We'll use this for order updates"
        )
        
        var password by remember { mutableStateOf("") }
        PasswordTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password"
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        PrimaryButton(
            text = "Save Changes",
            onClick = onSave,
            enabled = name.isNotBlank() && email.contains("@")
        )
    }
}
```

### 7. Cart with Empty State

```kotlin
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onCheckout: () -> Unit,
    onBrowse: () -> Unit
) {
    if (cartItems.isEmpty()) {
        EmptyCartState(onBrowseClick = onBrowse)
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    MenuItemCard(
                        menuItem = item.menuItem,
                        quantity = item.quantity,
                        onQuantityChange = { /* update */ }
                    )
                }
            }
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CompactPriceSummary(
                        total = cartItems.sumOf { it.totalPrice },
                        itemCount = cartItems.size
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    PrimaryButton(
                        text = "Proceed to Checkout",
                        onClick = onCheckout
                    )
                }
            }
        }
    }
}
```

## 🎯 Tips for Using Components

### 1. **State Hoisting**
Always hoist state to the parent composable:
```kotlin
var quantity by remember { mutableStateOf(0) }
QuantitySelector(
    quantity = quantity,
    onQuantityChange = { quantity = it }
)
```

### 2. **Loading States**
Show skeletons during initial load:
```kotlin
if (isLoading && items.isEmpty()) {
    RestaurantListSkeleton()
} else {
    // Show actual content
}
```

### 3. **Error Handling**
Use appropriate error screens:
```kotlin
when (error) {
    is NetworkError -> NetworkErrorScreen(onRetry)
    is NotFoundError -> NotFoundErrorScreen()
    else -> ErrorScreen(error.message, onRetry)
}
```

### 4. **Empty States**
Always handle empty states gracefully:
```kotlin
if (items.isEmpty()) {
    EmptyOrdersState(onOrderClick = { /* action */ })
} else {
    // Show items
}
```

### 5. **Accessibility**
Components include content descriptions, but add context:
```kotlin
IconButton(
    onClick = { /* action */ },
    modifier = Modifier.semantics {
        contentDescription = "Remove ${item.name} from cart"
    }
) { /* ... */ }
```

## 📚 Additional Resources

- **Full Documentation**: See `components/README.md`
- **Implementation Details**: See `COMPONENT_LIBRARY_SUMMARY.md`
- **Component Source**: Browse `app/src/main/java/com/noor/khabarlagbe/presentation/components/`

## 🤝 Contributing

When adding new components:
1. Follow existing patterns
2. Add KDoc documentation
3. Include preview function
4. Update README.md
5. Test different states

---

Happy coding! 🚀
