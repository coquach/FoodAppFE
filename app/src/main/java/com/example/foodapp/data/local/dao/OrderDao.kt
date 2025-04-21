package com.example.foodapp.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.foodapp.data.entities.OrderEntity


@Dao
interface OrderDao {

    @RawQuery(observedEntities = [OrderEntity::class])
    fun getOrdersByFilter(query: SupportSQLiteQuery): PagingSource<Int, OrderEntity>

    @RawQuery(observedEntities = [OrderEntity::class])
    suspend fun deleteOrdersByFilter(query: SupportSQLiteQuery): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOrders(orders: List<OrderEntity>)

}