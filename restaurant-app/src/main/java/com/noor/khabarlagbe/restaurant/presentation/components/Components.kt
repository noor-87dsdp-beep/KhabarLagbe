package com.noor.khabarlagbe.restaurant.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.noor.khabarlagbe.restaurant.domain.model.*

@Composable
fun OrderCard(
    order: Order,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onMarkPreparing: () -> Unit,
    onMarkReady: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (order.status) {
                OrderStatusEnum.PENDING -> Color(0xFFE3F2FD)
                OrderStatusEnum.ACCEPTED, OrderStatusEnum.PREPARING -> Color(0xFFFFF3E0)
                OrderStatusEnum.READY -> Color(0xFFE8F5E9)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "#${order.orderNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.customerName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                StatusChip(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
            
            order.items.take(3).forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "• ${item.name} x${item.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "৳${item.totalPrice.toInt()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (order.items.size > 3) {
                Text(
                    text = "+${order.items.size - 3} more items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মোট: ৳${order.total.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = order.paymentMethod,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OrderActionButtons(
                status = order.status,
                onAccept = onAccept,
                onReject = onReject,
                onMarkPreparing = onMarkPreparing,
                onMarkReady = onMarkReady
            )
        }
    }
}

@Composable
fun OrderActionButtons(
    status: OrderStatusEnum,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onMarkPreparing: () -> Unit,
    onMarkReady: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (status) {
            OrderStatusEnum.PENDING -> {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("বাতিল")
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("গ্রহণ করুন")
                }
            }
            OrderStatusEnum.ACCEPTED -> {
                Button(
                    onClick = onMarkPreparing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("রান্না শুরু করুন")
                }
            }
            OrderStatusEnum.PREPARING -> {
                Button(
                    onClick = onMarkReady,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("প্রস্তুত")
                }
            }
            OrderStatusEnum.READY -> {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    enabled = false
                ) {
                    Icon(Icons.Default.DeliveryDining, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("রাইডার অপেক্ষারত")
                }
            }
            else -> {}
        }
    }
}

@Composable
fun StatusChip(status: OrderStatusEnum) {
    val (backgroundColor, textColor, text) = when (status) {
        OrderStatusEnum.PENDING -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), "নতুন")
        OrderStatusEnum.ACCEPTED -> Triple(Color(0xFFFFF3E0), Color(0xFFF57C00), "গৃহীত")
        OrderStatusEnum.PREPARING -> Triple(Color(0xFFFFF3E0), Color(0xFFF57C00), "প্রস্তুত হচ্ছে")
        OrderStatusEnum.READY -> Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), "প্রস্তুত")
        OrderStatusEnum.PICKED_UP -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), "পিকআপ")
        OrderStatusEnum.DELIVERED -> Triple(Color(0xFFE8F5E9), Color(0xFF388E3C), "সম্পন্ন")
        OrderStatusEnum.CANCELLED -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), "বাতিল")
        OrderStatusEnum.REJECTED -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), "প্রত্যাখ্যাত")
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    onToggleAvailability: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.imageUrl != null) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                if (item.nameEn != null) {
                    Text(
                        text = item.nameEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "৳${item.price.toInt()}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (item.discountedPrice != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "৳${item.discountedPrice.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    DietaryFlags(item)
                }
            }
            
            Switch(
                checked = item.isAvailable,
                onCheckedChange = { onToggleAvailability() }
            )
        }
    }
}

@Composable
fun DietaryFlags(item: MenuItem) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (item.isVegetarian) {
            DietaryBadge(text = "V", color = Color(0xFF4CAF50))
        }
        if (item.isVegan) {
            DietaryBadge(text = "VG", color = Color(0xFF8BC34A))
        }
        if (item.isSpicy) {
            DietaryBadge(text = "🌶", color = Color(0xFFFF5722))
        }
    }
}

@Composable
fun DietaryBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), CircleShape)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun CategoryCard(
    category: MenuCategory,
    isExpanded: Boolean,
    itemCount: Int,
    onToggleExpand: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$itemCount items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand"
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onRespond: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.customerName.firstOrNull()?.toString() ?: "?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = review.customerName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = review.createdAt.take(10),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                RatingStars(rating = review.rating)
            }
            
            if (!review.comment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = review.comment,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (review.response != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "আপনার উত্তর:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = review.response.text,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onRespond) {
                    Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("উত্তর দিন")
                }
            }
        }
    }
}

@Composable
fun RatingStars(rating: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
