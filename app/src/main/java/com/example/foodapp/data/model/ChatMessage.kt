package com.example.foodapp.data.model

data class ChatMessage(
    val id: Long?= null,
    val content: String,
    val sender: String,
    val createdAt: String?=null,
)
