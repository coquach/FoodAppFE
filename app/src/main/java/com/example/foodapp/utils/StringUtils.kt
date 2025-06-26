package com.example.foodapp.utils

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
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

    fun parseLocalDate(
        dateString: String?,
        pattern: String = "dd-MM-yyyy"
    ): LocalDate? {
        return try {
            dateString?.let {
                LocalDate.parse(it, DateTimeFormatter.ofPattern(pattern))
            }
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
    private const val SECONDS_IN_MINUTE = 60
    private const val SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE
    fun formatDistance(meters: Double): String {
        return when {
            meters < 1000 -> "${meters.toInt()} m"
            else -> {
                // Using String.format with Locale for consistent decimal formatting
                // across different user locales.
                String.format(Locale.US, "%.2f km", meters / 1000)
                // Alternatively, for simpler cases and default locale:
                // "%.2f km".format(meters / 1000)
            }
        }
    }
    fun formatDurationDetailed(totalSecondsInput: Double): String {
        val totalSeconds = totalSecondsInput.toInt()

        if (totalSeconds < 0) return "0s"
        if (totalSeconds == 0) return "0s"


        val hours = totalSeconds / SECONDS_IN_HOUR
        val minutes = (totalSeconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
        val secs = totalSeconds % SECONDS_IN_MINUTE

        val parts = mutableListOf<String>()
        if (hours > 0) {
            parts.add("${hours}h")
        }
        if (minutes > 0 || hours > 0) { // Show minutes if hours are present, even if minutes are 0
            // Use %02d for minutes only if hours are also present or it's the primary unit
            val minuteFormat = if (hours > 0) "%02dm" else "%dm"
            parts.add(String.format(Locale.US, minuteFormat, minutes))
        }
        // Show seconds if it's the only unit, or if minutes/hours are present
        if (secs > 0 || parts.isEmpty()) {
            val secondFormat = if (parts.isNotEmpty()) "%02ds" else "%ds"
            parts.add(String.format(Locale.US, secondFormat, secs))
        }

        return parts.joinToString(" ")
    }

}

