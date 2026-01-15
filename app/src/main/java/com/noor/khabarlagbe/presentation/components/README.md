# KhabarLagbe Component Library

A production-ready, reusable Jetpack Compose component library for the KhabarLagbe User App. All components follow Material 3 design guidelines and the app's existing theme.

## 📦 Components Overview

### 1. **KhabarLagbeButton.kt**
Standardized button components with loading states and icon support.

#### Components:
- **PrimaryButton**: Filled button with orange background
- **SecondaryButton**: Outlined button with orange border
- **KhabarLagbeTextButton**: Text-only button with orange text

#### Usage:
```kotlin
PrimaryButton(
    text = "Order Now",
    onClick = { /* action */ },
    isLoading = false,
    enabled = true,
    icon = Icons.Filled.ShoppingCart
)

SecondaryButton(
    text = "View Menu",
    onClick = { /* action */ }
)

KhabarLagbeTextButton(
    text = "See All",
    onClick = { /* action */ }
)
```

#### Features:
- Loading state with spinner
- Enabled/disabled states
- Optional leading icons
- Consistent sizing (56dp height for primary/secondary, 48dp for text)
- Proper elevation and colors

---

### 2. **RestaurantCard.kt**
Reusable card for displaying restaurant information.

#### Usage:
```kotlin
RestaurantCard(
    restaurant = restaurant,
    onClick = { /* navigate to details */ },
    onFavoriteClick = { /* toggle favorite */ },
    isFavorite = false
)
```

#### Features:
- Restaurant image with gradient overlay
- Name, cuisines, rating, distance, delivery time
- Favorite button
- Discount badge support
- Closed state overlay
- Responsive layout

---

### 3. **MenuItemCard.kt**
Card for displaying menu items with add-to-cart functionality.

#### Usage:
```kotlin
MenuItemCard(
    menuItem = menuItem,
    quantity = cartQuantity,
    onQuantityChange = { newQty -> /* update cart */ },
    onCustomize = { /* show customization dialog */ }
)
```

#### Features:
- Item image, name, description, price
- Dietary badges (Veg, Vegan, Gluten-Free)
- Add to cart button
- Quantity selector when added
- Customization indicator
- Availability status

---

### 4. **OrderStatusTimeline.kt**
Vertical timeline showing order progression.

#### Usage:
```kotlin
OrderStatusTimeline(
    currentStep = 3,
    steps = listOf(
        "Order Placed",
        "Order Confirmed",
        "Preparing",
        "Ready for Pickup",
        "Picked Up",
        "On the Way",
        "Delivered"
    ),
    timestamps = listOf("10:30 AM", "10:32 AM", ...)
)
```

#### Features:
- Visual progress indicators
- Completed/current/pending states
- Color-coded steps (green for completed, orange for current)
- Optional timestamps
- Connector lines between steps

---

### 5. **AddressCard.kt**
Card for displaying and managing delivery addresses.

#### Usage:
```kotlin
AddressCard(
    address = address,
    isSelected = true,
    onSelect = { /* select for checkout */ },
    onEdit = { /* show edit dialog */ },
    onDelete = { /* confirm delete */ },
    showActions = true
)
```

#### Features:
- Label badges (Home/Work/Other) with icons
- Default badge for primary address
- Full formatted address display
- Delivery instructions
- Edit and delete actions
- Selectable state with border highlight

---

### 6. **EmptyState.kt**
Generic empty state component with pre-configured variants.

#### Usage:
```kotlin
// Generic
EmptyState(
    icon = Icons.Filled.ShoppingCart,
    title = "Your cart is empty",
    message = "Add items to get started",
    actionButtonText = "Browse Restaurants",
    onActionClick = { /* navigate */ }
)

// Pre-configured variants
EmptyCartState(onBrowseClick = { })
EmptyFavoritesState(onExploreClick = { })
EmptyOrdersState(onOrderClick = { })
EmptySearchState()
```

#### Features:
- Large icon placeholder
- Title and message
- Optional action button
- Pre-configured variants for common scenarios

