package com.noor.khabarlagbe.restaurant.data.repository

import com.noor.khabarlagbe.restaurant.data.api.RestaurantApi
import com.noor.khabarlagbe.restaurant.data.dto.RespondToReviewRequestDto
import com.noor.khabarlagbe.restaurant.data.dto.ReviewDto
import com.noor.khabarlagbe.restaurant.data.local.dao.ReviewDao
import com.noor.khabarlagbe.restaurant.data.local.entity.ReviewEntity
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import com.noor.khabarlagbe.restaurant.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepositoryImpl @Inject constructor(
    private val api: RestaurantApi,
    private val reviewDao: ReviewDao,
    private val authRepository: RestaurantAuthRepository
) : ReviewRepository {
    
    private fun getAuthToken(): String {
        return "Bearer ${authRepository.getToken() ?: ""}"
    }
    
    override fun getReviews(): Flow<List<Review>> {
        return reviewDao.getAllReviews().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun getReviewsByRating(rating: Int): Flow<List<Review>> {
        return reviewDao.getReviewsByRating(rating).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getReviewsWithSummary(page: Int, rating: Int?): Result<ReviewsWithSummary> {
        return try {
            val response = api.getReviews(getAuthToken(), page = page, rating = rating)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                val reviews = data.reviews.map { it.toDomain() }
                
                reviewDao.insertReviews(reviews.map { it.toEntity() })
                
                Result.success(
                    ReviewsWithSummary(
                        reviews = reviews,
                        summary = ReviewsSummary(
                            averageRating = data.summary.averageRating,
                            totalReviews = data.summary.totalReviews,
                            ratingDistribution = RatingDistribution(
                                five = data.summary.ratingDistribution.five,
                                four = data.summary.ratingDistribution.four,
                                three = data.summary.ratingDistribution.three,
                                two = data.summary.ratingDistribution.two,
                                one = data.summary.ratingDistribution.one
                            )
                        ),
                        total = data.total,
                        page = data.page,
                        totalPages = data.totalPages
                    )
                )
            } else {
                Result.failure(Exception("Failed to fetch reviews"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun respondToReview(reviewId: String, response: String): Result<Review> {
        return try {
            val apiResponse = api.respondToReview(getAuthToken(), reviewId, RespondToReviewRequestDto(response))
            if (apiResponse.isSuccessful && apiResponse.body()?.success == true) {
                val review = apiResponse.body()!!.data!!.toDomain()
                reviewDao.updateReview(review.toEntity())
                Result.success(review)
            } else {
                Result.failure(Exception(apiResponse.body()?.message ?: "Failed to respond to review"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshReviews(): Result<List<Review>> {
        return try {
            val response = api.getReviews(getAuthToken())
            if (response.isSuccessful && response.body()?.success == true) {
                val reviews = response.body()!!.data!!.reviews.map { it.toDomain() }
                reviewDao.clearReviews()
                reviewDao.insertReviews(reviews.map { it.toEntity() })
                Result.success(reviews)
            } else {
                Result.failure(Exception("Failed to fetch reviews"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun ReviewDto.toDomain(): Review {
        return Review(
            id = id,
            orderId = orderId,
            customerId = customerId,
            customerName = customerName,
            customerAvatar = customerAvatar,
            rating = rating,
            comment = comment,
            images = images,
            response = response?.let { ReviewResponse(it.text, it.respondedAt) },
            orderItems = orderItems,
            createdAt = createdAt
        )
    }
    
    private fun Review.toEntity(): ReviewEntity {
        return ReviewEntity(
            id = id,
            orderId = orderId,
            customerId = customerId,
            customerName = customerName,
            customerAvatar = customerAvatar,
            rating = rating,
            comment = comment,
            responseText = response?.text,
            respondedAt = response?.respondedAt,
            createdAt = createdAt,
            updatedAt = createdAt
        )
    }
    
    private fun ReviewEntity.toDomain(): Review {
        return Review(
            id = id,
            orderId = orderId,
            customerId = customerId,
            customerName = customerName,
            customerAvatar = customerAvatar,
            rating = rating,
            comment = comment,
            images = null,
            response = responseText?.let { ReviewResponse(it, respondedAt ?: "") },
            orderItems = null,
            createdAt = createdAt
        )
    }
}
