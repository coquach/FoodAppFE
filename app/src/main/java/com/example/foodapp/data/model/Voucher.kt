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
    val id: Long? = null,
    val code: String = "",
    val value: Double= 0.0,
    val minOrderPrice: BigDecimal = BigDecimal.ZERO,
    val maxValue: BigDecimal = BigDecimal.ZERO,
    val quantity: Int = 1,
    val type: String = VoucherType.PERCENTAGE.name,
    val startDate: LocalDate?= null,
    val endDate: LocalDate? = null,
    val expired: Boolean = false,

) : Parcelable
