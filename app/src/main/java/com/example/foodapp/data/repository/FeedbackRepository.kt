package com.example.foodapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.foodapp.data.model.Feedback

import com.example.foodapp.data.paging.FeedbackPagingSource

import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.utils.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedbackRepository @Inject constructor(
    private val foodApi: FoodApi,
) {

    fun getAllFeedbacksByFoodId(foodId: Long): Flow<PagingData<Feedback>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE,
                initialLoadSize = ITEMS_PER_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true),
            pagingSourceFactory = {
                FeedbackPagingSource(foodApi = foodApi, foodId = foodId)
            }
        ).flow
    }


}