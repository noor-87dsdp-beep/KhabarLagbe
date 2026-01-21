package com.noor.khabarlagbe.domain.model

data class Wallet(
    val id: String,
    val userId: String,
    val balance: Double,
    val currency: String = "BDT",
    val isActive: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class WalletTransaction(
    val id: String,
    val walletId: String,
    val amount: Double,
    val type: WalletTransactionType,
    val status: WalletTransactionStatus,
    val description: String,
    val referenceId: String? = null, // Order ID or Top-up ID
    val paymentMethod: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class WalletTransactionType {
    TOP_UP,
    PAYMENT,
    REFUND,
    CASHBACK,
    REWARD_REDEMPTION,
    REFERRAL_BONUS,
    PROMOTIONAL_CREDIT
}

enum class WalletTransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class TopUpOption(
    val id: String,
    val amount: Double,
    val bonusAmount: Double = 0.0,
    val isPopular: Boolean = false,
    val description: String? = null
)
