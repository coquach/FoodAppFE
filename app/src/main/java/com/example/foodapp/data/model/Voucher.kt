package com.example.foodapp.data.model

import android.os.Parcelable
import com.example.foodapp.data.model.enums.VoucherType
import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
data class Voucher(
    val id: Long,
    val code: String,
    val value: Double,
    val minOrderPrice: BigDecimal,
    val maxValue: BigDecimal,
    val quantity: Int,
    val type: String,
    val startDate: LocalDate,
    val endDate: LocalDate,

    val expired: Boolean,

) : Parcelable
