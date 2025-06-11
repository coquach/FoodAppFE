package com.example.foodapp.domain.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.MenuRequest
import com.example.foodapp.data.model.Menu
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
    fun getMenus(status: Boolean? = null, name: String?): Flow<ApiResponse<List<Menu>>>
    fun addMenu(request: MenuRequest) : Flow<ApiResponse<Menu>>
    fun updateMenu(menuId: Int, request: MenuRequest) : Flow<ApiResponse<Menu>>
    fun updateMenuStatus(menuId: Int) : Flow<ApiResponse<Unit>>

}