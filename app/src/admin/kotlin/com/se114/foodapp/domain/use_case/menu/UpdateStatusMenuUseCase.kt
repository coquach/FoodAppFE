package com.se114.foodapp.domain.use_case.menu

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.domain.repository.MenuRepository
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UpdateStatusMenuUseCase @Inject constructor(
    private val menuRepository: MenuRepository,
) {
    operator fun invoke(menuId: Int) = flow<ApiResponse<Unit>> {
        try {
             menuRepository.updateMenuStatus(menuId).collect { emit(it) }
        } catch (e: Exception) {
            e.printStackTrace()
           emit(ApiResponse.Failure(e.message ?: "Lỗi không xác định", 999))
        }
    }.flowOn(Dispatchers.IO)
}