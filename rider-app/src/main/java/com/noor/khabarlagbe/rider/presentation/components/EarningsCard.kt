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
import com.noor.khabarlagbe.rider.domain.model.EarningsSummary

@Composable
fun EarningsCard(
    summary: EarningsSummary,
    title: String,
    modifier: Modifier = Modifier,
    onWithdraw: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                if (onWithdraw != null) {
                    TextButton(
                        onClick = onWithdraw,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("উত্তোলন")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "৳${String.format("%,.0f", summary.totalEarnings)}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EarningsStatItem(
                    label = "ডেলিভারি",
                    value = "${summary.deliveriesCount}টি",
                    icon = Icons.Default.LocalShipping
                )
                EarningsStatItem(
                    label = "ডেলিভারি ফি",
                    value = "৳${summary.deliveryFees.toInt()}",
                    icon = Icons.Default.AttachMoney
                )
                EarningsStatItem(
                    label = "টিপস",
                    value = "৳${summary.tips.toInt()}",
                    icon = Icons.Default.Favorite
                )
            }
        }
    }
}

@Composable
private fun EarningsStatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun EarningsBreakdownCard(
    summary: EarningsSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "আয়ের বিবরণ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            EarningsBreakdownRow(
                label = "ডেলিভারি ফি",
                value = "৳${String.format("%,.0f", summary.deliveryFees)}",
                icon = Icons.Default.LocalShipping
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            EarningsBreakdownRow(
                label = "টিপস",
                value = "৳${String.format("%,.0f", summary.tips)}",
                icon = Icons.Default.Favorite
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            EarningsBreakdownRow(
                label = "ইনসেনটিভ/বোনাস",
                value = "৳${String.format("%,.0f", summary.incentives)}",
                icon = Icons.Default.CardGiftcard
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মোট",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "৳${String.format("%,.0f", summary.totalEarnings)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun EarningsBreakdownRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
