package com.se114.foodapp.data.paging

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.paging.ApiPagingSource
import com.example.foodapp.data.remote.main_api.StaffApi
import com.se114.foodapp.data.dto.filter.StaffFilter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StaffPagingSource @Inject constructor(
    private val staffApi: StaffApi,
    private val filter: StaffFilter,
) : ApiPagingSource<Staff>() {

    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Staff>>> {
        return apiRequestFlow {
            staffApi.getStaffs(
                page = page,
                size = size,
                fullName = filter.fullName,
                gender = filter.gender
            )
        }
    }
}