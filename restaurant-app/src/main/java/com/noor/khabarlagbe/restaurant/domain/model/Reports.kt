package com.noor.khabarlagbe.restaurant.domain.model

data class Reports(
    val summary: ReportsSummary,
    val revenueChart: List<RevenueDataPoint>,
    val orderChart: List<OrderDataPoint>,
    val topSellingItems: List<TopSellingItem>,
    val peakHours: List<PeakHour>,
    val ordersByStatus: OrdersByStatus,
    val paymentMethodBreakdown: List<PaymentMethodBreakdown>
)

data class ReportsSummary(
    val totalRevenue: Double,
    val totalOrders: Int,
    val averageOrderValue: Double,
    val completionRate: Double,
    val averageRating: Double,
    val totalReviews: Int,
    val revenueGrowth: Double,
    val orderGrowth: Double
)

data class RevenueDataPoint(
    val date: String,
    val revenue: Double,
    val orders: Int
)

data class OrderDataPoint(
    val date: String,
    val count: Int,
    val completed: Int,
    val cancelled: Int
)

data class TopSellingItem(
    val id: String,
    val name: String,
    val nameEn: String?,
    val imageUrl: String?,
    val quantity: Int,
    val revenue: Double,
    val percentageOfTotal: Double
)

data class PeakHour(
    val hour: Int,
    val orderCount: Int,
    val revenue: Double
)

data class OrdersByStatus(
    val completed: Int,
    val cancelled: Int,
    val rejected: Int
)

data class PaymentMethodBreakdown(
    val method: String,
    val count: Int,
    val amount: Double,
    val percentage: Double
)
