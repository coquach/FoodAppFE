package com.se114.foodapp.domain.use_case.feedback

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Feedback
import com.se114.foodapp.domain.repository.FeedbackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetFeedbackByOrderItemIdUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) {
    operator fun invoke(orderItemId: Long) = flow<ApiResponse<Feedback>> {
        try {
            feedbackRepository.getFeedbackByOrderItemId(orderItemId).collect {
                emit(it)
            }
        }catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure("Đã có lỗi xảy ra khi lấy đánh giá", 401))
        }
    }.flowOn(Dispatchers.IO)
}