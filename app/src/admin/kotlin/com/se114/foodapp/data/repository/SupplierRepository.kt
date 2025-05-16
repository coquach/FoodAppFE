package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.filter.SupplierFilter
import com.example.foodapp.data.model.Supplier
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.data.paging.SupplierPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupplierRepository @Inject constructor(
    private val foodApi: FoodApi,
) {

    fun getAllSuppliers(filter: SupplierFilter): Flow<PagingData<Supplier>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true),
            pagingSourceFactory = {
                SupplierPagingSource(foodApi = foodApi, filter = filter)
            }
        ).flow
    }



}