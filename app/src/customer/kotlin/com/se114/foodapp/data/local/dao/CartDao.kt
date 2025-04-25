package com.se114.foodapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.se114.foodapp.data.entities.CartItemEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CartDao {
    @Query("SELECT * FROM cart_table")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItems(items: List<CartItemEntity>)

    @Delete
    suspend fun deleteItems(items: List<CartItemEntity>)

    @Query("DELETE FROM cart_table")
    suspend fun clearAll()

    @Query("UPDATE cart_table SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, quantity: Int)
}