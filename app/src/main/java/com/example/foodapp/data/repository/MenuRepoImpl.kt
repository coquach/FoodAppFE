package com.example.foodapp.data.repository

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.request.MenuRequest
import com.example.foodapp.data.model.Menu
import com.example.foodapp.data.remote.main_api.FoodApi
import com.example.foodapp.domain.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MenuRepoImpl @Inject constructor(
    private val foodApi: FoodApi
): MenuRepository {
    override fun getMenus(status: Boolean?, name: String?): Flow<ApiResponse<List<Menu>>> {
        return apiRequestFlow {
            foodApi.getMenus(status = status, name = name)
        }
    }


    override fun addMenu(request: MenuRequest): Flow<ApiResponse<Menu>> {
        return apiRequestFlow {
            foodApi.createMenu(request)
        }
    }

    override fun updateMenu(
        menuId: Int,
        request: MenuRequest,
    ): Flow<ApiResponse<Menu>> {
       return apiRequestFlow {
           foodApi.updateMenu(menuId, request)
       }
    }

    override fun updateMenuStatus(
        menuId: Int,
    ): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            foodApi.updateMenuStatus(menuId)
        }
    }


}