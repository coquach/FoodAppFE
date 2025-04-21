package com.example.foodapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodapp.utils.Constants.ORDER_REMOTE_KEYS_TABLE

@Entity(tableName = ORDER_REMOTE_KEYS_TABLE)
data class OrderRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prevPage: Int?,
    val nextPage: Int?
)
