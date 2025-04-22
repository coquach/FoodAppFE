package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.LocalTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalTime

@Serializable
data class Menu(
    val id: Long,
    val name: String,
    @SerialName("isDeleted")
    val isDeleted: Boolean= false
)