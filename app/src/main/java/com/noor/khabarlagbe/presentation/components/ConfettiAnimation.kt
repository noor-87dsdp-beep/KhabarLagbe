package com.noor.khabarlagbe.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    val color: Color,
    val size: Float,
    var velocityX: Float,
    var velocityY: Float,
    var rotation: Float,
    val rotationSpeed: Float
)

private const val DEFAULT_CONFETTI_DURATION_MS = 3000L

@Composable
fun ConfettiAnimation(
    isPlaying: Boolean,
    particleCount: Int = 100,
    durationMs: Long = DEFAULT_CONFETTI_DURATION_MS,
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    val confettiColors = listOf(
        Color(0xFFFF6B6B),
        Color(0xFFFFAA4C),
        Color(0xFFFFD93D),
        Color(0xFF6BCB77),
        Color(0xFF4D96FF),
        Color(0xFFFF6BCB),
        Color(0xFF9C27B0)
    )

    var particles by remember { mutableStateOf<List<ConfettiParticle>>(emptyList()) }
    var animationProgress by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(16, easing = LinearEasing)
        ),
        label = "confetti_time"
    )

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            particles = List(particleCount) {
                ConfettiParticle(
                    x = Random.nextFloat(),
                    y = Random.nextFloat() * -0.5f,
                    color = confettiColors.random(),
                    size = Random.nextFloat() * 10f + 5f,
                    velocityX = (Random.nextFloat() - 0.5f) * 0.02f,
                    velocityY = Random.nextFloat() * 0.015f + 0.005f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 10f
                )
            }
            animationProgress = 0f
            
            kotlinx.coroutines.delay(durationMs)
            onAnimationEnd()
        }
    }

    LaunchedEffect(time, isPlaying) {
        if (isPlaying && particles.isNotEmpty()) {
            particles = particles.map { particle ->
                particle.copy(
                    x = particle.x + particle.velocityX,
                    y = particle.y + particle.velocityY,
                    rotation = particle.rotation + particle.rotationSpeed,
                    velocityY = particle.velocityY + 0.0005f
                )
            }.filter { it.y < 1.5f }
        }
    }

    if (isPlaying) {
        Canvas(modifier = modifier.fillMaxSize()) {
            particles.forEach { particle ->
                rotate(particle.rotation, pivot = Offset(size.width * particle.x, size.height * particle.y)) {
                    drawRect(
                        color = particle.color,
                        topLeft = Offset(
                            size.width * particle.x - particle.size / 2,
                            size.height * particle.y - particle.size / 2
                        ),
                        size = androidx.compose.ui.geometry.Size(particle.size, particle.size * 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun SuccessConfetti(
    showConfetti: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConfettiAnimation(
        isPlaying = showConfetti,
        particleCount = 150,
        modifier = modifier,
        onAnimationEnd = onDismiss
    )
}
