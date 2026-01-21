package com.noor.khabarlagbe.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.noor.khabarlagbe.ui.theme.*

data class FabMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun FloatingActionMenu(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    items: List<FabMenuItem>,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fab_rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Menu items
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items.forEachIndexed { index, item ->
                    val itemDelay = (items.size - index) * 50
                    var itemVisible by remember { mutableStateOf(false) }
                    
                    LaunchedEffect(isExpanded) {
                        if (isExpanded) {
                            kotlinx.coroutines.delay(itemDelay.toLong())
                            itemVisible = true
                        } else {
                            itemVisible = false
                        }
                    }

                    AnimatedVisibility(
                        visible = itemVisible,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 4.dp
                            ) {
                                Text(
                                    text = item.label,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            SmallFloatingActionButton(
                                onClick = {
                                    item.onClick()
                                    onToggle()
                                },
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Icon(item.icon, contentDescription = item.label)
                            }
                        }
                    }
                }
            }
        }

        // Main FAB
        FloatingActionButton(
            onClick = onToggle,
            containerColor = Primary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = if (isExpanded) "Close menu" else "Open menu",
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
fun QuickActionsMenu(
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onWalletClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    FloatingActionMenu(
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        items = listOf(
            FabMenuItem(Icons.Filled.Search, "Search", onSearchClick),
            FabMenuItem(Icons.Filled.Favorite, "Favorites", onFavoritesClick),
            FabMenuItem(Icons.Filled.History, "Orders", onOrdersClick),
            FabMenuItem(Icons.Filled.AccountBalanceWallet, "Wallet", onWalletClick)
        ),
        modifier = modifier
    )
}
