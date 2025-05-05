package com.example.foodapp.utils

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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

    fun formatDateTime(
        input: LocalDateTime?,
        outputPattern: String = "dd/MM/yyyy HH:mm:ss"
    ): String? {
        return try {
            input?.let {
                val outputFormatter = DateTimeFormatter.ofPattern(outputPattern).withLocale(Locale("vi", "VN"))
                outputFormatter.format(it.atZone(ZoneOffset.UTC).toLocalDateTime())
            }
        } catch (e: Exception) {
            null
        }
    }

    fun formatLocalDate(
        date: LocalDate?,
        pattern: String = "dd-MM-yyyy"
    ): String? {
        return try {
            date?.format(DateTimeFormatter.ofPattern(pattern))
        } catch (e: Exception) {
            null
        }
    }

    fun getFormattedCurrentVietnamDate(pattern: String = "dd-MM-yyyy"): String {
        return try {
            val currentDate = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"))
            val formatter = DateTimeFormatter.ofPattern(pattern)
            currentDate.format(formatter)
        } catch (e: Exception) {
            "Ngày không hợp lệ!"
        }
    }

    fun getCurrentVietnamLocalTime(): String {
        val timeNow = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return timeNow.format(formatter)
    }

    fun getFormattedCurrentVietnamDateTime(pattern: String = "dd-MM-yyyy HH:mm:ss"): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).format(formatter)
        } catch (e: Exception) {
            "Thời gian không hợp lệ!"
        }
    }


    fun parseLocalDate(input: String): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            LocalDate.parse(input, formatter)
        } catch (e: Exception) {
            null
        }
    }
}

