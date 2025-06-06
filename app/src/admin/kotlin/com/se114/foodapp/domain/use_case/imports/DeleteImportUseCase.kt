package com.se114.foodapp.domain.use_case.imports

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.domain.repository.ImportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteImportUseCase @Inject constructor(
    private val importRepository: ImportRepository
) {
    operator fun invoke(id: Long) = flow<ApiResponse<Unit>> {
        try {
            importRepository.deleteImport(id).collect{
                emit(it)
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã xảy ra lỗi khi xóa đơn nhập hàng", 999))
        }
    }.flowOn(Dispatchers.IO)
}