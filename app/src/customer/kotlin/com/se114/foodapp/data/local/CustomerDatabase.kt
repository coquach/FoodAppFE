package com.se114.foodapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodapp.utils.Converters

import com.example.foodapp.data.local.entities.CartItemEntity
import com.example.foodapp.data.local.daos.CartDao


@Database(entities = [CartItemEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CustomerDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}