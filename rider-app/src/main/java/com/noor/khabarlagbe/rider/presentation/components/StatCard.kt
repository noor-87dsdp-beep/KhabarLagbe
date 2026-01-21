package com.noor.khabarlagbe.rider.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    trend: StatTrend? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconTint.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(12.dp).size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (trend != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TrendBadge(trend = trend)
            }
            
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TrendBadge(trend: StatTrend) {
    val (icon, color, text) = when (trend) {
        is StatTrend.Up -> Triple(
            Icons.Default.TrendingUp,
            Color(0xFF4CAF50),
            "+${trend.percentage}%"
        )
        is StatTrend.Down -> Triple(
            Icons.Default.TrendingDown,
            Color(0xFFE53935),
            "-${trend.percentage}%"
        )
        StatTrend.Stable -> Triple(
            Icons.Default.TrendingFlat,
            Color.Gray,
            "0%"
        )
    }
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

sealed class StatTrend {
    data class Up(val percentage: Int) : StatTrend()
    data class Down(val percentage: Int) : StatTrend()
    object Stable : StatTrend()
}

@Composable
fun LargeStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = iconTint.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.padding(16.dp).size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun RatingStatCard(
    rating: Double,
    totalRatings: Int,
    fiveStarPercentage: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "রেটিং",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format("%.1f", rating),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < rating.toInt()) Icons.Default.Star 
                                             else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "$totalRatings রেটিং",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "৫ স্টার রেটিং",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$fiveStarPercentage%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = { fiveStarPercentage / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
            )
        }
    }
}
