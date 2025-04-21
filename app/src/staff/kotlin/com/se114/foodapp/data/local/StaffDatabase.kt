package com.se114.foodapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodapp.data.entities.OrderEntity
import com.example.foodapp.data.entities.OrderRemoteKeys
import com.example.foodapp.data.local.dao.OrderDao
import com.example.foodapp.data.local.dao.OrderRemoteKeysDao
import com.example.foodapp.data.model.Staff
import com.example.foodapp.utils.Converters

@Database(entities = [OrderEntity::class, OrderRemoteKeys::class], version = 1)
@TypeConverters(Converters::class)
abstract class StaffDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun orderRemoteKesDao(): OrderRemoteKeysDao
}