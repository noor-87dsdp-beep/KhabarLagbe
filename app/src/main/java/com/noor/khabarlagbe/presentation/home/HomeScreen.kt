package com.noor.khabarlagbe.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.noor.khabarlagbe.domain.model.Restaurant
import com.noor.khabarlagbe.navigation.Screen
import com.noor.khabarlagbe.ui.theme.*
import com.noor.khabarlagbe.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    
    // TODO: Replace with ViewModel and proper data loading
    // For production: Implement HomeViewModel with StateFlow for restaurants
    // and handle loading, success, and error states
    // Example:
    // val uiState by viewModel.restaurantsState.collectAsState()
    // when (uiState) {
    //     is Loading -> ShowLoadingShimmer()
    //     is Success -> ShowRestaurantList(data)
    //     is Error -> ShowErrorMessage()
    // }
    // Currently using sample data for UI demonstration
    val restaurants = remember { getSampleRestaurants() }
    val categories = Constants.Cuisines.CATEGORIES
    
    Scaffold(
        topBar = {
            HomeTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onCartClick = { navController.navigate(Screen.Cart.route) },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
            )
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Promotional Banner
            PromotionalBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Categories
            CategorySection(
                categories = categories,
                selectedCategory = selectedFilter,
                onCategorySelect = { selectedFilter = it }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Featured Restaurants
            SectionHeader(
                title = "Featured Restaurants",
                actionText = "See All"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            restaurants.forEach { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onClick = {
                        navController.navigate(Screen.RestaurantDetails.createRoute(restaurant.id))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top row with location and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = Primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Deliver to",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Current Location",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Change location",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                
                // Action buttons
                Row {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                Badge { Text("3") }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Profile"
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search bar
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                placeholder = { Text("Search for restaurants or dishes...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceLight,
                    unfocusedContainerColor = SurfaceLight,
                    disabledContainerColor = SurfaceLight,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
    }
}

@Composable
fun PromotionalBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Primary
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Primary,
                                PrimaryVariant
                            )
                        )
                    )
            )
            
            // Content
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "30% OFF",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "On your first order",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Order Now", fontWeight = FontWeight.Bold)
                    }
                }
                
                Icon(
                    imageVector = Icons.Filled.LocalOffer,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun CategorySection(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = category == selectedCategory,
                    onClick = { onCategorySelect(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Primary else SurfaceVariant
    val contentColor = if (isSelected) Color.White else TextPrimary
    
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        TextButton(onClick = onActionClick) {
            Text(
                text = actionText,
                color = Primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight
        )
    ) {
        Column {
            // Restaurant Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
                
                // Discount badge (if applicable)
                if (restaurant.tags.contains("30% OFF")) {
                    Surface(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart),
                        color = Discount,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "30% OFF",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                // Favorite button
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Add to favorites",
                        tint = Primary
                    )
                }
            }
            
            // Restaurant Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = restaurant.cuisine.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Rating,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${restaurant.rating}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " (${restaurant.totalReviews})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    
                    // Delivery Time
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = "Delivery time",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${restaurant.deliveryTime} min",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    
                    // Distance
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "Distance",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${restaurant.distance} km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Divider()
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Delivery Fee and Min Order
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Delivery: ${Constants.CURRENCY_SYMBOL}${restaurant.deliveryFee}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (restaurant.deliveryFee == 0.0) Success else TextSecondary,
                        fontWeight = if (restaurant.deliveryFee == 0.0) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = "Min Order: ${Constants.CURRENCY_SYMBOL}${restaurant.minOrderAmount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

// Sample data - Bangladesh restaurants
fun getSampleRestaurants() = listOf(
    Restaurant(
        id = "1",
        name = "Star Kabab & Restaurant",
        description = "Authentic Mughlai cuisine with premium kacchi biriyani",
        imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800",
        cuisine = listOf("Bengali Traditional", "Biriyani & Tehari", "Mughlai"),
        rating = 4.6,
        totalReviews = 450,
        deliveryTime = 35,
        deliveryFee = 0.0,
        minOrderAmount = 300.0,
        isOpen = true,
        distance = 2.1,
        latitude = 23.7808875,
        longitude = 90.4133503,
        address = "Gulshan 2, Dhaka",
        tags = listOf("Featured", "Free Delivery")
    ),
    Restaurant(
        id = "2",
        name = "Kacchi Bhai",
        description = "Home of the best kacchi biriyani in Dhaka",
        imageUrl = "https://images.unsplash.com/photo-1633945274405-b6c8069047b0?w=800",
        cuisine = listOf("Kacchi House", "Biriyani & Tehari"),
        rating = 4.8,
        totalReviews = 890,
        deliveryTime = 40,
        deliveryFee = 30.0,
        minOrderAmount = 400.0,
        isOpen = true,
        distance = 3.5,
        latitude = 23.7925,
        longitude = 90.4078,
        address = "Banani, Dhaka",
        tags = listOf("Popular", "Most Ordered")
    ),
    Restaurant(
        id = "3",
        name = "Sultans Dine",
        description = "Traditional Bengali and Mughlai dishes",
        imageUrl = "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=800",
        cuisine = listOf("Bengali Traditional", "Mughlai", "Biriyani & Tehari"),
        rating = 4.5,
        totalReviews = 320,
        deliveryTime = 30,
        deliveryFee = 40.0,
        minOrderAmount = 350.0,
        isOpen = true,
        distance = 1.8,
        latitude = 23.8103,
        longitude = 90.4125,
        address = "Uttara, Dhaka",
        tags = listOf("Featured")
    ),
    Restaurant(
        id = "4",
        name = "Chillox",
        description = "Modern café with fusion dishes and great ambiance",
        imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800",
        cuisine = listOf("Café & Coffee", "Fast Food", "Chinese-Bangla"),
        rating = 4.4,
        totalReviews = 275,
        deliveryTime = 25,
        deliveryFee = 50.0,
        minOrderAmount = 250.0,
        isOpen = true,
        distance = 2.8,
        latitude = 23.7465,
        longitude = 90.3763,
        address = "Dhanmondi, Dhaka",
        tags = listOf("Trending")
    ),
    Restaurant(
        id = "5",
        name = "Fakruddin Biriyani",
        description = "Legendary biriyani since 1985",
        imageUrl = "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800",
        cuisine = listOf("Biriyani & Tehari", "Bengali Traditional"),
        rating = 4.7,
        totalReviews = 1240,
        deliveryTime = 45,
        deliveryFee = 35.0,
        minOrderAmount = 500.0,
        isOpen = true,
        distance = 4.2,
        latitude = 23.7644,
        longitude = 90.3686,
        address = "Old Dhaka",
        tags = listOf("Featured", "Popular", "Most Ordered")
    ),
    Restaurant(
        id = "6",
        name = "Café Cinnamon",
        description = "Premium coffee and continental breakfast",
        imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=800",
        cuisine = listOf("Café & Coffee", "Breakfast", "Healthy"),
        rating = 4.3,
        totalReviews = 156,
        deliveryTime = 20,
        deliveryFee = 40.0,
        minOrderAmount = 200.0,
        isOpen = true,
        distance = 1.5,
        latitude = 23.7808,
        longitude = 90.4217,
        address = "Gulshan 1, Dhaka",
        tags = listOf("New")
    ),
    Restaurant(
        id = "7",
        name = "KFC Bangladesh",
        description = "Finger lickin' good fried chicken",
        imageUrl = "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=800",
        cuisine = listOf("Fast Food", "American"),
        rating = 4.2,
        totalReviews = 580,
        deliveryTime = 30,
        deliveryFee = 0.0,
        minOrderAmount = 300.0,
        isOpen = true,
        distance = 2.3,
        latitude = 23.7925,
        longitude = 90.4078,
        address = "Banani, Dhaka",
        tags = listOf("Free Delivery")
    ),
    Restaurant(
        id = "8",
        name = "The Food Republic",
        description = "Multi-cuisine restaurant with diverse menu",
        imageUrl = "https://images.unsplash.com/photo-1552566626-52f8b828add9?w=800",
        cuisine = listOf("Chinese-Bangla", "Thai-Bangla", "Fast Food"),
        rating = 4.4,
        totalReviews = 412,
        deliveryTime = 35,
        deliveryFee = 45.0,
        minOrderAmount = 350.0,
        isOpen = true,
        distance = 3.1,
        latitude = 23.8041,
        longitude = 90.4152,
        address = "Bashundhara, Dhaka",
        tags = listOf("Popular")
    )
)
