package com.noor.khabarlagbe.presentation.rewards

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.navigation.Screen
import com.noor.khabarlagbe.presentation.components.ConfettiAnimation
import com.noor.khabarlagbe.presentation.components.ShimmerEffect
import com.noor.khabarlagbe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    navController: NavController,
    viewModel: RewardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is RewardsUiState.Success && (uiState as RewardsUiState.Success).redeemSuccess) {
            showConfetti = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Rewards", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.PointsHistory.route) }) {
                            Icon(Icons.Filled.History, "Points History")
                        }
                    }
                )
            }
        ) { padding ->
            when (val state = uiState) {
                is RewardsUiState.Loading -> RewardsLoadingState(Modifier.padding(padding))
                is RewardsUiState.Success -> RewardsContent(
                    loyaltyProfile = state.loyaltyProfile,
                    onRedeemClick = { viewModel.redeemReward(it) },
                    onHistoryClick = { navController.navigate(Screen.PointsHistory.route) },
                    modifier = Modifier.padding(padding)
                )
                is RewardsUiState.Error -> ErrorContent(state.message) { viewModel.loadRewardsData() }
            }
        }

        // Confetti overlay
        ConfettiAnimation(
            isPlaying = showConfetti,
            onAnimationEnd = {
                showConfetti = false
                viewModel.clearRedeemSuccess()
            }
        )
    }
}

@Composable
private fun RewardsContent(
    loyaltyProfile: LoyaltyProfile,
    onRedeemClick: (String) -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tier Card
        item {
            TierStatusCard(loyaltyProfile)
        }

        // Points Balance
        item {
            PointsBalanceCard(
                currentPoints = loyaltyProfile.currentPoints,
                streakDays = loyaltyProfile.streakDays,
                onHistoryClick = onHistoryClick
            )
        }

        // Available Rewards Header
        item {
            Text(
                text = "Available Rewards",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Rewards Grid
        items(loyaltyProfile.availableRewards.chunked(2)) { rowRewards ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowRewards.forEach { reward ->
                    RewardCard(
                        reward = reward,
                        currentPoints = loyaltyProfile.currentPoints,
                        onRedeemClick = { onRedeemClick(reward.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowRewards.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TierStatusCard(loyaltyProfile: LoyaltyProfile) {
    val tierColor = when (loyaltyProfile.tier) {
        LoyaltyTier.BRONZE -> Color(0xFFCD7F32)
        LoyaltyTier.SILVER -> Color(0xFFC0C0C0)
        LoyaltyTier.GOLD -> Color(0xFFFFD700)
        LoyaltyTier.PLATINUM -> Color(0xFFE5E4E2)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(tierColor.copy(alpha = 0.8f), tierColor.copy(alpha = 0.4f))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = loyaltyProfile.tier.displayName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Member",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progress to next tier
                if (loyaltyProfile.tier != LoyaltyTier.PLATINUM) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = loyaltyProfile.tier.displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                            Text(
                                text = LoyaltyTier.entries[loyaltyProfile.tier.ordinal + 1].displayName,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { loyaltyProfile.tierProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${loyaltyProfile.pointsToNextTier} points to ${LoyaltyTier.entries[loyaltyProfile.tier.ordinal + 1].displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tier benefits
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TierBenefit(
                            icon = Icons.Filled.Percent,
                            text = "${((loyaltyProfile.tier.multiplier - 1) * 100).toInt()}% bonus points"
                        )
                        TierBenefit(
                            icon = Icons.Filled.LocalShipping,
                            text = if (loyaltyProfile.tier >= LoyaltyTier.GOLD) "Free delivery" else "Std. delivery"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TierBenefit(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = Color.White)
    }
}

@Composable
private fun PointsBalanceCard(
    currentPoints: Int,
    streakDays: Int,
    onHistoryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Available Points",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$currentPoints",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = " pts",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                
                // Streak badge
                Surface(
                    color = Warning.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.LocalFireDepartment, null, tint = Warning, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streakDays day streak",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Warning
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EarningMethod(
                    icon = Icons.Filled.ShoppingBag,
                    title = "Order",
                    subtitle = "1 pt / ৳10",
                    modifier = Modifier.weight(1f)
                )
                EarningMethod(
                    icon = Icons.Filled.RateReview,
                    title = "Review",
                    subtitle = "+25 pts",
                    modifier = Modifier.weight(1f)
                )
                EarningMethod(
                    icon = Icons.Filled.Share,
                    title = "Refer",
                    subtitle = "+100 pts",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onHistoryClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("View History", color = Primary)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Filled.ArrowForward, null, tint = Primary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun EarningMethod(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = SurfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
private fun RewardsLoadingState(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(20.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Error, null, tint = Error, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}
