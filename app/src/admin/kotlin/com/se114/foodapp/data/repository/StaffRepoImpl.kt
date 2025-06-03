package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.model.Staff
import com.se114.foodapp.data.paging.StaffPagingSource
import com.example.foodapp.data.remote.main_api.StaffApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.data.dto.filter.StaffFilter
import com.se114.foodapp.data.dto.request.StaffMultipartRequest
import com.se114.foodapp.domain.repository.StaffRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class StaffRepoImpl @Inject constructor(
    private val staffApi: StaffApi,
) : StaffRepository {
    override fun getStaffs(
        filter: StaffFilter,
    ): Flow<PagingData<Staff>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                StaffPagingSource(staffApi = staffApi, filter = filter)
            }
        ).flow
    }

    override fun createStaff(
        request: Map<String, @JvmSuppressWildcards RequestBody>,
        image: MultipartBody.Part?,
    ): Flow<ApiResponse<Staff>> {
        return apiRequestFlow {
            staffApi.createStaff(request, image)
        }
    }

    override fun updateStaff(
        id: Long,
        request: Map<String, @JvmSuppressWildcards RequestBody>,
        image: MultipartBody.Part?,
    ): Flow<ApiResponse<Staff>> {
        return apiRequestFlow {
            staffApi.updateStaff(id, request, image)
        }
    }

    override fun deleteStaff(id: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            staffApi.deleteStaff(id)
        }
    }
}