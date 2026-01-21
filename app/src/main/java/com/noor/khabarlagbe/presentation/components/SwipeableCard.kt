package com.noor.khabarlagbe.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.noor.khabarlagbe.ui.theme.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

enum class SwipeAction {
    FAVORITE,
    DELETE,
    NONE
}

private const val SWIPE_ROTATION_DEGREES = 5f

@Composable
fun SwipeableCard(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    modifier: Modifier = Modifier,
    swipeThreshold: Float = 0.4f,
    leftAction: SwipeAction = SwipeAction.DELETE,
    rightAction: SwipeAction = SwipeAction.FAVORITE,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var cardWidth by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isDragging) offsetX else 0f,
        label = "swipe_offset"
    )

    val swipeProgress = if (cardWidth > 0) (animatedOffsetX / cardWidth).coerceIn(-1f, 1f) else 0f

    val leftBackgroundColor by animateColorAsState(
        targetValue = when {
            swipeProgress < -swipeThreshold -> Error
            swipeProgress < 0 -> Error.copy(alpha = (-swipeProgress / swipeThreshold).coerceIn(0f, 1f))
            else -> Color.Transparent
        },
        label = "left_bg"
    )

    val rightBackgroundColor by animateColorAsState(
        targetValue = when {
            swipeProgress > swipeThreshold -> Primary
            swipeProgress > 0 -> Primary.copy(alpha = (swipeProgress / swipeThreshold).coerceIn(0f, 1f))
            else -> Color.Transparent
        },
        label = "right_bg"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Background actions
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(if (swipeProgress < 0) leftBackgroundColor else rightBackgroundColor),
            horizontalArrangement = if (swipeProgress < 0) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = if (swipeProgress < 0) {
                when (leftAction) {
                    SwipeAction.DELETE -> Icons.Filled.Delete
                    SwipeAction.FAVORITE -> Icons.Filled.Favorite
                    SwipeAction.NONE -> null
                }
            } else {
                when (rightAction) {
                    SwipeAction.DELETE -> Icons.Filled.Delete
                    SwipeAction.FAVORITE -> Icons.Filled.Favorite
                    SwipeAction.NONE -> null
                }
            }

            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .scale(swipeProgress.absoluteValue.coerceIn(0.5f, 1.5f))
                )
            }
        }

        // Foreground card
        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            when {
                                swipeProgress < -swipeThreshold -> onSwipeLeft()
                                swipeProgress > swipeThreshold -> onSwipeRight()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            isDragging = false
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX += dragAmount
                            cardWidth = size.width.toFloat()
                        }
                    )
                }
                .graphicsLayer {
                    rotationZ = swipeProgress * SWIPE_ROTATION_DEGREES
                }
        ) {
            content()
        }
    }
}
