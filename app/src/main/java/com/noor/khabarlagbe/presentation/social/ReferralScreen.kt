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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.noor.khabarlagbe.domain.model.*
import com.noor.khabarlagbe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralScreen(navController: NavController) {
    val referralCode = "KHABAR2024"
    val referrals = remember { getSampleReferrals() }
    val totalEarned = referrals.filter { it.status == ReferralStatus.COMPLETED }.sumOf { it.rewardPoints }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invite Friends", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card
            item {
                ReferralHeroCard(
                    totalEarned = totalEarned,
                    referralCode = referralCode,
                    onShareClick = { /* Share referral code */ }
                )
            }

            // How it works
            item {
                ReferralStepsCard()
            }

            // Referral history
            if (referrals.isNotEmpty()) {
                item {
                    Text(
                        text = "Your Referrals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(referrals) { referral ->
                    ReferralHistoryItem(referral)
                }
            }
        }
    }
}

@Composable
private fun ReferralHeroCard(
    totalEarned: Int,
    referralCode: String,
    onShareClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, Info)
                    )
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.CardGiftcard,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Give ৳100, Get ৳100",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Invite friends to KhabarLagbe. When they order, you both get rewards!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ReferralStat(value = "5", label = "Friends Invited")
                    ReferralStat(value = "$totalEarned", label = "Points Earned")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Referral code
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Referral Code",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = referralCode,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 4.sp
                            )
                        }
                        
                        IconButton(
                            onClick = { /* Copy to clipboard */ },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Filled.ContentCopy, null, tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onShareClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Share, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share with Friends", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReferralStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ReferralStepsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "How it works",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ReferralStep(
                icon = Icons.Filled.Share,
                title = "Share your code",
                description = "Send your referral code to friends"
            )
            
            ReferralStep(
                icon = Icons.Filled.PersonAdd,
                title = "Friend signs up",
                description = "They create an account with your code"
            )
            
            ReferralStep(
                icon = Icons.Filled.ShoppingBag,
                title = "First order",
                description = "Your friend places their first order"
            )
            
            ReferralStep(
                icon = Icons.Filled.Celebration,
                title = "Both earn rewards!",
                description = "You each get 100 points instantly",
                isLast = true
            )
        }
    }
}

@Composable
private fun ReferralStep(
    icon: ImageVector,
    title: String,
    description: String,
    isLast: Boolean = false
) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Primary, modifier = Modifier.size(20.dp))
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(Primary.copy(alpha = 0.3f))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Medium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun ReferralHistoryItem(referral: Referral) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = referral.referredUserName?.firstOrNull()?.toString() ?: "?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = referral.referredUserName ?: "Pending",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                ReferralStatusText(referral.status)
            }
            
            if (referral.status == ReferralStatus.COMPLETED) {
                Surface(
                    color = Success.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "+${referral.rewardPoints} pts",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Success,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ReferralStatusText(status: ReferralStatus) {
    val (color, text) = when (status) {
        ReferralStatus.PENDING -> TextSecondary to "Invite sent"
        ReferralStatus.SIGNED_UP -> Info to "Signed up - waiting for first order"
        ReferralStatus.FIRST_ORDER -> Warning to "Processing reward..."
        ReferralStatus.COMPLETED -> Success to "Reward earned!"
        ReferralStatus.EXPIRED -> Error to "Expired"
    }
    
    Text(text = text, style = MaterialTheme.typography.bodySmall, color = color)
}

private fun getSampleReferrals(): List<Referral> = listOf(
    Referral("r1", "user_1", "KHABAR2024", "user_5", "Ahmed Khan", ReferralStatus.COMPLETED, 100),
    Referral("r2", "user_1", "KHABAR2024", "user_6", "Sara Begum", ReferralStatus.COMPLETED, 100),
    Referral("r3", "user_1", "KHABAR2024", "user_7", "Rahim Uddin", ReferralStatus.SIGNED_UP, 100),
    Referral("r4", "user_1", "KHABAR2024", null, null, ReferralStatus.PENDING, 100)
)
