package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.model.Inventory
import com.example.foodapp.data.dto.filter.InventoryFilter
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getInventoriesByFilter(filter: InventoryFilter): Flow<PagingData<Inventory>>
}