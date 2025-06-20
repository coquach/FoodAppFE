package com.example.foodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodapp.data.model.enums.Gender
import com.example.foodapp.utils.Constants.STAFF_TABLE
import com.example.foodapp.utils.json_format.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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

    val basicSalary: Double = 0.0,
    val salaryHistories: List<SalaryHistory> = emptyList(),
    )