package com.noor.khabarlagbe.rider.domain.model

data class BankDetails(
    val accountHolderName: String,
    val accountNumber: String,
    val bankName: String,
    val branchName: String,
    val routingNumber: String? = null,
    val accountType: AccountType = AccountType.SAVINGS
)

enum class AccountType {
    SAVINGS,
    CURRENT
}
