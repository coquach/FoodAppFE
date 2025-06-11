package com.example.foodapp.data.model

import com.example.foodapp.data.model.enums.Gender
import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class Staff(
    val id: Long?= null,
    val fullName: String = "",
    val position: String? = null,
    val phone: String = "",
    val gender: String = Gender.MALE.name,
    val address: String = "",
    val avatar: ImageInfo? = null,

    @Serializable(with = LocalDateSerializer::class)
    val birthDate: LocalDate? = null,

    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate? = null,

    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate? = null,

    @Serializable(with = BigDecimalSerializer::class)
    val basicSalary: BigDecimal = BigDecimal.ZERO,
    val salaryHistories: List<SalaryHistory> = emptyList(),
    )