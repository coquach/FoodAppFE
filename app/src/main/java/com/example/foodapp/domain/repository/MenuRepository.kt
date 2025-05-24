package com.example.foodapp.domain.repository

import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.MenuRequest
import com.example.foodapp.data.model.Menu
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
    fun getMenu(): Flow<PagingData<Menu>>
    fun addMenu(request: MenuRequest) : Flow<ApiResponse<Menu>>
    fun updateMenu(menuId: Long, request: MenuRequest) : Flow<ApiResponse<Menu>>
    fun updateMenuStatus(menuId: Long, status: Boolean) : Flow<ApiResponse<Unit>>

}