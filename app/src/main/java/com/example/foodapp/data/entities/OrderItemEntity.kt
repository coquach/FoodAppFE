package com.example.foodapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.utils.Constants.ORDER_ITEM_TABLE
import com.example.foodapp.utils.json_format.BigDecimalSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Entity(tableName = ORDER_ITEM_TABLE)
class OrderItemEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long?= null,
    val menuItemName: String?= null,
    val currentPrice: Double,
    val quantity: Int,

    @SerialName("isDeleted")
    val isDeleted: Boolean
)