package com.noor.khabarlagbe.restaurant.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.restaurant.domain.model.Review
import com.noor.khabarlagbe.restaurant.domain.model.ReviewsSummary
import com.noor.khabarlagbe.restaurant.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReviewsUiState {
    object Loading : ReviewsUiState()
    data class Success(
        val reviews: List<Review>,
        val summary: ReviewsSummary,
        val hasMore: Boolean
    ) : ReviewsUiState()
    data class Error(val message: String) : ReviewsUiState()
}

sealed class RespondState {
    object Idle : RespondState()
    object Loading : RespondState()
    object Success : RespondState()
    data class Error(val message: String) : RespondState()
}

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<ReviewsUiState>(ReviewsUiState.Loading)
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()
    
    private val _respondState = MutableStateFlow<RespondState>(RespondState.Idle)
    val respondState: StateFlow<RespondState> = _respondState.asStateFlow()
    
    private val _selectedRatingFilter = MutableStateFlow<Int?>(null)
    val selectedRatingFilter: StateFlow<Int?> = _selectedRatingFilter.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _respondingToReviewId = MutableStateFlow<String?>(null)
    val respondingToReviewId: StateFlow<String?> = _respondingToReviewId.asStateFlow()
    
    private val _responseText = MutableStateFlow("")
    val responseText: StateFlow<String> = _responseText.asStateFlow()
    
    private var currentPage = 1
    private var totalPages = 1
    
    init {
        loadReviews()
    }
    
    fun loadReviews(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh) {
                currentPage = 1
            }
            _uiState.value = ReviewsUiState.Loading
            reviewRepository.getReviewsWithSummary(page = currentPage, rating = _selectedRatingFilter.value)
                .onSuccess { data ->
                    totalPages = data.totalPages
                    _uiState.value = ReviewsUiState.Success(
                        reviews = data.reviews,
                        summary = data.summary,
                        hasMore = currentPage < totalPages
                    )
                }
                .onFailure { error ->
                    _uiState.value = ReviewsUiState.Error(error.message ?: "Failed to load reviews")
                }
        }
    }
    
    fun loadMore() {
        if (currentPage < totalPages) {
            viewModelScope.launch {
                currentPage++
                reviewRepository.getReviewsWithSummary(page = currentPage, rating = _selectedRatingFilter.value)
                    .onSuccess { data ->
                        val currentState = _uiState.value
                        if (currentState is ReviewsUiState.Success) {
                            _uiState.value = currentState.copy(
                                reviews = currentState.reviews + data.reviews,
                                hasMore = currentPage < totalPages
                            )
                        }
                    }
            }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            reviewRepository.getReviewsWithSummary(page = 1, rating = _selectedRatingFilter.value)
                .onSuccess { data ->
                    totalPages = data.totalPages
                    _uiState.value = ReviewsUiState.Success(
                        reviews = data.reviews,
                        summary = data.summary,
                        hasMore = currentPage < totalPages
                    )
                }
            _isRefreshing.value = false
        }
    }
    
    fun filterByRating(rating: Int?) {
        _selectedRatingFilter.value = rating
        currentPage = 1
        loadReviews(forceRefresh = true)
    }
    
    fun startResponding(reviewId: String) {
        _respondingToReviewId.value = reviewId
        _responseText.value = ""
    }
    
    fun cancelResponding() {
        _respondingToReviewId.value = null
        _responseText.value = ""
        _respondState.value = RespondState.Idle
    }
    
    fun updateResponseText(text: String) {
        _responseText.value = text
    }
    
    fun submitResponse() {
        val reviewId = _respondingToReviewId.value ?: return
        val response = _responseText.value
        if (response.isBlank()) return
        
        viewModelScope.launch {
            _respondState.value = RespondState.Loading
            reviewRepository.respondToReview(reviewId, response)
                .onSuccess {
                    _respondState.value = RespondState.Success
                    _respondingToReviewId.value = null
                    _responseText.value = ""
                    refresh()
                }
                .onFailure { error ->
                    _respondState.value = RespondState.Error(error.message ?: "Failed to submit response")
                }
        }
    }
    
    fun clearRespondState() {
        _respondState.value = RespondState.Idle
    }
}
