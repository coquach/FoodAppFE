package com.example.foodapp.data.model


import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class CartItem(
    val id: Long? = null,
    val name: String,
    val quantity: Int,
    val menuId: Long,
    val menuName: String,
    val imageUrl: String?= null,
    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal
)
