package com.noor.khabarlagbe.presentation.restaurant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.navigation.Screen
import com.noor.khabarlagbe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailsScreen(
    restaurantId: String,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // TODO: Replace with ViewModel and fetch restaurant by ID
    val restaurant = remember { getSampleRestaurantDetails(restaurantId) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Add to favorites */ }) {
                        Icon(Icons.Filled.FavoriteBorder, contentDescription = "Favorite")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                        BadgedBox(badge = { Badge { Text("2") } }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Restaurant Header
            item {
                Column {
                    // Cover Image
                    AsyncImage(
                        model = restaurant.coverImageUrl ?: restaurant.imageUrl,
                        contentDescription = restaurant.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Restaurant Info
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = restaurant.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = restaurant.cuisine.joinToString(", "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Restaurant Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            RestaurantStat(
                                icon = Icons.Filled.Star,
                                value = "${restaurant.rating}",
                                label = "${restaurant.totalReviews} reviews"
                            )
                            RestaurantStat(
                                icon = Icons.Filled.Schedule,
                                value = "${restaurant.deliveryTime} min",
                                label = "Delivery"
                            )
                            RestaurantStat(
                                icon = Icons.Filled.LocalShipping,
                                value = if (restaurant.deliveryFee == 0.0) "Free" else "₹${restaurant.deliveryFee}",
                                label = "Delivery fee"
                            )
                        }
                    }
                    
                    Divider()
                }
            }
            
            // Menu Categories
            restaurant.categories.forEach { category ->
                item {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                items(category.items) { menuItem ->
                    MenuItemCard(
                        item = menuItem,
                        onAddToCart = { /* TODO: Add to cart */ }
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Item Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (item.isVegetarian) {
                        Surface(
                            color = Success,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹${"%.2f".format(item.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            
            // Item Image and Add Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (item.imageUrl != null) {
                    Box {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Button(
                            onClick = onAddToCart,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("ADD")
                        }
                    }
                } else {
                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("ADD")
                    }
                }
            }
        }
    }
}

// Sample restaurant details
fun getSampleRestaurantDetails(id: String) = Restaurant(
    id = id,
    name = "Pizza Paradise",
    description = "Authentic Italian pizzas with fresh ingredients",
    imageUrl = "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=800",
    coverImageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1200",
    cuisine = listOf("Italian", "Pizza", "Fast Food"),
    rating = 4.5,
    totalReviews = 230,
    deliveryTime = 30,
    deliveryFee = 0.0,
    minOrderAmount = 100.0,
    isOpen = true,
    distance = 2.5,
    latitude = 0.0,
    longitude = 0.0,
    address = "123 Main St",
    tags = listOf("Featured", "30% OFF"),
    categories = listOf(
        MenuCategory(
            id = "1",
            name = "Pizzas",
            items = listOf(
                MenuItem(
                    id = "1",
                    name = "Margherita Pizza",
                    description = "Classic pizza with tomato sauce, mozzarella, and fresh basil",
                    price = 299.0,
                    imageUrl = "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=400",
                    isVegetarian = true
                ),
                MenuItem(
                    id = "2",
                    name = "Pepperoni Pizza",
                    description = "Loaded with pepperoni and mozzarella cheese",
                    price = 399.0,
                    imageUrl = "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=400",
                    isVegetarian = false
                )
            )
        ),
        MenuCategory(
            id = "2",
            name = "Sides",
            items = listOf(
                MenuItem(
                    id = "3",
                    name = "Garlic Bread",
                    description = "Crispy bread with garlic butter and herbs",
                    price = 99.0,
                    imageUrl = "https://images.unsplash.com/photo-1573140401552-388e8e2940c9?w=400",
                    isVegetarian = true
                )
            )
        )
    )
)