---

### 7. **LoadingIndicator.kt**
Loading indicators and skeleton screens.

#### Components:
- **LoadingIndicator**: Full-screen circular progress
- **InlineLoadingIndicator**: Compact inline loader
- **RestaurantCardSkeleton**: Shimmer skeleton for restaurant cards
- **MenuItemCardSkeleton**: Shimmer skeleton for menu items
- **RestaurantListSkeleton**: List of skeleton cards

#### Usage:
```kotlin
LoadingIndicator(message = "Loading restaurants...")

RestaurantCardSkeleton()

RestaurantListSkeleton(count = 3)
```

#### Features:
- Shimmer animation effect
- Multiple skeleton variants
- Optional loading messages
- Inline and full-screen options

---

### 8. **SearchBar.kt**
Standardized search input field.

#### Usage:
```kotlin
SearchBar(
    query = searchQuery,
    onQueryChange = { query -> /* update */ },
    placeholder = "Search for restaurants...",
    showFilterButton = true,
    onFilterClick = { /* show filters */ }
)
```

#### Features:
- Search icon
- Clear button (shows when query not empty)
- Optional filter button
- Consistent styling with shadow
- Single-line input

---

### 9. **FilterChip.kt**
Category and filter chips with selected states.

#### Components:
- **FilterChip**: Single filter chip
- **FilterChipGroup**: Group of chips with selection

#### Usage:
```kotlin
FilterChip(
    label = "Pizza",
    isSelected = true,
    onClick = { /* select */ },
    icon = Icons.Filled.LocalPizza
)

FilterChipGroup(
    items = listOf("All", "Pizza", "Burger", "Asian"),
    selectedItem = "Pizza",
    onItemSelect = { item -> /* update selection */ }
)
```

#### Features:
- Selected/unselected states
- Optional icons
- Elevation on selection
- Color transitions

---

### 10. **QuantitySelector.kt**
Quantity adjustment controls.

#### Components:
- **QuantitySelector**: Standard size selector
- **CompactQuantitySelector**: Smaller variant

#### Usage:
```kotlin
QuantitySelector(
    quantity = quantity,
    onQuantityChange = { newQty -> /* update */ },
    minQuantity = 1,
    maxQuantity = 10
)

CompactQuantitySelector(
    quantity = quantity,
    onQuantityChange = { newQty -> /* update */ }
)
```

#### Features:
- Plus/minus buttons
- Min/max constraints
- Disabled states at limits
- Visual feedback
- Compact variant for tight spaces

---

### 11. **RatingStars.kt**
Star rating displays with multiple variants.

#### Components:
- **RatingStars**: Standard star display
- **LargeRating**: Large format for detail screens
- **CompactRating**: Minimal format for lists

#### Usage:
```kotlin
RatingStars(
    rating = 4.5,
    reviewCount = 230,
    showNumeric = true,
    starSize = 16.dp
)

LargeRating(
    rating = 4.8,
    reviewCount = 542
)

CompactRating(rating = 4.2)
```

#### Features:
- Half-star support
- Configurable star size
- Optional numeric rating
- Optional review count
- Multiple display variants

---

### 12. **ErrorScreen.kt**
Error displays with pre-configured variants.

#### Components:
- **ErrorScreen**: Generic error screen
- **NetworkErrorScreen**: No internet variant
- **NotFoundErrorScreen**: 404 variant
- **ServerErrorScreen**: Server error variant
- **InlineError**: Small error message
- **ErrorDialog**: Error alert dialog

#### Usage:
```kotlin
NetworkErrorScreen(onRetry = { /* retry */ })

ServerErrorScreen(onRetry = { /* retry */ })

InlineError(message = "Invalid email")

ErrorDialog(
    title = "Error",
    message = "Something went wrong",
    onDismiss = { },
    onConfirm = { /* retry */ }
)
```

#### Features:
- Full-screen and inline variants
- Retry button support
- Customizable icons and messages
- Pre-configured for common errors

---

### 13. **PriceBreakdownCard.kt**
Itemized price breakdown display.

