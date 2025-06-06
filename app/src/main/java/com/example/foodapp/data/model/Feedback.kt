package com.example.foodapp.data.model

import android.net.Uri
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Feedback(
    val id: Long,
    val content: String?,
    val images: List<ImageInfo>? = emptyList(),
    val rating: Int,

    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime?
)


data class FeedbackUi(
    val content: String?= null,
    val images: List<Uri>? = emptyList(),
    val rating: Int = 5,
)