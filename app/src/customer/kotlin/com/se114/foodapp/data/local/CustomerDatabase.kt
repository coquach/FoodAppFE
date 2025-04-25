package com.se114.foodapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodapp.utils.Converters

import com.se114.foodapp.data.entities.CartItemEntity
import com.se114.foodapp.data.local.dao.CartDao


@Database(entities = [CartItemEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class CustomerDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}