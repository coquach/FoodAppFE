package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.LocalTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class Menu(
    @Serializable(with = LocalTimeSerializer::class)
    val createdAt: LocalTime? = null,
    val id: Long?,
    val name: String,
    val menuItems: List<MenuItem> = emptyList(),

    @SerialName("isDeleted")
    val isDeleted: Boolean? =  null
)