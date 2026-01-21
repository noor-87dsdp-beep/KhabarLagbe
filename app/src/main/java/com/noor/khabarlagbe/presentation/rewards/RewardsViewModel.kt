package com.noor.khabarlagbe.presentation.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noor.khabarlagbe.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<RewardsUiState>(RewardsUiState.Loading)
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    private val _pointsHistory = MutableStateFlow<List<PointsTransaction>>(emptyList())
    val pointsHistory: StateFlow<List<PointsTransaction>> = _pointsHistory.asStateFlow()

    init {
        loadRewardsData()
    }

    fun loadRewardsData() {
        viewModelScope.launch {
            _uiState.value = RewardsUiState.Loading
            // Simulate API call - replace with actual repository call
            kotlinx.coroutines.delay(500)
            
            val loyaltyProfile = LoyaltyProfile(
                userId = "user_1",
                currentPoints = 1250,
                lifetimePoints = 4500,
                tier = LoyaltyTier.SILVER,
                tierProgress = 0.625f,
                pointsToNextTier = 750,
                memberSince = System.currentTimeMillis() - 90 * 24 * 60 * 60 * 1000L,
                streakDays = 7,
                availableRewards = getSampleRewards()
            )
            
            _uiState.value = RewardsUiState.Success(loyaltyProfile)
            _pointsHistory.value = getSampleTransactions()
        }
    }

    fun redeemReward(rewardId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is RewardsUiState.Success) {
                val reward = currentState.loyaltyProfile.availableRewards.find { it.id == rewardId }
                if (reward != null && currentState.loyaltyProfile.currentPoints >= reward.pointsCost) {
                    // Simulate redemption
                    _uiState.value = currentState.copy(
                        loyaltyProfile = currentState.loyaltyProfile.copy(
                            currentPoints = currentState.loyaltyProfile.currentPoints - reward.pointsCost,
                            availableRewards = currentState.loyaltyProfile.availableRewards.map {
                                if (it.id == rewardId) it.copy(isRedeemed = true, redemptionCode = "REWARD${System.currentTimeMillis()}") else it
                            }
                        ),
                        redeemSuccess = true,
                        redeemedReward = reward
                    )
                }
            }
        }
    }

    fun clearRedeemSuccess() {
        val currentState = _uiState.value
        if (currentState is RewardsUiState.Success) {
            _uiState.value = currentState.copy(redeemSuccess = false, redeemedReward = null)
        }
    }

    private fun getSampleRewards(): List<Reward> = listOf(
        Reward("r1", "Free Delivery", "Get free delivery on your next order", 100, null, null, RewardCategory.FREE_DELIVERY),
        Reward("r2", "20% Off", "20% discount up to ৳200", 200, null, null, RewardCategory.DISCOUNT_PERCENTAGE),
        Reward("r3", "৳50 Cashback", "Flat ৳50 cashback", 150, null, null, RewardCategory.CASHBACK),
        Reward("r4", "Free Dessert", "Free dessert with any order over ৳500", 250, null, null, RewardCategory.FREE_ITEM),
        Reward("r5", "30% Off", "30% discount up to ৳300", 350, null, null, RewardCategory.DISCOUNT_PERCENTAGE),
        Reward("r6", "VIP Access", "Early access to new restaurants", 500, null, null, RewardCategory.EXCLUSIVE_ACCESS)
    )

    private fun getSampleTransactions(): List<PointsTransaction> = listOf(
        PointsTransaction("t1", 50, PointsTransactionType.EARNED_ORDER, "Order from Star Kabab", "order_1", System.currentTimeMillis() - 86400000),
        PointsTransaction("t2", 25, PointsTransactionType.EARNED_REVIEW, "Review bonus", null, System.currentTimeMillis() - 172800000),
        PointsTransaction("t3", -100, PointsTransactionType.REDEEMED, "Free delivery redeemed", null, System.currentTimeMillis() - 259200000),
        PointsTransaction("t4", 75, PointsTransactionType.EARNED_ORDER, "Order from Pizza Hut", "order_2", System.currentTimeMillis() - 345600000),
        PointsTransaction("t5", 100, PointsTransactionType.EARNED_REFERRAL, "Friend joined: Ahmed", null, System.currentTimeMillis() - 432000000),
        PointsTransaction("t6", 50, PointsTransactionType.EARNED_BONUS, "Weekly streak bonus", null, System.currentTimeMillis() - 518400000)
    )
}

sealed class RewardsUiState {
    object Loading : RewardsUiState()
    data class Success(
        val loyaltyProfile: LoyaltyProfile,
        val redeemSuccess: Boolean = false,
        val redeemedReward: Reward? = null
    ) : RewardsUiState()
    data class Error(val message: String) : RewardsUiState()
}
