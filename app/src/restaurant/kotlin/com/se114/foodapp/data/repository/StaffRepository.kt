package com.se114.foodapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.remote.FoodApi
import com.se114.foodapp.data.local.AdminDatabase
import com.example.foodapp.data.model.Staff
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.data.paging.StaffRemoteMediator

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalPagingApi
@Singleton
class StaffRepository @Inject constructor(
    private val foodApi: FoodApi,
    private val adminDatabase: AdminDatabase
) {

    fun getAllStaffs(): Flow<PagingData<Staff>> {
        val pagingSourceFactory = { adminDatabase.staffDao().getAllStaffs() }
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            remoteMediator = StaffRemoteMediator(
                foodApi = foodApi,
                adminDatabase = adminDatabase
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }


}