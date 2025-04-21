package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalTimeSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalTime


@Serializable
data class MenuItem(
    @Serializable(with = LocalTimeSerializer::class)
    val createdAt: LocalTime?= null,
    val description: String,
    val id: Long? = null,
    val menuName: String,
    val imageUrl: String? = null,
    val name: String,

    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
)