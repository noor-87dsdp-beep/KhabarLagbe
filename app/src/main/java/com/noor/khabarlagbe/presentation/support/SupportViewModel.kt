package com.noor.khabarlagbe.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FAQItem(
    val id: String,
    val question: String,
    val answer: String,
    val category: String
)

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class SupportViewModel @Inject constructor() : ViewModel() {

    private val _faqs = MutableStateFlow<List<FAQItem>>(emptyList())
    val faqs: StateFlow<List<FAQItem>> = _faqs.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    init {
        loadFAQs()
        initializeChat()
    }

    private fun loadFAQs() {
        _faqs.value = listOf(
            FAQItem("1", "How do I track my order?", "Go to Order History and tap on your active order to see real-time tracking.", "Orders"),
            FAQItem("2", "How do I cancel an order?", "You can cancel within 5 minutes of placing. Go to Order Details and tap Cancel.", "Orders"),
            FAQItem("3", "How do I add money to wallet?", "Go to Wallet > Add Money, select amount and payment method.", "Payments"),
            FAQItem("4", "What payment methods are accepted?", "We accept bKash, Nagad, Rocket, cards, and Cash on Delivery.", "Payments"),
            FAQItem("5", "How do I change delivery address?", "Go to Profile > Saved Addresses to manage your addresses.", "Account"),
            FAQItem("6", "How do I contact the restaurant?", "Tap on restaurant name in Order Details for contact info.", "Orders"),
            FAQItem("7", "How do I report an issue with my order?", "Go to Order Details > Report Issue to submit a complaint.", "Orders"),
            FAQItem("8", "How do rewards points work?", "Earn 1 point per ৳10 spent. Redeem for discounts and free items.", "Rewards")
        )
    }

    private fun initializeChat() {
        _chatMessages.value = listOf(
            ChatMessage("init", "Hi! I'm your KhabarLagbe assistant. How can I help you today?", false)
        )
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val userMessage = ChatMessage("user_${System.currentTimeMillis()}", content, true)
            _chatMessages.value = _chatMessages.value + userMessage
            
            _isTyping.value = true
            kotlinx.coroutines.delay(1000)
            _isTyping.value = false
            
            val response = generateResponse(content)
            val botMessage = ChatMessage("bot_${System.currentTimeMillis()}", response, false)
            _chatMessages.value = _chatMessages.value + botMessage
        }
    }

    private fun generateResponse(input: String): String {
        val lowercaseInput = input.lowercase()
        return when {
            lowercaseInput.contains("track") || lowercaseInput.contains("order status") -> 
                "To track your order, go to Order History and tap on your active order. You'll see real-time updates!"
            lowercaseInput.contains("cancel") -> 
                "You can cancel your order within 5 minutes. Go to Order Details and tap the Cancel button."
            lowercaseInput.contains("refund") -> 
                "Refunds are processed within 3-5 business days to your original payment method."
            lowercaseInput.contains("delivery time") || lowercaseInput.contains("how long") -> 
                "Average delivery time is 30-45 minutes. Track your order for accurate ETA!"
            lowercaseInput.contains("promo") || lowercaseInput.contains("discount") -> 
                "Check the Rewards section for available offers. Use WELCOME20 for 20% off first order!"
            else -> 
                "I understand you need help. For complex issues, please use Report Issue or call us at 16205."
        }
    }

    fun submitIssue(orderId: String, issueType: String, description: String) {
        viewModelScope.launch {
            // Simulate API call
            kotlinx.coroutines.delay(500)
            // Issue submitted
        }
    }
}
