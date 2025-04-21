package com.example.foodapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodapp.utils.Constants.MENU_ITEM_REMOTE_KEYS_TABLE

@Entity(tableName = MENU_ITEM_REMOTE_KEYS_TABLE)
data class MenuItemRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prevPage: Int?,
    val nextPage: Int?
)
