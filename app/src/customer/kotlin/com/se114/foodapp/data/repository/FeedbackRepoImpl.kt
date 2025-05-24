package com.se114.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.apiRequestFlow
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.paging.FeedbackPagingSource
import com.example.foodapp.data.paging.FoodPagingSource
import com.example.foodapp.data.remote.main_api.FeedbackApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import com.se114.foodapp.domain.repository.FeedbackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class FeedbackRepoImpl @Inject constructor(
    private val feedbackApi: FeedbackApi
) : FeedbackRepository {
    override fun getFeedbacks(foodId: Long): Flow<PagingData<Feedback>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FeedbackPagingSource(feedbackApi = feedbackApi, foodId = foodId)
            }
        ).flow
    }

    override fun createFeedback(
        request: Map<String, @JvmSuppressWildcards RequestBody>,
        images: List<MultipartBody.Part>?,
    ): Flow<ApiResponse<Feedback>> {
        return apiRequestFlow {
            feedbackApi.createFeedback(
                request = request,
                images = images
            )
        }
    }

    override fun updateFeedback(
        id: Long,
        request: Map<String, @JvmSuppressWildcards RequestBody>,
        images: List<MultipartBody.Part>?,
    ): Flow<ApiResponse<Feedback>> {
        return apiRequestFlow {
            feedbackApi.updateFeedback(
                id = id,
                request = request,
                images = images
            )
        }
    }

    override fun deleteFeedback(id: Long): Flow<ApiResponse<Unit>> {
        return apiRequestFlow {
            feedbackApi.deleteFeedback(
                id = id
            )
        }
    }

}