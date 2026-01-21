package com.noor.khabarlagbe.restaurant.data.dto

import com.google.gson.annotations.SerializedName

data class ReportsResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ReportsDataDto?
)

data class ReportsDataDto(
    @SerializedName("summary") val summary: ReportsSummaryDto,
    @SerializedName("revenueChart") val revenueChart: List<RevenueDataPointDto>,
    @SerializedName("orderChart") val orderChart: List<OrderDataPointDto>,
    @SerializedName("topSellingItems") val topSellingItems: List<TopSellingItemDto>,
    @SerializedName("peakHours") val peakHours: List<PeakHourDto>,
    @SerializedName("ordersByStatus") val ordersByStatus: OrdersByStatusDto,
    @SerializedName("paymentMethodBreakdown") val paymentMethodBreakdown: List<PaymentMethodBreakdownDto>
)

data class ReportsSummaryDto(
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("totalOrders") val totalOrders: Int,
    @SerializedName("averageOrderValue") val averageOrderValue: Double,
    @SerializedName("completionRate") val completionRate: Double,
    @SerializedName("averageRating") val averageRating: Double,
    @SerializedName("totalReviews") val totalReviews: Int,
    @SerializedName("revenueGrowth") val revenueGrowth: Double,
    @SerializedName("orderGrowth") val orderGrowth: Double
)

data class RevenueDataPointDto(
    @SerializedName("date") val date: String,
    @SerializedName("revenue") val revenue: Double,
    @SerializedName("orders") val orders: Int
)

data class OrderDataPointDto(
    @SerializedName("date") val date: String,
    @SerializedName("count") val count: Int,
    @SerializedName("completed") val completed: Int,
    @SerializedName("cancelled") val cancelled: Int
)

data class TopSellingItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("revenue") val revenue: Double,
    @SerializedName("percentageOfTotal") val percentageOfTotal: Double
)

data class PeakHourDto(
    @SerializedName("hour") val hour: Int,
    @SerializedName("orderCount") val orderCount: Int,
    @SerializedName("revenue") val revenue: Double
)

data class OrdersByStatusDto(
    @SerializedName("completed") val completed: Int,
    @SerializedName("cancelled") val cancelled: Int,
    @SerializedName("rejected") val rejected: Int
)

data class PaymentMethodBreakdownDto(
    @SerializedName("method") val method: String,
    @SerializedName("count") val count: Int,
    @SerializedName("amount") val amount: Double,
    @SerializedName("percentage") val percentage: Double
)

data class ExportReportRequestDto(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("format") val format: String
)

data class ExportReportResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ExportDataDto?
)

data class ExportDataDto(
    @SerializedName("downloadUrl") val downloadUrl: String,
    @SerializedName("expiresAt") val expiresAt: String
)
