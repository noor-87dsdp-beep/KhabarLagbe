package com.noor.khabarlagbe.presentation.support

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.noor.khabarlagbe.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    navController: NavController,
    orderId: String? = null,
    viewModel: SupportViewModel = hiltViewModel()
) {
    var selectedIssueType by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val issueTypes = listOf(
        "Order not received" to Icons.Filled.LocalShipping,
        "Wrong items delivered" to Icons.Filled.SwapHoriz,
        "Missing items" to Icons.Filled.RemoveCircle,
        "Food quality issue" to Icons.Filled.ThumbDown,
        "Late delivery" to Icons.Filled.Schedule,
        "Payment issue" to Icons.Filled.CreditCard,
        "Other" to Icons.Filled.MoreHoriz
    )

    if (showSuccess) {
        IssueSubmittedDialog(
            onDismiss = {
                showSuccess = false
                navController.popBackStack()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Issue", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Order info
            orderId?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Receipt, null, tint = Primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Order ID", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(it, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Issue type selection
            Text(
                text = "What's the issue?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            issueTypes.forEach { (issue, icon) ->
                IssueTypeItem(
                    issue = issue,
                    icon = icon,
                    isSelected = selectedIssueType == issue,
                    onClick = { selectedIssueType = issue }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Description
            Text(
                text = "Describe the issue",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Please provide details about your issue...") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Photo upload
            Card(
                onClick = { /* Upload photo */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CameraAlt, null, tint = Primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Photos (optional)", color = Primary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Submit button
            Button(
                onClick = {
                    if (selectedIssueType != null) {
                        viewModel.submitIssue(orderId ?: "", selectedIssueType!!, description)
                        showSuccess = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedIssueType != null,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = "Submit Report",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun IssueTypeItem(
    issue: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Primary.copy(alpha = 0.1f) else SurfaceLight
        ),
        border = if (isSelected) BorderStroke(2.dp, Primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                tint = if (isSelected) Primary else TextSecondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = issue,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Primary else TextPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isSelected) {
                Icon(
                    Icons.Filled.CheckCircle,
                    null,
                    tint = Primary
                )
            }
        }
    }
}

@Composable
private fun IssueSubmittedDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Filled.CheckCircle,
                null,
                tint = Success,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text("Report Submitted", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "We've received your report. Our team will review it and get back to you within 24 hours.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
