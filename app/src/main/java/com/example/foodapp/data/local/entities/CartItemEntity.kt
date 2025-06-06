package com.example.foodapp.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import com.example.foodapp.utils.Constants.CART_TABLE
import java.math.BigDecimal

@Entity(tableName = CART_TABLE)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val name: String,
    val quantity: Int,
    val price: BigDecimal,
    val imageUrl: String?= null
)