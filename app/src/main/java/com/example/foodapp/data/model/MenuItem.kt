package com.example.foodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodapp.utils.Constants.MENU_ITEM_TABLE
import com.example.foodapp.utils.json_format.BigDecimalSerializer
import com.example.foodapp.utils.json_format.LocalTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalTime


@Serializable
data class MenuItem(
    val id: Long,
    val description: String,
    val menuName: String,
    val imageUrl: String? = null,
    val name: String,

    @Serializable(with = BigDecimalSerializer::class)
    val price: BigDecimal,
    val defaultQuantity: Int,
    val remainingQuantity: Int,
    val active: Boolean,
)