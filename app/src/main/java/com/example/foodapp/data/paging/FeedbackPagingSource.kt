package com.example.foodapp.data.paging


import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.dto.response.PageResponse

import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.remote.main_api.FeedbackApi

import com.example.foodapp.data.remote.main_api.FoodApi
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.first

import javax.inject.Inject


class FeedbackPagingSource @Inject constructor(
    private val feedbackApi: FeedbackApi,
    private val foodId: Long,
) : ApiPagingSource<Feedback>() {
    override suspend fun fetch(
        page: Int,
        size: Int,
    ): Flow<ApiResponse<PageResponse<Feedback>>> {
        return apiRequestFlow {
            feedbackApi.getFeedbacksByFoodId(page = page, size = size, foodId = foodId)
        }
    }
}