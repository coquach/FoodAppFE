package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SalaryHistory(
    val id: Long? = null,
    val staffId: Long,
    val month: Int = 1,
    val year: Int = 2024,
    val currentSalary: Double = 0.0,
    val isDeleted: Boolean = false
)
