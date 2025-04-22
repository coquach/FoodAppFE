package com.example.foodapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodapp.data.entities.MenuItemRemoteKeys


@Dao
interface MenuItemRemoteKeysDao {

    @Query("SELECT * FROM menu_item_remote_keys_table WHERE id =:id")
    suspend fun getRemoteKeys(id: String): MenuItemRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<MenuItemRemoteKeys>)

    @Query("DELETE FROM menu_item_remote_keys_table")
    suspend fun deleteAllRemoteKeys()

}