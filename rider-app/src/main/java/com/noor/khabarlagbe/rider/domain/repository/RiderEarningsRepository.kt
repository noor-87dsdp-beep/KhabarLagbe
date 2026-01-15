package com.noor.khabarlagbe.rider.domain.repository

import com.noor.khabarlagbe.rider.domain.model.Earnings
import com.noor.khabarlagbe.rider.domain.model.EarningsEntry
import kotlinx.coroutines.flow.Flow

interface RiderEarningsRepository {
    suspend fun getTodayEarnings(): Result<Earnings>
    suspend fun getEarningsByDateRange(startDate: Long, endDate: Long): Result<List<EarningsEntry>>
    fun observeEarnings(): Flow<Earnings>
    suspend fun getTransactionHistory(page: Int, pageSize: Int): Result<List<EarningsEntry>>
}
