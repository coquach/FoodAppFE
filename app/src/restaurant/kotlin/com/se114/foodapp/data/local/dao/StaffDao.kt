package com.se114.foodapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.se114.foodapp.data.model.Staff

@Dao
interface StaffDao {

    @Query("SELECT * FROM staff_table")
    fun getAllStaffs(): PagingSource<Int, Staff>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStaffs(staffs: List<Staff>)

    @Query("DELETE FROM staff_table")
    suspend fun deleteAllStaffs()

}