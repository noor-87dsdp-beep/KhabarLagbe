package com.noor.khabarlagbe.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.noor.khabarlagbe.ui.theme.*

@Composable
fun RatingDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, review: String) -> Unit,
    restaurantName: String,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        var rating by remember { mutableIntStateOf(0) }
        var review by remember { mutableStateOf("") }

        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Rate Your Order",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "How was your experience with $restaurantName?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Animated star rating
                    AnimatedStarRating(
                        rating = rating,
                        onRatingChange = { rating = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rating label
                    Text(
                        text = when (rating) {
                            1 -> "Poor"
                            2 -> "Fair"
                            3 -> "Good"
                            4 -> "Very Good"
                            5 -> "Excellent!"
                            else -> "Tap to rate"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (rating > 0) Primary else TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Review text field
                    OutlinedTextField(
                        value = review,
                        onValueChange = { review = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Write a review (optional)") },
                        placeholder = { Text("Share your experience...") },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Maybe Later")
                        }
                        
                        Button(
                            onClick = { onSubmit(rating, review) },
                            enabled = rating > 0,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text("Submit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedStarRating(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    maxRating: Int = 5,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..maxRating) {
            AnimatedStar(
                isSelected = i <= rating,
                onClick = { onRatingChange(i) },
                delay = i * 50
            )
        }
    }
}

@Composable
private fun AnimatedStar(
    isSelected: Boolean,
    onClick: () -> Unit,
    delay: Int = 0
) {
    var hasAnimated by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected && !hasAnimated) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "star_scale",
        finishedListener = { hasAnimated = true }
    )

    LaunchedEffect(isSelected) {
        if (isSelected) {
            hasAnimated = false
        }
    }

    IconButton(
        onClick = onClick,
        modifier = Modifier.scale(scale)
    ) {
        Icon(
            imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.StarOutline,
            contentDescription = null,
            tint = if (isSelected) Rating else TextDisabled,
            modifier = Modifier.size(40.dp)
        )
    }
}
