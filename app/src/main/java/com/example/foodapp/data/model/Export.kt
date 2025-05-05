package com.example.foodapp.data.model

import com.example.foodapp.utils.gson.LocalDateDeserializer
import com.example.foodapp.utils.json_format.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class Export(
    val id: Long,
    val staffId: Long,
    val staffName: String,
    @Serializable(with = LocalDateSerializer::class)
    val exportDate: LocalDate,
    val exportDetails: List<ExportDetail>,
)
