package com.example.foodapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.example.foodapp.utils.Constants.MENU_TABLE
import java.time.LocalTime


@Entity(tableName = MENU_TABLE)
data class MenuEntity (
    @PrimaryKey(autoGenerate = false)
    val id: Long?= null,
    val createdAt: LocalTime? = null,
    val name: String,
    val isDeleted: Boolean? =  null
)