package com.se114.foodapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodapp.data.entities.MenuEntity
import com.example.foodapp.data.entities.MenuItemEntity
import com.example.foodapp.data.entities.OrderEntity
import com.example.foodapp.data.entities.OrderRemoteKeys
import com.example.foodapp.data.local.dao.OrderDao
import com.example.foodapp.data.local.dao.OrderRemoteKeysDao
import com.example.foodapp.utils.Converters
import com.se114.foodapp.data.entities.CartItemEntity
import com.se114.foodapp.data.local.dao.CartDao


@Database(entities = [CartItemEntity::class, MenuItemEntity::class, MenuEntity::class, OrderEntity::class, OrderRemoteKeys::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CustomerDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderRemoteKesDao(): OrderRemoteKeysDao
}