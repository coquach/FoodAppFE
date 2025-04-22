package com.example.foodapp.utils

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object StringUtils {

    fun formatCurrency(value: BigDecimal): String {
        val vietnamLocale = java.util.Locale("vi", "VN")
        val currencyFormatter = java.text.NumberFormat.getCurrencyInstance(vietnamLocale).apply {
            currency = java.util.Currency.getInstance("VND")
            maximumFractionDigits = 0
        }
        return currencyFormatter.format(value)
    }
    private val vietnamTimeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

    fun formatDateTime(input: String, inputPattern: String = "yyyy-MM-dd'T'HH:mm:ss'Z'", outputPattern: String = "dd/MM/yyyy HH:mm"): String {
        return try {
            val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val outputFormat = SimpleDateFormat(outputPattern, Locale("vi", "VN")).apply {
                timeZone = vietnamTimeZone
            }
            val date = inputFormat.parse(input) ?: return "Invalid date"
            outputFormat.format(date)
        } catch (e: Exception) {
            "Thời gian không hợp lệ!"
        }
    }

    fun getCurrentDateTime(outputPattern: String = "dd/MM/yyyy HH:mm"): String {
        val dateFormat = SimpleDateFormat(outputPattern, Locale("vi", "VN"))
        dateFormat.timeZone = vietnamTimeZone
        return dateFormat.format(Date())
    }
}

