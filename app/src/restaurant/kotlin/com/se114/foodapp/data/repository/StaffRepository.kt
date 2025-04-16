package com.se114.foodapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.remote.FoodApi
import com.se114.foodapp.data.local.AdminDatabase
import com.se114.foodapp.data.model.Staff
import com.se114.foodapp.data.paging.StaffRemoteMediator
import com.se114.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
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