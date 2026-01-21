package com.noor.khabarlagbe.presentation.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsFeedScreen(navController: NavController) {
    val activities = remember { getSampleActivities() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Friends Activity", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Find friends */ }) {
                        Icon(Icons.Filled.PersonAdd, "Add Friends")
                    }
                }
            )
        }
    ) { padding ->
        if (activities.isEmpty()) {
            EmptyFeedState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activities) { activity ->
                    FriendActivityCard(
                        activity = activity,
                        onRestaurantClick = { /* Navigate to restaurant */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendActivityCard(
    activity: FriendActivity,
    onRestaurantClick: () -> Unit
) {
    val timeFormat = remember { SimpleDateFormat("MMM dd 'at' hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with avatar and user info
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Primary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (activity.userImageUrl != null) {
                        AsyncImage(
                            model = activity.userImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = activity.userName.first().toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.userName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = timeFormat.format(Date(activity.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                
                // Activity type icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(getActivityColor(activity.activityType).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        getActivityIcon(activity.activityType),
                        null,
                        tint = getActivityColor(activity.activityType),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Activity content
            ActivityContent(activity, onRestaurantClick)

            // Rating stars if applicable
            if (activity.activityType == FriendActivityType.REVIEWED && activity.rating != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    repeat(5) { index ->
                        Icon(
                            if (index < activity.rating!!) Icons.Filled.Star else Icons.Filled.StarBorder,
                            null,
                            tint = Rating,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityContent(activity: FriendActivity, onRestaurantClick: () -> Unit) {
    val text = when (activity.activityType) {
        FriendActivityType.ORDERED_FROM -> "Ordered ${activity.itemName ?: ""} from"
        FriendActivityType.REVIEWED -> "Left a review for"
        FriendActivityType.FAVORITED -> "Added to favorites"
        FriendActivityType.EARNED_REWARD -> "Earned a reward:"
        FriendActivityType.REACHED_TIER -> "Reached"
    }

    val highlight = when (activity.activityType) {
        FriendActivityType.ORDERED_FROM, FriendActivityType.REVIEWED, FriendActivityType.FAVORITED ->
            activity.restaurantName ?: ""
        FriendActivityType.EARNED_REWARD -> activity.itemName ?: "New Reward"
        FriendActivityType.REACHED_TIER -> "${activity.itemName} Status!"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$text ",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        
        if (activity.restaurantName != null || activity.activityType == FriendActivityType.EARNED_REWARD || activity.activityType == FriendActivityType.REACHED_TIER) {
            TextButton(
                onClick = onRestaurantClick,
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
            ) {
                Text(
                    text = highlight,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }
    }
}

@Composable
private fun EmptyFeedState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.People,
            null,
            tint = TextDisabled,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No activity yet",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )
        Text(
            text = "Connect with friends to see their orders",
            style = MaterialTheme.typography.bodyMedium,
            color = TextDisabled
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { /* Find friends */ }) {
            Icon(Icons.Filled.PersonAdd, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Find Friends")
        }
    }
}

private fun getActivityIcon(type: FriendActivityType): ImageVector = when (type) {
    FriendActivityType.ORDERED_FROM -> Icons.Filled.ShoppingBag
    FriendActivityType.REVIEWED -> Icons.Filled.RateReview
    FriendActivityType.FAVORITED -> Icons.Filled.Favorite
    FriendActivityType.EARNED_REWARD -> Icons.Filled.CardGiftcard
    FriendActivityType.REACHED_TIER -> Icons.Filled.EmojiEvents
}

private fun getActivityColor(type: FriendActivityType): Color = when (type) {
    FriendActivityType.ORDERED_FROM -> Primary
    FriendActivityType.REVIEWED -> Warning
    FriendActivityType.FAVORITED -> Error
    FriendActivityType.EARNED_REWARD -> Success
    FriendActivityType.REACHED_TIER -> DeliveryBadge
}

private fun getSampleActivities(): List<FriendActivity> = listOf(
    FriendActivity("a1", "user_2", "Ahmed Khan", null, FriendActivityType.ORDERED_FROM, "rest_1", "Star Kabab", "Beef Tehari", null, System.currentTimeMillis() - 3600000),
    FriendActivity("a2", "user_3", "Sara Begum", null, FriendActivityType.REVIEWED, "rest_2", "Pizza Hut", null, 5, System.currentTimeMillis() - 7200000),
    FriendActivity("a3", "user_4", "Rahim Uddin", null, FriendActivityType.FAVORITED, "rest_3", "Madchef", null, null, System.currentTimeMillis() - 14400000),
    FriendActivity("a4", "user_5", "Fatima Akter", null, FriendActivityType.EARNED_REWARD, null, null, "Free Delivery", null, System.currentTimeMillis() - 28800000),
    FriendActivity("a5", "user_6", "Karim Hassan", null, FriendActivityType.REACHED_TIER, null, null, "Gold", null, System.currentTimeMillis() - 43200000)
)
