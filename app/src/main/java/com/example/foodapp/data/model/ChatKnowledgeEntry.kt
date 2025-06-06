package com.example.foodapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatKnowledgeEntry(
    val id: Long,
    val title: String,
    val content: String
)
