package com.noor.khabarlagbe.rider.data.repository

import com.noor.khabarlagbe.rider.data.api.RiderApi
import com.noor.khabarlagbe.rider.data.dto.*
import com.noor.khabarlagbe.rider.data.local.dao.EarningsDao
import com.noor.khabarlagbe.rider.data.local.entity.EarningsEntity
import com.noor.khabarlagbe.rider.domain.model.Earnings
import com.noor.khabarlagbe.rider.domain.model.EarningsEntry
import com.noor.khabarlagbe.rider.domain.model.EarningsSummary
import com.noor.khabarlagbe.rider.domain.model.EarningsType
import com.noor.khabarlagbe.rider.domain.repository.RiderAuthRepository
import com.noor.khabarlagbe.rider.domain.repository.RiderEarningsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiderEarningsRepositoryImpl @Inject constructor(
    private val riderApi: RiderApi,
    private val earningsDao: EarningsDao,
    private val authRepository: RiderAuthRepository
) : RiderEarningsRepository {
    
    override val todayEarnings: Flow<Double> = earningsDao.getAllEarnings().map { entries ->
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % (24 * 60 * 60 * 1000))
        entries.filter { it.date >= startOfDay }.sumOf { it.amount + it.tip }
    }
    
    override suspend fun getEarnings(period: String): Result<Earnings> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getEarnings("Bearer $token", period)
                if (response.isSuccessful && response.body() != null) {
                    val earnings = response.body()!!.toDomain()
                    Result.success(earnings)
                } else {
                    Result.failure(Exception("Failed to fetch earnings"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            val now = System.currentTimeMillis()
            val dayInMs = 24 * 60 * 60 * 1000L
            
            val todayStart = now - (now % dayInMs)
            val weekStart = todayStart - (6 * dayInMs)
            val monthStart = todayStart - (29 * dayInMs)
            
            val todayTotal = earningsDao.getTotalEarningsByDateRange(todayStart, now) ?: 0.0
            val todayCount = earningsDao.getDeliveryCountByDateRange(todayStart, now)
            
            val weekTotal = earningsDao.getTotalEarningsByDateRange(weekStart, now) ?: 0.0
            val weekCount = earningsDao.getDeliveryCountByDateRange(weekStart, now)
            
            val monthTotal = earningsDao.getTotalEarningsByDateRange(monthStart, now) ?: 0.0
            val monthCount = earningsDao.getDeliveryCountByDateRange(monthStart, now)
            
            val cachedEarnings = Earnings(
                today = EarningsSummary(todayTotal, todayTotal, 0.0, 0.0, todayCount),
                thisWeek = EarningsSummary(weekTotal, weekTotal, 0.0, 0.0, weekCount),
                thisMonth = EarningsSummary(monthTotal, monthTotal, 0.0, 0.0, monthCount),
                history = emptyList()
            )
            Result.success(cachedEarnings)
        }
    }
    
    override suspend fun getEarningsSummary(
        startDate: String,
        endDate: String
    ): Result<Pair<EarningsSummary, List<DailyEarningsDto>>> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getEarningsSummary("Bearer $token", startDate, endDate)
                if (response.isSuccessful && response.body() != null) {
                    val summaryDto = response.body()!!
                    val summary = summaryDto.summary.toDomain()
                    Result.success(Pair(summary, summaryDto.dailyBreakdown))
                } else {
                    Result.failure(Exception("Failed to fetch earnings summary"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEarningsHistory(
        page: Int,
        limit: Int,
        startDate: String?,
        endDate: String?
    ): Result<List<EarningsEntry>> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getEarningsHistory(
                    "Bearer $token",
                    page,
                    limit,
                    startDate,
                    endDate
                )
                if (response.isSuccessful && response.body() != null) {
                    val entries = response.body()!!.entries.map { it.toDomain() }
                    
                    val entities = response.body()!!.entries.map { dto ->
                        EarningsEntity(
                            id = dto.id,
                            date = dto.date,
                            orderId = dto.orderId,
                            amount = dto.amount,
                            tip = dto.tip,
                            type = dto.type,
                            description = dto.description
                        )
                    }
                    earningsDao.insertEarnings(entities)
                    
                    Result.success(entries)
                } else {
                    Result.failure(Exception("Failed to fetch earnings history"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getLocalEarningsHistory(): Flow<List<EarningsEntry>> {
        return earningsDao.getAllEarnings().map { entities ->
            entities.map { entity ->
                EarningsEntry(
                    date = entity.date,
                    orderId = entity.orderId ?: "",
                    amount = entity.amount,
                    tip = entity.tip,
                    type = try { EarningsType.valueOf(entity.type) } catch (e: Exception) { EarningsType.DELIVERY }
                )
            }
        }
    }
    
    override suspend fun getAvailableBalance(): Result<Double> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getEarnings("Bearer $token", "today")
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.availableBalance)
                } else {
                    Result.failure(Exception("Failed to fetch available balance"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun requestWithdrawal(
        amount: Double,
        method: String,
        accountDetails: String?
    ): Result<WithdrawalDto> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.requestWithdrawal(
                    "Bearer $token",
                    WithdrawalRequest(amount, method, accountDetails)
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.withdrawal)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception(errorBody ?: "Failed to request withdrawal"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWithdrawalHistory(page: Int, limit: Int): Result<List<WithdrawalDto>> {
        return try {
            val token = authRepository.getAccessToken()
            if (token != null) {
                val response = riderApi.getWithdrawalHistory("Bearer $token", page, limit)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.withdrawals)
                } else {
                    Result.failure(Exception("Failed to fetch withdrawal history"))
                }
            } else {
                Result.failure(Exception("Not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun syncEarnings() {
        try {
            getEarnings("today")
        } catch (e: Exception) {
            // Ignore sync errors
        }
    }
}
