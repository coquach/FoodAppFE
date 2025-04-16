package com.se114.foodapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.se114.foodapp.data.model.Staff
import com.se114.foodapp.data.model.StaffRemoteKeys

@Dao
interface StaffRemoteKeysDao {

    @Query("SELECT * FROM staff_remote_keys_table WHERE id =:id")
    suspend fun getRemoteKeys(id: String): StaffRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<StaffRemoteKeys>)

    @Query("DELETE FROM staff_remote_keys_table")
    suspend fun deleteAllRemoteKeys()

}