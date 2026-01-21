package com.noor.khabarlagbe.presentation.ai

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.noor.khabarlagbe.ui.theme.*

enum class VoiceState {
    IDLE, LISTENING, PROCESSING, RESULT, ERROR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceOrderScreen(navController: NavController) {
    var voiceState by remember { mutableStateOf(VoiceState.IDLE) }
    var transcript by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Text(
                text = when (voiceState) {
                    VoiceState.IDLE -> "Tap to start ordering"
                    VoiceState.LISTENING -> "I'm listening..."
                    VoiceState.PROCESSING -> "Processing..."
                    VoiceState.RESULT -> "Here's what I found"
                    VoiceState.ERROR -> "Please try again"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (voiceState) {
                    VoiceState.IDLE -> "Say something like \"Order chicken biryani from Star Kabab\""
                    VoiceState.LISTENING -> "Speak clearly..."
                    VoiceState.PROCESSING -> "Understanding your request"
                    VoiceState.RESULT -> transcript.ifEmpty { "No results" }
                    VoiceState.ERROR -> "Couldn't understand that"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Voice button
            VoiceButton(
                state = voiceState,
                onClick = {
                    voiceState = when (voiceState) {
                        VoiceState.IDLE -> VoiceState.LISTENING
                        VoiceState.LISTENING -> {
                            transcript = "Chicken Biryani from Star Kabab"
                            suggestions = listOf(
                                "Chicken Biryani - ৳320",
                                "Mutton Biryani - ৳450",
                                "Vegetable Biryani - ৳220"
                            )
                            VoiceState.RESULT
                        }
                        else -> VoiceState.IDLE
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Quick commands
            QuickCommandsSection(
                commands = listOf(
                    "\"Order my usual\"",
                    "\"What's popular nearby?\"",
                    "\"Reorder last meal\"",
                    "\"Find healthy options\""
                ),
                onCommandClick = { command ->
                    transcript = command.trim('"')
                    voiceState = VoiceState.RESULT
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Result suggestions
            if (voiceState == VoiceState.RESULT && suggestions.isNotEmpty()) {
                SuggestionsCard(suggestions = suggestions)
            }
        }
    }
}

@Composable
private fun VoiceButton(
    state: VoiceState,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (state == VoiceState.LISTENING) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val outerRingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (state == VoiceState.LISTENING) 0f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring"
    )

    Box(contentAlignment = Alignment.Center) {
        // Outer ring animation
        if (state == VoiceState.LISTENING) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale * 1.2f)
                    .background(
                        Primary.copy(alpha = outerRingAlpha),
                        CircleShape
                    )
            )
        }

        // Main button
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(120.dp)
                .scale(scale),
            shape = CircleShape,
            color = when (state) {
                VoiceState.LISTENING -> Error
                VoiceState.PROCESSING -> Warning
                else -> Primary
            },
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when (state) {
                        VoiceState.LISTENING -> Icons.Filled.Stop
                        VoiceState.PROCESSING -> Icons.Filled.HourglassTop
                        else -> Icons.Filled.Mic
                    },
                    contentDescription = "Voice",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickCommandsSection(
    commands: List<String>,
    onCommandClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Try saying:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        commands.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { command ->
                    Surface(
                        onClick = { onCommandClick(command) },
                        modifier = Modifier.weight(1f),
                        color = SurfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = command,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SuggestionsCard(suggestions: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Did you mean:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            suggestions.forEach { suggestion ->
                Surface(
                    onClick = { /* Select suggestion */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = Primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Restaurant,
                            null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(suggestion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
