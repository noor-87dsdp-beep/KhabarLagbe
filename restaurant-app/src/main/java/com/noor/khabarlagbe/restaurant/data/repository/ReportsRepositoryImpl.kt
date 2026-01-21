package com.noor.khabarlagbe.restaurant.data.repository

import com.noor.khabarlagbe.restaurant.data.api.RestaurantApi
import com.noor.khabarlagbe.restaurant.data.dto.ExportReportRequestDto
import com.noor.khabarlagbe.restaurant.domain.model.*
import com.noor.khabarlagbe.restaurant.domain.repository.ReportsRepository
import com.noor.khabarlagbe.restaurant.domain.repository.RestaurantAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportsRepositoryImpl @Inject constructor(
    private val api: RestaurantApi,
    private val authRepository: RestaurantAuthRepository
) : ReportsRepository {
    
    private fun getAuthToken(): String {
        return "Bearer ${authRepository.getToken() ?: ""}"
    }
    
    override suspend fun getReports(startDate: String, endDate: String, period: String): Result<Reports> {
        return try {
            val response = api.getReports(getAuthToken(), startDate, endDate, period)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                Result.success(
                    Reports(
                        summary = ReportsSummary(
                            totalRevenue = data.summary.totalRevenue,
                            totalOrders = data.summary.totalOrders,
                            averageOrderValue = data.summary.averageOrderValue,
                            completionRate = data.summary.completionRate,
                            averageRating = data.summary.averageRating,
                            totalReviews = data.summary.totalReviews,
                            revenueGrowth = data.summary.revenueGrowth,
                            orderGrowth = data.summary.orderGrowth
                        ),
                        revenueChart = data.revenueChart.map { 
                            RevenueDataPoint(it.date, it.revenue, it.orders) 
                        },
                        orderChart = data.orderChart.map { 
                            OrderDataPoint(it.date, it.count, it.completed, it.cancelled) 
                        },
                        topSellingItems = data.topSellingItems.map { 
                            TopSellingItem(it.id, it.name, it.nameEn, it.imageUrl, it.quantity, it.revenue, it.percentageOfTotal) 
                        },
                        peakHours = data.peakHours.map { 
                            PeakHour(it.hour, it.orderCount, it.revenue) 
                        },
                        ordersByStatus = OrdersByStatus(
                            completed = data.ordersByStatus.completed,
                            cancelled = data.ordersByStatus.cancelled,
                            rejected = data.ordersByStatus.rejected
                        ),
                        paymentMethodBreakdown = data.paymentMethodBreakdown.map { 
                            PaymentMethodBreakdown(it.method, it.count, it.amount, it.percentage) 
                        }
                    )
                )
            } else {
                Result.failure(Exception("Failed to fetch reports"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun exportReport(startDate: String, endDate: String, format: String): Result<String> {
        return try {
            val request = ExportReportRequestDto(startDate, endDate, format)
            val response = api.exportReport(getAuthToken(), request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!.downloadUrl)
            } else {
                Result.failure(Exception("Failed to export report"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
