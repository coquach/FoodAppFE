package com.se114.foodapp.domain.use_case.feedback

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.domain.repository.FeedbackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DeleteFeedbackUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,

) {

    operator fun invoke(id: Long) = flow<ApiResponse<Unit>> {
        try {
            feedbackRepository.deleteFeedback(id).collect {
                emit(it)
            }
        }catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã có lỗi xảy ra khi xóa đánh giá", 401))
        }
    }.flowOn(Dispatchers.IO)
}