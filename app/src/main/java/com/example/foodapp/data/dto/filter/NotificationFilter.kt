package com.example.foodapp.data.dto.filter

data class NotificationFilter(
    val userId: String,
    val isRead: Boolean?= null,
)
