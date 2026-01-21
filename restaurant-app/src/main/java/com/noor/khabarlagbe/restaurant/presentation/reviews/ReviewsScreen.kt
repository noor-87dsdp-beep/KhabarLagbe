package com.noor.khabarlagbe.restaurant.presentation.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noor.khabarlagbe.restaurant.domain.model.RatingDistribution
import com.noor.khabarlagbe.restaurant.domain.model.ReviewsSummary
import com.noor.khabarlagbe.restaurant.presentation.components.ReviewCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    onBackClick: () -> Unit,
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val selectedRatingFilter by viewModel.selectedRatingFilter.collectAsState()
    val respondingToReviewId by viewModel.respondingToReviewId.collectAsState()
    val responseText by viewModel.responseText.collectAsState()
    val respondState by viewModel.respondState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("রিভিউ ও রেটিং") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is ReviewsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ReviewsUiState.Success -> {
                    val state = uiState as ReviewsUiState.Success
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Rating summary
                        item {
                            RatingSummaryCard(summary = state.summary)
                        }
                        
                        // Rating distribution
                        item {
                            RatingDistributionCard(
                                distribution = state.summary.ratingDistribution,
                                totalReviews = state.summary.totalReviews
                            )
                        }
                        
                        // Filter chips
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedRatingFilter == null,
                                    onClick = { viewModel.filterByRating(null) },
                                    label = { Text("সব") }
                                )
                                (5 downTo 1).forEach { rating ->
                                    FilterChip(
                                        selected = selectedRatingFilter == rating,
                                        onClick = { viewModel.filterByRating(rating) },
                                        label = { 
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("$rating")
                                                Icon(
                                                    Icons.Default.Star,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = Color(0xFFFFC107)
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Reviews list
                        if (state.reviews.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.RateReview,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = "কোনো রিভিউ নেই",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }
                            }
                        } else {
                            items(state.reviews, key = { it.id }) { review ->
                                ReviewCard(
                                    review = review,
                                    onRespond = { viewModel.startResponding(review.id) }
                                )
                            }
                            
                            if (state.hasMore) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TextButton(onClick = { viewModel.loadMore() }) {
                                            Text("আরো দেখুন")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is ReviewsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (uiState as ReviewsUiState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadReviews(forceRefresh = true) }) {
                                Text("আবার চেষ্টা করুন")
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Response dialog
    if (respondingToReviewId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelResponding() },
            title = { Text("রিভিউতে উত্তর দিন") },
            text = {
                Column {
                    OutlinedTextField(
                        value = responseText,
                        onValueChange = { viewModel.updateResponseText(it) },
                        label = { Text("আপনার উত্তর") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    if (respondState is RespondState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (respondState as RespondState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.submitResponse() },
                    enabled = responseText.isNotBlank() && respondState !is RespondState.Loading
                ) {
                    if (respondState is RespondState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("পাঠান")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelResponding() }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun RatingSummaryCard(summary: ReviewsSummary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%.1f".format(summary.averageRating),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    val fullStars = summary.averageRating.toInt()
                    val hasHalfStar = (summary.averageRating - fullStars) >= 0.5f
                    repeat(5) { index ->
                        Icon(
                            imageVector = when {
                                index < fullStars -> Icons.Default.Star
                                index == fullStars && hasHalfStar -> Icons.Default.StarHalf
                                else -> Icons.Default.StarBorder
                            },
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${summary.totalReviews} টি রিভিউ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RatingDistributionCard(distribution: RatingDistribution, totalReviews: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "রেটিং বিতরণ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            RatingBar(5, distribution.five, totalReviews)
            RatingBar(4, distribution.four, totalReviews)
            RatingBar(3, distribution.three, totalReviews)
            RatingBar(2, distribution.two, totalReviews)
            RatingBar(1, distribution.one, totalReviews)
        }
    }
}

@Composable
fun RatingBar(rating: Int, count: Int, total: Int) {
    val percentage = if (total > 0) count.toFloat() / total else 0f
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$rating",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(20.dp)
        )
        Icon(
            Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
            color = Color(0xFFFFC107),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(32.dp)
        )
    }
}
