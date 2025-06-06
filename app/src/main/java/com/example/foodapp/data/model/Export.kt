package com.example.foodapp.data.model


import com.example.foodapp.utils.json_format.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Export(
    val id: Long?=null,
    val staffId: Long?=null,
    val staffName: String= "",

    @Serializable(with = LocalDateSerializer::class)
    val exportDate: LocalDate?=null,

    val exportDetails: List<ExportDetail> = emptyList(),
)
