package com.example.foodapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.foodapp.data.model.MenuItem


@Dao
interface MenuItemDao {

    @RawQuery(observedEntities = [MenuItem::class])
    fun getMenuItemsByFilter(query: SupportSQLiteQuery): PagingSource<Int, MenuItem>

    @RawQuery(observedEntities = [MenuItem::class])
    suspend fun deleteMenuItemsByFilter(query: SupportSQLiteQuery): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMenuItems(menuItems: List<MenuItem>)

}