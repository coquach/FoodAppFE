package com.example.foodapp.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import com.example.foodapp.utils.Constants.MENU_ITEM_TABLE
import java.math.BigDecimal
import java.time.LocalTime


@Entity(tableName = MENU_ITEM_TABLE)
data class MenuItemEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long?= null,
    val createdAt: LocalTime? = null,
    val description: String,
    val menuName: String,
    val imageUrl: String? = null,
    val name: String,
    val price: BigDecimal,
)