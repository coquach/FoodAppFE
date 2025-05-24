package com.se114.foodapp.domain.use_case.feedback

import com.example.foodapp.data.remote.main_api.FeedbackApi
import com.se114.foodapp.domain.repository.FeedbackRepository
import javax.inject.Inject

class GetFeedbacksUseCase @Inject constructor(
    private val feedbackRepository: FeedbackRepository
) {
    operator fun invoke(foodId: Long) = feedbackRepository.getFeedbacks(foodId)
}