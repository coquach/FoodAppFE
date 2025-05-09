package com.se114.foodapp.ui.screen.feedback

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.repository.FeedbackRepository
import com.example.foodapp.ui.navigation.Feedbacks
import com.se114.foodapp.ui.screen.vouchers.VouchersViewModel.VouchersState
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
    private val _uiState = MutableStateFlow<FeedbackListState>(FeedbackListState.Nothing)
    val uiState = _uiState.asStateFlow()

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

    private fun getFeedbacks() {
        viewModelScope.launch {
            delay(100)
            feedbackRepository.getAllFeedbacksByFoodId(_foodId.value!!).collect {
                _feedbacks.value = it
            }
        }
    }

    sealed class FeedbackListState {
        data object Nothing : FeedbackListState()
        data object Loading : FeedbackListState()
        data object Success : FeedbackListState()
        data object Error : FeedbackListState()
    }
}