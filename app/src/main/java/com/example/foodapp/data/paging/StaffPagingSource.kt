package com.example.foodapp.data.paging

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.filter.StaffFilter
import com.example.foodapp.data.dto.response.PageResponse
import com.example.foodapp.data.model.Staff
import com.example.foodapp.data.remote.main_api.StaffApi
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
                sortBy = filter.sortBy,
                order = filter.order,
                fullName = filter.fullName,
                gender = filter.gender
            )
        }
    }
}