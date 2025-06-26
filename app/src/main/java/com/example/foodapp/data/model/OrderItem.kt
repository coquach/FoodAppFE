package com.example.foodapp.data.model

import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class OrderItem(
    val id: Long,
    val foodName: String,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val quantity: Int,
    val foodImages: List<ImageInfo>

)