package com.se114.foodapp.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import com.example.foodapp.data.entities.MenuItemEntity
import com.se114.foodapp.utils.Constants.CART_TABLE
import java.math.BigDecimal

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity(tableName = CART_TABLE)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long?= null,
    val name: String,
    val menuName: String,
    val quantity: Int,
    val price: BigDecimal,
    val imageUrl: String?= null
)