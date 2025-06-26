package com.example.foodapp.domain.use_case.food

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Menu
import com.example.foodapp.domain.repository.MenuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetMenusUseCase @Inject constructor(
    private val menuRepository: MenuRepository
) {
    operator fun invoke(status: Boolean?= null, name: String? = null)= flow<ApiResponse<List<Menu>>> {
        try {
            menuRepository.getMenus(status, name).collect{
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi lấy menu", 999))
        }
    }.flowOn(Dispatchers.IO)

}