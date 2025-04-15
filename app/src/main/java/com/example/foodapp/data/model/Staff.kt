package com.example.foodapp.data.model


import com.example.foodapp.utils.json_format.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Staff(
    val id: Long? = null,
    val fullName: String? = null,
    val position: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val imageUrl: String? = null,

    @Serializable(with = LocalDateSerializer::class)
    val birthDate: LocalDate? = null,

    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate? = null,

    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate? = null,

    val basicSalary: Double = 0.0,

    val isDeleted: Boolean = false,


    val salaryHistories: List<SalaryHistory> = emptyList()

)
