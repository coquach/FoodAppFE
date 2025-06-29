package com.se114.foodapp.domain.use_case.feedback

import android.content.Context
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.FeedbackMultipartRequest
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.model.FeedbackUi
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import com.example.foodapp.utils.ImageUtils
import com.se114.foodapp.domain.repository.FeedbackRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CreateFeedbackUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
    private val getUserIdUseCase: GetUserIdUseCase,
    @ApplicationContext val context: Context,
) {
    operator fun invoke(orderItemId: Long, feedback: FeedbackUi) = flow<ApiResponse<Unit>> {
        emit(ApiResponse.Loading)
        try {
            val imageParts = feedback.images?.map { ImageUtils.getImagePart(context, it, "images") }
            val request = FeedbackMultipartRequest(
                orderItemId = orderItemId,
                content = feedback.content,
                rating = feedback.rating,
                customerId = getUserIdUseCase()
            )
            val partMap = request.toPartMap()
            feedbackRepository.createFeedback(partMap, imageParts).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        emit(ApiResponse.Success(Unit))
                    }

                    is ApiResponse.Failure -> {
                        emit(ApiResponse.Failure(result.errorMessage, result.code))
                    }

                    is ApiResponse.Loading -> {
                        emit(ApiResponse.Loading)
                    }
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
            emit(ApiResponse.Failure(e.message ?: "Đã có lỗi xảy ra khi tạo đánh giá", 401))
        }
    }.flowOn(Dispatchers.IO)
}