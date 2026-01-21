package com.noor.khabarlagbe.presentation.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.noor.khabarlagbe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareOrderScreen(
    navController: NavController,
    orderId: String? = null
) {
    // Sample order data
    val restaurantName = "Star Kabab"
    val orderItems = listOf("Beef Tehari", "Chicken Roast", "Firni")
    val rating = 5

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Preview Card
            SharePreviewCard(
                restaurantName = restaurantName,
                orderItems = orderItems,
                rating = rating
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Share via",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Social share options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ShareOption(
                    icon = Icons.Filled.Facebook,
                    label = "Facebook",
                    color = Color(0xFF1877F2),
                    onClick = { /* Share to Facebook */ }
                )
                ShareOption(
                    icon = Icons.Filled.Chat,
                    label = "WhatsApp",
                    color = Color(0xFF25D366),
                    onClick = { /* Share to WhatsApp */ }
                )
                ShareOption(
                    icon = Icons.Filled.CameraAlt,
                    label = "Instagram",
                    color = Color(0xFFE4405F),
                    onClick = { /* Share to Instagram */ }
                )
                ShareOption(
                    icon = Icons.AutoMirrored.Filled.Message,
                    label = "Messages",
                    color = Color(0xFF007AFF),
                    onClick = { /* Share via Messages */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(24.dp))

            // Copy link button
            OutlinedButton(
                onClick = { /* Copy link */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Link, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy Link")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // More options
            OutlinedButton(
                onClick = { /* More share options */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.MoreHoriz, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("More Options")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Earn points info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Stars,
                        null,
                        tint = Primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Earn 10 bonus points",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "When friends order using your shared link",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SharePreviewCard(
    restaurantName: String,
    orderItems: List<String>,
    rating: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            // Header image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Restaurant,
                        null,
                        tint = Primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = restaurantName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Just ordered from $restaurantName! 🍽️",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                orderItems.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "My rating: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    repeat(5) { index ->
                        Icon(
                            if (index < rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                            null,
                            tint = Rating,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Fastfood,
                            null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Order on KhabarLagbe",
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareOption(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}
