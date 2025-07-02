package com.example.foodapp.data.model

import android.net.Uri
import androidx.core.net.toUri
import com.example.foodapp.utils.json_format.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Feedback(
    val id: Long,
    val content: String?=null,
    val displayName: String,
    val avatar: String?=null,
    val images: List<ImageInfo>? = emptyList(),
    val rating: Int,

    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime?
)


data class FeedbackUi(
    val id: Long?=null,
    val content: String?= null,
    val images: List<Uri>? = emptyList(),
    val rating: Int = 5,
)

fun Feedback.toUi() = FeedbackUi(
    id = this.id,
    content = this.content,
    images = this.images?.map { it.url.toUri() } ?: emptyList(),
    rating = this.rating,
)