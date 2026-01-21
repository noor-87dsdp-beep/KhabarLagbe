package com.noor.khabarlagbe.restaurant.presentation.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.noor.khabarlagbe.restaurant.domain.model.Reports
import com.noor.khabarlagbe.restaurant.presentation.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBackClick: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val selectedDateRange by viewModel.selectedDateRange.collectAsState()
    val startDate by viewModel.startDate.collectAsState()
    val endDate by viewModel.endDate.collectAsState()
    
    var showDateRangeMenu by remember { mutableStateOf(false) }
    var showExportMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("রিপোর্ট ও বিশ্লেষণ") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(Icons.Default.Download, contentDescription = "Export")
                        }
                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("PDF এক্সপোর্ট") },
                                onClick = {
                                    viewModel.exportReport("pdf")
                                    showExportMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Excel এক্সপোর্ট") },
                                onClick = {
                                    viewModel.exportReport("xlsx")
                                    showExportMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.TableChart, contentDescription = null) }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Date range selector
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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
                            text = "সময়কাল",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$startDate - $endDate",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Box {
                        OutlinedButton(onClick = { showDateRangeMenu = true }) {
                            Text(getDateRangeLabel(selectedDateRange))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = showDateRangeMenu,
                            onDismissRequest = { showDateRangeMenu = false }
                        ) {
                            DateRangeOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(getDateRangeLabel(option)) },
                                    onClick = {
                                        viewModel.selectDateRange(option)
                                        showDateRangeMenu = false
                                    },
                                    trailingIcon = {
                                        if (selectedDateRange == option) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            when (uiState) {
                is ReportsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ReportsUiState.Success -> {
                    val reports = (uiState as ReportsUiState.Success).reports
                    ReportsContent(reports = reports)
                }
                is ReportsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (uiState as ReportsUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadReports() }) {
                                Text("আবার চেষ্টা করুন")
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Export state handling
    when (exportState) {
        is ExportState.Loading -> {
            // Show loading indicator
        }
        is ExportState.Success -> {
            LaunchedEffect(Unit) {
                // Open download URL
                viewModel.clearExportState()
            }
        }
        is ExportState.Error -> {
            LaunchedEffect(Unit) {
                viewModel.clearExportState()
            }
        }
        else -> {}
    }
}

@Composable
fun ReportsContent(reports: Reports) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary cards
        item {
            Text(
                text = "সামগ্রিক পরিসংখ্যান",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "মোট আয়",
                    value = "৳${reports.summary.totalRevenue.toInt()}",
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "মোট অর্ডার",
                    value = "${reports.summary.totalOrders}",
                    icon = Icons.Default.Receipt,
                    color = Color(0xFF2196F3),
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
                    title = "গড় অর্ডার মূল্য",
                    value = "৳${reports.summary.averageOrderValue.toInt()}",
                    icon = Icons.Default.TrendingUp,
                    color = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "গড় রেটিং",
                    value = "%.1f".format(reports.summary.averageRating),
                    icon = Icons.Default.Star,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Growth indicators
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "প্রবৃদ্ধি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        GrowthIndicator(
                            label = "আয় বৃদ্ধি",
                            value = reports.summary.revenueGrowth,
                            modifier = Modifier.weight(1f)
                        )
                        GrowthIndicator(
                            label = "অর্ডার বৃদ্ধি",
                            value = reports.summary.orderGrowth,
                            modifier = Modifier.weight(1f)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${(reports.summary.completionRate * 100).toInt()}%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text(
                                text = "সম্পূর্ণতার হার",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
        
        // Top selling items
        item {
            Text(
                text = "সবচেয়ে বিক্রিত আইটেম",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        items(reports.topSellingItems.take(5)) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${item.quantity} বিক্রি",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "৳${item.revenue.toInt()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${item.percentageOfTotal.toInt()}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        
        // Peak hours
        item {
            Text(
                text = "পিক আওয়ার",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    reports.peakHours.sortedByDescending { it.orderCount }.take(5).forEach { peak ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${peak.hour}:00 - ${peak.hour + 1}:00",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${peak.orderCount} অর্ডার",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        
        // Payment methods
        item {
            Text(
                text = "পেমেন্ট পদ্ধতি",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    reports.paymentMethodBreakdown.forEach { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (method.method.lowercase()) {
                                        "cash" -> Icons.Default.Money
                                        "card" -> Icons.Default.CreditCard
                                        else -> Icons.Default.AccountBalance
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = method.method,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${method.count} টি লেনদেন",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "৳${method.amount.toInt()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${method.percentage.toInt()}%",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GrowthIndicator(label: String, value: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (value >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                contentDescription = null,
                tint = if (value >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${if (value >= 0) "+" else ""}${value.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (value >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun getDateRangeLabel(option: DateRangeOption): String {
    return when (option) {
        DateRangeOption.TODAY -> "আজ"
        DateRangeOption.YESTERDAY -> "গতকাল"
        DateRangeOption.LAST_7_DAYS -> "গত ৭ দিন"
        DateRangeOption.LAST_30_DAYS -> "গত ৩০ দিন"
        DateRangeOption.THIS_MONTH -> "এই মাস"
        DateRangeOption.LAST_MONTH -> "গত মাস"
        DateRangeOption.CUSTOM -> "কাস্টম"
    }
}
