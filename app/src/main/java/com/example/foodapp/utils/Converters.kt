package com.example.foodapp.utils

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Converters {
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromDateToString(date: LocalDate?): String? {
        return date?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @TypeConverter
    fun fromStringToDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): Double? {
        return value?.toDouble()
    }

    @TypeConverter
    fun toBigDecimal(value: Double?): BigDecimal? {
        return value?.let { BigDecimal.valueOf(it) }
    }

    private val zoneId = ZoneId.systemDefault()

    @TypeConverter
    fun localDateToInstant(date: LocalDate): Instant {
        return date.atStartOfDay(zoneId).toInstant()
    }
    fun instantToLocalDate(instant: Instant): LocalDate {
        return instant.atZone(zoneId).toLocalDate()
    }
    @TypeConverter
    fun localTimeToInstant(time: LocalTime): Instant {
        return time.atDate(LocalDate.now()).atZone(zoneId).toInstant()
    }

    fun instantToLocalTime(instant: Instant): LocalTime {
        return instant.atZone(zoneId).toLocalTime()
    }
}

