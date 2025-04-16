package com.se114.foodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.se114.foodapp.utils.Constants.STAFF_REMOTE_KEYS_TABLE

@Entity(tableName = STAFF_REMOTE_KEYS_TABLE)
data class StaffRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prevPage: Int?,
    val nextPage: Int?
)
