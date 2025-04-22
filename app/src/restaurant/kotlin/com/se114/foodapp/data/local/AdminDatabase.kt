package com.se114.foodapp.data.local


import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverters
import com.example.foodapp.data.entities.MenuItemRemoteKeys
import com.example.foodapp.data.local.dao.MenuItemDao
import com.example.foodapp.data.local.dao.MenuItemRemoteKeysDao
import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.utils.Converters
import com.se114.foodapp.data.local.dao.StaffDao
import com.se114.foodapp.data.local.dao.StaffRemoteKeysDao
import com.example.foodapp.data.model.Staff

import com.se114.foodapp.data.model.StaffRemoteKeys

@Database(entities = [Staff::class, StaffRemoteKeys::class, MenuItem::class, MenuItemRemoteKeys::class], version = 1)
@TypeConverters(Converters::class)
abstract class AdminDatabase : RoomDatabase() {
    abstract fun staffDao(): StaffDao
    abstract fun staffRemoteKeysDao(): StaffRemoteKeysDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun menuItemRemoteKeysDao(): MenuItemRemoteKeysDao
}