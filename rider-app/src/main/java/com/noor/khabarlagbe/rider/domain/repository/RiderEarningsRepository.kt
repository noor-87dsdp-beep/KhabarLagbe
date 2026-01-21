package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.Earnings
import com.noor.khabarlagbe.rider.domain.model.EarningsEntry
import com.noor.khabarlagbe.rider.domain.model.EarningsSummary
import com.noor.khabarlagbe.rider.data.dto.DailyEarningsDto
import com.noor.khabarlagbe.rider.data.dto.WithdrawalDto
import kotlinx.coroutines.flow.Flow

interface RiderEarningsRepository {
    
    val todayEarnings: Flow<Double>
    
    suspend fun getEarnings(period: String = "today"): Result<Earnings>
    
    suspend fun getEarningsSummary(
        startDate: String,
        endDate: String
    ): Result<Pair<EarningsSummary, List<DailyEarningsDto>>>
    
    suspend fun getEarningsHistory(
        page: Int = 1,
        limit: Int = 50,
        startDate: String? = null,
        endDate: String? = null
    ): Result<List<EarningsEntry>>
    
    fun getLocalEarningsHistory(): Flow<List<EarningsEntry>>
    
    suspend fun getAvailableBalance(): Result<Double>
    
    suspend fun requestWithdrawal(
        amount: Double,
        method: String,
        accountDetails: String?
    ): Result<WithdrawalDto>
    
    suspend fun getWithdrawalHistory(
        page: Int = 1,
        limit: Int = 20
    ): Result<List<WithdrawalDto>>
    
    suspend fun syncEarnings()
}
