package com.noor.khabarlagbe.rider.presentation.stats

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.noor.khabarlagbe.rider.data.dto.LeaderboardEntryDto
import com.noor.khabarlagbe.rider.presentation.components.RatingStatCard
import com.noor.khabarlagbe.rider.presentation.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavController,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("পারফরম্যান্স") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            is StatsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is StatsUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.ErrorOutline, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text((uiState as StatsUiState.Error).message)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadStats() }) { Text("আবার চেষ্টা করুন") }
                }
            }
            
            is StatsUiState.Success -> {
                val successState = uiState as StatsUiState.Success
                val stats = successState.stats
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Period filter
                    item {
                        PeriodFilterRow(
                            selectedPeriod = selectedPeriod,
                            onPeriodSelected = { viewModel.selectPeriod(it) }
                        )
                    }
                    
                    // Main stats grid
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "ডেলিভারি",
                                value = "${stats.completedDeliveries}",
                                icon = Icons.Default.LocalShipping,
                                iconTint = Color(0xFF4CAF50),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "আয়",
                                value = "৳${stats.totalEarnings.toInt()}",
                                icon = Icons.Default.AttachMoney,
                                iconTint = Color(0xFF2196F3),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "দূরত্ব",
                                value = "${String.format("%.1f", stats.totalDistance)} কিমি",
                                icon = Icons.Default.Route,
                                iconTint = Color(0xFFFF5722),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "গড় সময়",
                                value = "${stats.averageDeliveryTime} মিনিট",
                                icon = Icons.Default.Timer,
                                iconTint = Color(0xFF9C27B0),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    // Rating card
                    item {
                        RatingStatCard(
                            rating = stats.averageRating,
                            totalRatings = stats.totalRatings,
                            fiveStarPercentage = if (stats.totalRatings > 0) 
                                ((stats.fiveStarRatings.toDouble() / stats.totalRatings) * 100).toInt() 
                            else 0
                        )
                    }
                    
                    // Performance rates
                    item {
                        PerformanceRatesCard(
                            acceptanceRate = stats.acceptanceRate,
                            completionRate = stats.completionRate,
                            onTimeRate = stats.onTimeRate
                        )
                    }
                    
                    // Leaderboard position
                    if (successState.myPosition > 0) {
                        item {
                            LeaderboardPositionCard(
                                position = successState.myPosition,
                                period = selectedPeriod
                            )
                        }
                    }
                    
                    // Leaderboard
                    if (successState.leaderboard.isNotEmpty()) {
                        item {
                            Text(
                                text = "লিডারবোর্ড",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        items(successState.leaderboard) { entry ->
                            LeaderboardItem(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodFilterRow(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("today" to "আজ", "week" to "এই সপ্তাহ", "month" to "এই মাস").forEach { (period, label) ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun PerformanceRatesCard(
    acceptanceRate: Double,
    completionRate: Double,
    onTimeRate: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "পারফরম্যান্স রেট",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PerformanceRateRow(
                label = "অ্যাক্সেপ্ট রেট",
                rate = acceptanceRate,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceRateRow(
                label = "কমপ্লিশন রেট",
                rate = completionRate,
                color = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceRateRow(
                label = "অন-টাইম রেট",
                rate = onTimeRate,
                color = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
private fun PerformanceRateRow(
    label: String,
    rate: Double,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "${String.format("%.1f", rate)}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (rate / 100).toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f),
        )
    }
}

@Composable
private fun LeaderboardPositionCard(
    position: Int,
    period: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFD700).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFD700)
            ) {
                Icon(
                    Icons.Default.EmojiEvents, null,
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "লিডারবোর্ড পজিশন",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "#$position",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

@Composable
private fun LeaderboardItem(entry: LeaderboardEntryDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position badge
            Surface(
                shape = CircleShape,
                color = when (entry.position) {
                    1 -> Color(0xFFFFD700)
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${entry.position}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (entry.position <= 3) Color.White 
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Profile image
            if (entry.profileImageUrl != null) {
                AsyncImage(
                    model = entry.profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Person, null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.riderName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star, null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", entry.rating),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = "${entry.deliveries} ডেলিভারি",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
