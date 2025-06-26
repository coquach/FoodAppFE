package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class SalaryHistory(
    val month: Int,
    val year: Int ,
    @Serializable(with = BigDecimalSerializer::class)
    val currentSalary: BigDecimal,
)