#### Components:
- **PriceBreakdownCard**: Full price breakdown
- **CompactPriceSummary**: Summary for bottom bars

#### Usage:
```kotlin
PriceBreakdownCard(
    subtotal = 450.0,
    deliveryFee = 30.0,
    tax = 45.0,
    discount = 90.0,
    promoCode = "FIRST30"
)

CompactPriceSummary(
    total = 435.0,
    itemCount = 3
)
```

#### Features:
- Subtotal, fees, tax, discount rows
- Highlighted total
- Optional promo code display
- Clean row-based layout
- Automatic total calculation

---

### 14. **BottomSheetHandle.kt**
Standard bottom sheet drag handle.

#### Usage:
```kotlin
BottomSheetHandle()
```

#### Features:
- Centered visual indicator
- Consistent styling
- Standard Material 3 appearance

---

### 15. **CustomTextField.kt**
Standardized text input fields.

#### Components:
- **CustomTextField**: Generic text field
- **PasswordTextField**: Password field with visibility toggle

#### Usage:
```kotlin
CustomTextField(
    value = value,
    onValueChange = { value = it },
    label = "Email",
    placeholder = "Enter your email",
    leadingIcon = Icons.Filled.Email,
    isError = false,
    errorMessage = "Invalid email",
    helperText = "We'll send confirmation here"
)

PasswordTextField(
    value = password,
    onValueChange = { password = it },
    label = "Password"
)
```

#### Features:
- Label, placeholder, helper text
- Leading and trailing icons
- Error states with messages
- Password visibility toggle
- Keyboard type and IME action support
- Single/multi-line support
- Read-only and enabled states

---

## 🎨 Design Principles

All components follow these principles:

1. **Consistency**: Use existing theme colors, typography, and spacing
2. **Accessibility**: Proper content descriptions and touch targets
3. **Reusability**: Highly configurable with sensible defaults
4. **Material 3**: Built on Material 3 components
5. **Preview Support**: Every component has preview functions
6. **Documentation**: Comprehensive KDoc comments

## 🎯 Theme Integration

Components use the app's theme colors:
- **Primary**: Orange (`#FF6B35`)
- **Secondary**: Green (`#4CAF50`)
- **Success**: Green for positive states
- **Error**: Red for error states
- **Rating**: Yellow for stars
- **Discount**: Pink for discount badges

## 📝 Best Practices

1. **Always provide content descriptions** for accessibility
2. **Use preview functions** during development
3. **Leverage remember and derivedStateOf** for performance
4. **Handle edge cases** (empty lists, null values, etc.)
5. **Test different states** (loading, error, empty, success)

## 🚀 Usage in Screens

Import components in your screens:

```kotlin
import com.noor.khabarlagbe.presentation.components.*
```

Then use them directly in your composables.

## 📱 Preview Mode

All components include preview functions. View them in Android Studio's preview pane during development.

---

## 📄 File Structure

```
presentation/
└── components/
    ├── AddressCard.kt
    ├── BottomSheetHandle.kt
    ├── CustomTextField.kt
    ├── EmptyState.kt
    ├── ErrorScreen.kt
    ├── FilterChip.kt
    ├── KhabarLagbeButton.kt
    ├── LoadingIndicator.kt
    ├── MenuItemCard.kt
    ├── OrderStatusTimeline.kt
    ├── PriceBreakdownCard.kt
    ├── QuantitySelector.kt
    ├── RatingStars.kt
    ├── RestaurantCard.kt
    └── SearchBar.kt
```

## ✅ Verification

All components have been verified to:
- ✅ Compile successfully
- ✅ Follow Kotlin and Compose best practices
- ✅ Include KDoc documentation
- ✅ Include preview functions
- ✅ Use existing theme
- ✅ Handle different states (loading, error, empty)
- ✅ Support accessibility

## 🔄 Future Enhancements

Potential improvements:
- Add animation variants
- Add haptic feedback
- Add sound effects
- Create component variants for tablet/foldable
- Add theme-specific adaptations
