package com.se114.foodapp.domain.use_case.feedback

import com.example.foodapp.data.dto.ApiResponse
import com.se114.foodapp.domain.repository.FeedbackRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateFeedbackUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) {


}