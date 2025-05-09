package com.se114.foodapp.ui.screen.feedback.feedback_details

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.request.FeedbackMultipartRequest
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.repository.FeedbackRepository
import com.example.foodapp.data.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val accountService: AccountService,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<FeedbackDetailsState>(FeedbackDetailsState.Nothing)
    val uiState = _uiState.asStateFlow()


    private val _feedbackRequest = MutableStateFlow(
        FeedbackMultipartRequest(
            menuItemId = null,
            content = null,
            rating = 5
        )
    )
    val feedbackRequest = _feedbackRequest.asStateFlow()
    private val _foodId = MutableStateFlow<Long?>(null)
    
    
    private val _imageList = MutableStateFlow<List<Uri>>(emptyList())
    val imageList = _imageList.asStateFlow()

    fun onUpdateImageList(imageList: List<Uri>?){
       _imageList.value = imageList ?: emptyList()
    }


    fun createFeedback(foodId: Long) {
        viewModelScope.launch {
            _uiState.value = FeedbackDetailsState.Loading
            try {
                _feedbackRequest.update { it.copy(menuItemId = foodId) }
                val request =_feedbackRequest.value
                val partMap = request.toPartMap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    sealed class FeedbackDetailsState {
        data object Nothing : FeedbackDetailsState()
        data object Loading : FeedbackDetailsState()
        data object Success : FeedbackDetailsState()
        data object Error : FeedbackDetailsState()
    }

    sealed class FeedbackDetailsEvents {
        data object BackToFeedbackList : FeedbackDetailsEvents()

    }
}