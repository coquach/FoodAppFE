package com.se114.foodapp.ui.screen.feedback.feedback_details

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.foodapp.BaseViewModel
import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.dto.request.FeedbackMultipartRequest
import com.example.foodapp.data.dto.safeApiCall
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.repository.FeedbackRepository
import com.example.foodapp.data.service.AccountService
import com.example.foodapp.utils.ImageUtils
import com.se114.foodapp.ui.screen.vouchers.VouchersViewModel.VouchersState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi,
    private val accountService: AccountService,
    @ApplicationContext val context: Context
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<FeedbackDetailsState>(FeedbackDetailsState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<FeedbackDetailsEvents>()
    val event = _event.receiveAsFlow()


    private val _feedbackRequest = MutableStateFlow(
        FeedbackMultipartRequest(
            foodId = null,
            content = "",
            rating = 5
        )
    )
    val feedbackRequest = _feedbackRequest.asStateFlow()

    fun onContentChange(content: String) {
        _feedbackRequest.update { it.copy(content = content) }

    }
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
                _feedbackRequest.update { it.copy(foodId = foodId) }
                val request =_feedbackRequest.value
                val partMap = request.toPartMap()
                val imageListPart = if(_imageList.value.isNotEmpty()) _imageList.value.map {
                    ImageUtils.getImagePart(context, it)!!
                }
                else null


                val response = safeApiCall { foodApi.createFeedback(partMap, imageListPart) }
                when(response){
                    is ApiResponse.Error -> {
                        error = "Lỗi khi tạo đánh giá"
                        errorDescription = response.message
                        _uiState.value = FeedbackDetailsState.Error
                    }
                    is ApiResponse.Exception -> {
                        error = "Lỗi khi tạo đánh giá"
                        errorDescription = response.exception.toString()
                        _uiState.value = FeedbackDetailsState.Error
                    }
                    is ApiResponse.Success -> {
                        _uiState.value = FeedbackDetailsState.Success
                        _event.send(FeedbackDetailsEvents.BackToFeedbackList)
                    }
                }
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