package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Import(
    val id: Long? = null,
    var supplier: Supplier,

    var staff: Staff,
    @Serializable(with = LocalDateTimeSerializer::class)
    var importDate: LocalDateTime,
    var isDeleted: Boolean = false,
    var importDetails: MutableList<ImportDetail> = mutableListOf()
)
