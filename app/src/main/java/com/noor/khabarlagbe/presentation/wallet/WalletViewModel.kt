package com.noor.khabarlagbe.presentation.wallet

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
class WalletViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<WalletUiState>(WalletUiState.Loading)
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _topUpOptions = MutableStateFlow<List<TopUpOption>>(emptyList())
    val topUpOptions: StateFlow<List<TopUpOption>> = _topUpOptions.asStateFlow()

    init {
        loadWalletData()
    }

    fun loadWalletData() {
        viewModelScope.launch {
            _uiState.value = WalletUiState.Loading
            kotlinx.coroutines.delay(500)
            
            val wallet = Wallet(
                id = "wallet_1",
                userId = "user_1",
                balance = 1250.50,
                currency = "BDT",
                isActive = true
            )
            
            _uiState.value = WalletUiState.Success(wallet)
            _transactions.value = getSampleTransactions()
            _topUpOptions.value = getTopUpOptions()
        }
    }

    fun addMoney(amount: Double, paymentMethod: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is WalletUiState.Success) {
                _uiState.value = currentState.copy(isProcessing = true)
                kotlinx.coroutines.delay(1500) // Simulate API call
                
                val newBalance = currentState.wallet.balance + amount
                val newTransaction = WalletTransaction(
                    id = "tx_${System.currentTimeMillis()}",
                    walletId = currentState.wallet.id,
                    amount = amount,
                    type = WalletTransactionType.TOP_UP,
                    status = WalletTransactionStatus.COMPLETED,
                    description = "Added via $paymentMethod",
                    paymentMethod = paymentMethod
                )
                
                _uiState.value = currentState.copy(
                    wallet = currentState.wallet.copy(balance = newBalance),
                    isProcessing = false,
                    topUpSuccess = true
                )
                _transactions.value = listOf(newTransaction) + _transactions.value
            }
        }
    }

    fun clearTopUpSuccess() {
        val currentState = _uiState.value
        if (currentState is WalletUiState.Success) {
            _uiState.value = currentState.copy(topUpSuccess = false)
        }
    }

    private fun getSampleTransactions(): List<WalletTransaction> = listOf(
        WalletTransaction("tx1", "wallet_1", 500.0, WalletTransactionType.TOP_UP, WalletTransactionStatus.COMPLETED, "Added via bKash", null, "bKash", System.currentTimeMillis() - 86400000),
        WalletTransaction("tx2", "wallet_1", -350.0, WalletTransactionType.PAYMENT, WalletTransactionStatus.COMPLETED, "Order #12345", "order_12345", null, System.currentTimeMillis() - 172800000),
        WalletTransaction("tx3", "wallet_1", 50.0, WalletTransactionType.CASHBACK, WalletTransactionStatus.COMPLETED, "Cashback from order", "order_12345", null, System.currentTimeMillis() - 259200000),
        WalletTransaction("tx4", "wallet_1", 100.0, WalletTransactionType.REFERRAL_BONUS, WalletTransactionStatus.COMPLETED, "Referral bonus", null, null, System.currentTimeMillis() - 345600000),
        WalletTransaction("tx5", "wallet_1", -200.0, WalletTransactionType.PAYMENT, WalletTransactionStatus.COMPLETED, "Order #12344", "order_12344", null, System.currentTimeMillis() - 432000000)
    )

    private fun getTopUpOptions(): List<TopUpOption> = listOf(
        TopUpOption("opt1", 100.0, 0.0, false, null),
        TopUpOption("opt2", 200.0, 0.0, false, null),
        TopUpOption("opt3", 500.0, 25.0, true, "Get ৳25 bonus!"),
        TopUpOption("opt4", 1000.0, 75.0, false, "Get ৳75 bonus!"),
        TopUpOption("opt5", 2000.0, 200.0, false, "Get ৳200 bonus!"),
        TopUpOption("opt6", 5000.0, 600.0, false, "Get ৳600 bonus!")
    )
}

sealed class WalletUiState {
    object Loading : WalletUiState()
    data class Success(
        val wallet: Wallet,
        val isProcessing: Boolean = false,
        val topUpSuccess: Boolean = false
    ) : WalletUiState()
    data class Error(val message: String) : WalletUiState()
}
