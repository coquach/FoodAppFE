package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.remote.FoodApi

import com.example.foodapp.data.model.Staff
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.example.foodapp.data.paging.StaffPagingSource

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StaffRepository @Inject constructor(
    private val foodApi: FoodApi,
) {

    fun getAllStaffs(): Flow<PagingData<Staff>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true),
            pagingSourceFactory = {
                StaffPagingSource(foodApi = foodApi)
            }
        ).flow
    }


}