package com.noor.khabarlagbe.rider.data.repository

import com.noor.khabarlagbe.rider.data.remote.api.RiderApi
import com.noor.khabarlagbe.rider.domain.model.Earnings
import com.noor.khabarlagbe.rider.domain.model.EarningsSummary
import com.noor.khabarlagbe.rider.domain.model.EarningsEntry
import com.noor.khabarlagbe.rider.domain.repository.RiderEarningsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RiderEarningsRepositoryImpl @Inject constructor(
    private val api: RiderApi
) : RiderEarningsRepository {
    
    private val _earnings = MutableStateFlow<Earnings?>(null)
    
    override suspend fun getTodayEarnings(): Result<Earnings> {
        return try {
            val response = api.getTodayEarnings()
            if (response.isSuccessful && response.body() != null) {
                _earnings.value = response.body()
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("আয় তথ্য লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getEarningsByDateRange(startDate: Long, endDate: Long): Result<List<EarningsEntry>> {
        return try {
            val response = api.getEarningsByDateRange(startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("আয় তথ্য লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeEarnings(): Flow<Earnings> {
        return _earnings
            .map { it ?: Earnings(
                EarningsSummary(0.0, 0.0, 0.0, 0.0, 0),
                EarningsSummary(0.0, 0.0, 0.0, 0.0, 0),
                EarningsSummary(0.0, 0.0, 0.0, 0.0, 0),
                emptyList()
            )}
    }
    
    override suspend fun getTransactionHistory(page: Int, pageSize: Int): Result<List<EarningsEntry>> {
        return try {
            val response = api.getTransactionHistory(page, pageSize)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("লেনদেন ইতিহাস লোড করতে ব্যর্থ"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
