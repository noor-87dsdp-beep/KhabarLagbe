package com.noor.khabarlagbe.rider.data.dto

import com.google.gson.annotations.SerializedName
import com.noor.khabarlagbe.rider.domain.model.*

data class EarningsDto(
    @SerializedName("today") val today: EarningsPeriodDto,
    @SerializedName("thisWeek") val thisWeek: EarningsPeriodDto,
    @SerializedName("thisMonth") val thisMonth: EarningsPeriodDto,
    @SerializedName("availableBalance") val availableBalance: Double,
    @SerializedName("pendingBalance") val pendingBalance: Double
) {
    fun toDomain(): Earnings {
        return Earnings(
            today = today.toDomain(),
            thisWeek = thisWeek.toDomain(),
            thisMonth = thisMonth.toDomain(),
            history = emptyList()
        )
    }
}

data class EarningsPeriodDto(
    @SerializedName("totalEarnings") val totalEarnings: Double,
    @SerializedName("deliveryFees") val deliveryFees: Double,
    @SerializedName("tips") val tips: Double,
    @SerializedName("incentives") val incentives: Double,
    @SerializedName("bonuses") val bonuses: Double,
    @SerializedName("deliveriesCount") val deliveriesCount: Int
) {
    fun toDomain(): EarningsSummary {
        return EarningsSummary(
            totalEarnings = totalEarnings,
            deliveryFees = deliveryFees,
            tips = tips,
            incentives = incentives + bonuses,
            deliveriesCount = deliveriesCount
        )
    }
}

data class EarningsSummaryDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("summary") val summary: EarningsPeriodDto,
    @SerializedName("dailyBreakdown") val dailyBreakdown: List<DailyEarningsDto>
)

data class DailyEarningsDto(
    @SerializedName("date") val date: String,
    @SerializedName("earnings") val earnings: Double,
    @SerializedName("deliveries") val deliveries: Int,
    @SerializedName("tips") val tips: Double,
    @SerializedName("bonuses") val bonuses: Double
)

data class EarningsHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("entries") val entries: List<EarningsEntryDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("totalPages") val totalPages: Int
)

data class EarningsEntryDto(
    @SerializedName("id") val id: String,
    @SerializedName("date") val date: Long,
    @SerializedName("orderId") val orderId: String?,
    @SerializedName("orderNumber") val orderNumber: String?,
    @SerializedName("amount") val amount: Double,
    @SerializedName("tip") val tip: Double,
    @SerializedName("type") val type: String,
    @SerializedName("description") val description: String?
) {
    fun toDomain(): EarningsEntry {
        return EarningsEntry(
            date = date,
            orderId = orderId ?: "",
            amount = amount,
            tip = tip,
            type = try { EarningsType.valueOf(type.uppercase()) } catch (e: Exception) { EarningsType.DELIVERY }
        )
    }
}

// Withdrawal DTOs
data class WithdrawalRequest(
    @SerializedName("amount") val amount: Double,
    @SerializedName("method") val method: String,
    @SerializedName("accountDetails") val accountDetails: String?
)

data class WithdrawalResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("withdrawal") val withdrawal: WithdrawalDto,
    @SerializedName("message") val message: String?
)

data class WithdrawalDto(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("method") val method: String,
    @SerializedName("status") val status: String,
    @SerializedName("accountDetails") val accountDetails: String?,
    @SerializedName("transactionId") val transactionId: String?,
    @SerializedName("requestedAt") val requestedAt: Long,
    @SerializedName("processedAt") val processedAt: Long?
)

data class WithdrawalHistoryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("withdrawals") val withdrawals: List<WithdrawalDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("totalPages") val totalPages: Int
)
