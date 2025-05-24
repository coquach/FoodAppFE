package com.example.foodapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.response.PageResponse

import com.example.foodapp.data.model.Staff

import com.example.foodapp.data.remote.main_api.StaffApi
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.first


import java.io.IOException
import javax.inject.Inject


class StaffPagingSource @Inject constructor(
    private val staffApi: StaffApi,
) : ApiPagingSource<Staff>() {

    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Staff>>> {
        return apiRequestFlow {
            staffApi.getStaffs(
                page = page, size = size
            )
        }
    }
}