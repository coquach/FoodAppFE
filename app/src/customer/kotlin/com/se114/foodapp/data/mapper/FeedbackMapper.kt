package com.se114.foodapp.data.mapper

import androidx.core.net.toUri
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.model.FeedbackUi

fun Feedback.toFeedbackUi() = FeedbackUi(
    content = this.content,
    images = this.images?.map { it.url.toUri() },
    rating = this.rating
)