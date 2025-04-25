package com.se114.foodapp.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import com.se114.foodapp.utils.Constants.CART_TABLE
import java.math.BigDecimal

@Entity(tableName = CART_TABLE)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long?= null,
    val name: String,
    val menuId: Long = 1,
    val menuName: String,
    val quantity: Int,
    val price: BigDecimal,
    val imageUrl: String?= null
)