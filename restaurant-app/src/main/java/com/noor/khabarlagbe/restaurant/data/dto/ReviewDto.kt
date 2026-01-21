package com.noor.khabarlagbe.restaurant.data.dto

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    @SerializedName("id") val id: String,
    @SerializedName("orderId") val orderId: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("customerName") val customerName: String,
    @SerializedName("customerAvatar") val customerAvatar: String?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String?,
    @SerializedName("images") val images: List<String>?,
    @SerializedName("response") val response: ReviewResponseDataDto?,
    @SerializedName("orderItems") val orderItems: List<String>?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class ReviewResponseDataDto(
    @SerializedName("text") val text: String,
    @SerializedName("respondedAt") val respondedAt: String
)

data class ReviewsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ReviewsDataDto?
)

data class ReviewsDataDto(
    @SerializedName("reviews") val reviews: List<ReviewDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("summary") val summary: ReviewsSummaryDto
)

data class ReviewsSummaryDto(
    @SerializedName("averageRating") val averageRating: Double,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("ratingDistribution") val ratingDistribution: RatingDistributionDto
)

data class RatingDistributionDto(
    @SerializedName("five") val five: Int,
    @SerializedName("four") val four: Int,
    @SerializedName("three") val three: Int,
    @SerializedName("two") val two: Int,
    @SerializedName("one") val one: Int
)

data class RespondToReviewRequestDto(
    @SerializedName("response") val response: String
)

data class ReviewResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: ReviewDto?
)
