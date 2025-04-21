package com.example.foodapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodapp.data.entities.OrderRemoteKeys

@Dao
interface OrderRemoteKeysDao {

    @Query("SELECT * FROM order_remote_keys_table WHERE id =:id")
    suspend fun getRemoteKeys(id: String): OrderRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<OrderRemoteKeys>)

    @Query("DELETE FROM order_remote_keys_table")
    suspend fun deleteAllRemoteKeys()

}