package com.se114.foodapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodapp.data.local.daos.CartDao
import com.example.foodapp.data.local.entities.CartItemEntity
import com.example.foodapp.data.model.Staff
import com.example.foodapp.utils.Converters

@Database(entities = [CartItemEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class StaffDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}