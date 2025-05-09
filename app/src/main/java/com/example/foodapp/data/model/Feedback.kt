package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Feedback(
    val id: Long,
    val content: String?,
    val imageUrls: List<String>?,
    val rating: Int,

    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime?
)
