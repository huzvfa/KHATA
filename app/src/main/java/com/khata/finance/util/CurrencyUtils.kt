package com.khata.finance.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val locale = Locale("en", "PK")

    fun format(amount: Double): String {
        val nf = NumberFormat.getNumberInstance(locale)
        nf.maximumFractionDigits = 0
        nf.minimumFractionDigits = 0
        return "Rs. " + nf.format(amount)
    }

    fun formatWithDecimals(amount: Double): String {
        val nf = NumberFormat.getNumberInstance(locale)
        nf.maximumFractionDigits = 2
        nf.minimumFractionDigits = 2
        return "Rs. " + nf.format(amount)
    }
}
