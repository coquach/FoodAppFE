package com.se114.foodapp.ui.screen.feedback

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.foodapp.data.model.Feedback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackListViewModel @Inject constructor(
    private val feedbackRepository: FeedbackRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(com.se114.foodapp.ui.screen.feedback.Feedback.UiState())
    val uiState : StateFlow<com.se114.foodapp.ui.screen.feedback.Feedback.UiState> get() = _uiState.asStateFlow()

    private val _feedbacks = MutableStateFlow<PagingData<Feedback>>(PagingData.empty())
    val feedbacks: StateFlow<PagingData<Feedback>> = _feedbacks


    private val _foodId = MutableStateFlow<Long?>(null)
    val foodId = _foodId.asStateFlow()

    fun setUpFoodId(foodId: Long) {
        _foodId.value = foodId
    }

    init {
        getFeedbacks()

    }

//    fun getFeedbacks() {
//        viewModelScope.launch {
//            delay(100)
//            feedbackRepository.getAllFeedbacksByFoodId(_foodId.value!!).cachedIn(viewModelScope).collect {
//                _feedbacks.value = it
//            }
//        }
//    }


}

object Feedback{
    data class UiState(
        val isLoading: Boolean = false,
        val error: String?= null,
    )

}