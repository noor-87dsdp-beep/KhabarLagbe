package com.noor.khabarlagbe.util

import java.text.DecimalFormat

object BDTFormatter {
    private val decimalFormat = DecimalFormat("#,##0.00")
    
    /**
     * Formats amount to BDT currency format
     * Example: 1250.50 -> "৳ 1,250.50"
     */
    fun format(amount: Double): String {
        return "${Constants.CURRENCY_SYMBOL} ${decimalFormat.format(amount)}"
    }
    
    /**
     * Formats amount to BDT currency format without decimal places
     * Example: 1250.50 -> "৳ 1,251"
     */
    fun formatNoDecimal(amount: Double): String {
        return "${Constants.CURRENCY_SYMBOL} ${amount.toInt()}"
    }
    
    /**
     * Formats amount with Bangla numerals
     * Example: 1250.50 -> "৳ ১,২৫০.৫০"
     */
    fun formatBangla(amount: Double): String {
        val formatted = decimalFormat.format(amount)
        return "${Constants.CURRENCY_SYMBOL} ${toBanglaNumber(formatted)}"
    }
    
    /**
     * Converts English numerals to Bangla numerals
     */
    private fun toBanglaNumber(englishNumber: String): String {
        val banglaDigits = mapOf(
            '0' to '০', '1' to '১', '2' to '২', '3' to '৩', '4' to '৪',
            '5' to '৫', '6' to '৬', '7' to '৭', '8' to '৮', '9' to '৯'
        )
        
        return englishNumber.map { char ->
            banglaDigits[char] ?: char
        }.joinToString("")
    }
}
