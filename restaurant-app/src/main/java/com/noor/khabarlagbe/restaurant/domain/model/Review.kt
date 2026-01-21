package com.noor.khabarlagbe.restaurant.domain.model

data class Review(
    val id: String,
    val orderId: String,
    val customerId: String,
    val customerName: String,
    val customerAvatar: String?,
    val rating: Int,
    val comment: String?,
    val images: List<String>?,
    val response: ReviewResponse?,
    val orderItems: List<String>?,
    val createdAt: String
)

data class ReviewResponse(
    val text: String,
    val respondedAt: String
)

data class ReviewsSummary(
    val averageRating: Double,
    val totalReviews: Int,
    val ratingDistribution: RatingDistribution
)

data class RatingDistribution(
    val five: Int,
    val four: Int,
    val three: Int,
    val two: Int,
    val one: Int
)

data class ReviewsWithSummary(
    val reviews: List<Review>,
    val summary: ReviewsSummary,
    val total: Int,
    val page: Int,
    val totalPages: Int
)
