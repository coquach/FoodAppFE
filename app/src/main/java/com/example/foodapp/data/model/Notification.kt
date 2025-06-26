package com.example.foodapp.data.model

import java.time.LocalDateTime

data class Notification(
    val createdAt: LocalDateTime,
    val id: Long,
    val read: Boolean,
    val body: String,
    val title: String,
    val userId: String
)
