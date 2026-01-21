package com.noor.khabarlagbe.presentation.rewards

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.noor.khabarlagbe.domain.model.Reward
import com.noor.khabarlagbe.domain.model.RewardCategory
import com.noor.khabarlagbe.ui.theme.*

@Composable
fun RewardCard(
    reward: Reward,
    currentPoints: Int,
    onRedeemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canRedeem = currentPoints >= reward.pointsCost && !reward.isRedeemed
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .heightIn(min = 180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reward.isRedeemed) SurfaceVariant else SurfaceLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = getCategoryColor(reward.category).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(reward.category),
                    contentDescription = null,
                    tint = getCategoryColor(reward.category),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            // Points cost
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Stars,
                    contentDescription = null,
                    tint = if (canRedeem) Primary else TextDisabled,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${reward.pointsCost} pts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (canRedeem) Primary else TextDisabled
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Redeem button
            Button(
                onClick = {
                    isPressed = true
                    onRedeemClick()
                },
                enabled = canRedeem,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = SurfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when {
                        reward.isRedeemed -> "Redeemed"
                        canRedeem -> "Redeem"
                        else -> "Need ${reward.pointsCost - currentPoints} more"
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(200)
            isPressed = false
        }
    }
}

private fun getCategoryIcon(category: RewardCategory): ImageVector = when (category) {
    RewardCategory.FREE_DELIVERY -> Icons.Filled.LocalShipping
    RewardCategory.DISCOUNT_PERCENTAGE -> Icons.Filled.Percent
    RewardCategory.DISCOUNT_FLAT -> Icons.Filled.MonetizationOn
    RewardCategory.FREE_ITEM -> Icons.Filled.Fastfood
    RewardCategory.CASHBACK -> Icons.Filled.Savings
    RewardCategory.EXCLUSIVE_ACCESS -> Icons.Filled.Star
}

private fun getCategoryColor(category: RewardCategory): Color = when (category) {
    RewardCategory.FREE_DELIVERY -> Info
    RewardCategory.DISCOUNT_PERCENTAGE -> Primary
    RewardCategory.DISCOUNT_FLAT -> Warning
    RewardCategory.FREE_ITEM -> Success
    RewardCategory.CASHBACK -> Secondary
    RewardCategory.EXCLUSIVE_ACCESS -> DeliveryBadge
}

@Composable
fun RewardRedemptionDialog(
    reward: Reward,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible && reward.redemptionCode != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        null,
                        tint = Success,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Reward Redeemed!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(reward.title, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = SurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = reward.redemptionCode!!,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Use this code at checkout",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                Button(onClick = onDismiss) { Text("Got it!") }
            },
            modifier = modifier
        )
    }
}
