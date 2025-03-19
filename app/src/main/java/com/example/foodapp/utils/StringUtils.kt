package com.example.foodapp.utils

object StringUtils {

    fun formatCurrency(value: Float): String {
        val currencyFormatter = java.text.NumberFormat.getCurrencyInstance()
        currencyFormatter.currency = java.util.Currency.getInstance("VND")
        return currencyFormatter.format(value)
    }
}

