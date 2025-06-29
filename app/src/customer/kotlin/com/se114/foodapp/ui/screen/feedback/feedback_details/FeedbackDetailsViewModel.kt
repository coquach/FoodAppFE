package com.se114.foodapp.ui.screen.feedback.feedback_details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute

import com.example.foodapp.data.dto.ApiResponse
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.data.model.FeedbackUi
import com.example.foodapp.data.model.toUi
import com.example.foodapp.domain.use_case.auth.GetUserIdUseCase
import com.example.foodapp.navigation.FeedbackDetails

import com.se114.foodapp.domain.use_case.feedback.CreateFeedbackUseCase
import com.se114.foodapp.domain.use_case.feedback.DeleteFeedbackUseCase
import com.se114.foodapp.domain.use_case.feedback.GetFeedbackByOrderItemIdUseCase
import com.se114.foodapp.domain.use_case.feedback.UpdateFeedbackUseCase
import com.se114.foodapp.ui.screen.feedback.feedback_details.FeedbackDetail.GetFeedbackState
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackDetailsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val createFeedbackUseCase: CreateFeedbackUseCase,
    private val getFeedbackByOrderItemIdUseCase: GetFeedbackByOrderItemIdUseCase,
    private val updateFeedbackUseCase: UpdateFeedbackUseCase,
    private val deleteFeedbackUseCase: DeleteFeedbackUseCase,
) : ViewModel() {

    private val orderItemId = savedStateHandle.toRoute<FeedbackDetails>().orderItemId

    private val _uiState = MutableStateFlow(FeedbackDetail.UiState())
    val uiState: StateFlow<FeedbackDetail.UiState> get() = _uiState.asStateFlow()

    private val _event = Channel<FeedbackDetail.Event>()
    val event = _event.receiveAsFlow()

    fun getFeedback() {
        viewModelScope.launch {
            getFeedbackByOrderItemIdUseCase(orderItemId).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _uiState.update { it.copy(feedback = result.data.toUi(), feedbackState = GetFeedbackState.Success, isUpdating = true) }}
                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(feedbackState = GetFeedbackState.Error(result.errorMessage), feedback = FeedbackUi()) }
                    }
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(feedbackState = GetFeedbackState.Loading) }
                    }
                }
            }
        }
    }

    private fun createFeedback() {
        viewModelScope.launch {
            createFeedbackUseCase(orderItemId, uiState.value.feedback).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _event.send(FeedbackDetail.Event.BackToAfterFeedback("Tạo đánh giá thành công"))
                    }

                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(error = result.errorMessage) }
                        _event.send(FeedbackDetail.Event.ShowError)
                    }

                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    private fun updateFeedback() {
        viewModelScope.launch {
            updateFeedbackUseCase(orderItemId, uiState.value.feedback).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _event.send(FeedbackDetail.Event.BackToAfterFeedback("Cập nhật đánh giá thành công"))
                    }
                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(error = result.errorMessage) }
                        _event.send(FeedbackDetail.Event.ShowError)
                    }
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }
    private fun deleteFeedback() {
        viewModelScope.launch {
            deleteFeedbackUseCase(_uiState.value.feedback.id!!).collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        _event.send(FeedbackDetail.Event.BackToAfterFeedback("Xóa đánh giá thành công"))
                    }
                    is ApiResponse.Failure -> {
                        _uiState.update { it.copy(error = result.errorMessage) }
                        _event.send(FeedbackDetail.Event.ShowError)
                    }
                    is ApiResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    fun onAction(action: FeedbackDetail.Action) {
        when (action) {
            is FeedbackDetail.Action.OnFeedbackClicked -> {
                createFeedback()
            }

            is FeedbackDetail.Action.OnRatingChanged -> {
                _uiState.update { it.copy(feedback = it.feedback.copy(rating = action.rating)) }
            }

            is FeedbackDetail.Action.OnContentChanged -> {
                _uiState.update { it.copy(feedback = it.feedback.copy(content = action.content)) }
            }

            is FeedbackDetail.Action.OnImagesChanged -> {
                _uiState.update { it.copy(feedback = it.feedback.copy(images = action.images)) }
            }

            is FeedbackDetail.Action.OnBack -> {
                viewModelScope.launch {
                    _event.send(FeedbackDetail.Event.OnBack)
                }
            }

            FeedbackDetail.Action.OnDelete -> {
                deleteFeedback()
            }
            FeedbackDetail.Action.OnUpdate -> {
                updateFeedback()
            }
        }
    }

}

object FeedbackDetail {
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val feedback: FeedbackUi = FeedbackUi(),
        val feedbackState: GetFeedbackState = GetFeedbackState.Loading,
        val isUpdating: Boolean = false
    )

    sealed interface GetFeedbackState{
        data object Success: GetFeedbackState
        data class Error(val errorMessage: String): GetFeedbackState
        data object Loading: GetFeedbackState
    }

    sealed interface Event {
        data class BackToAfterFeedback(val message: String) : Event
        data object ShowError : Event
        data object OnBack : Event

    }

    sealed interface Action {
        data object OnFeedbackClicked : Action
        data class OnRatingChanged(val rating: Int) : Action
        data class OnContentChanged(val content: String) : Action
        data class OnImagesChanged(val images: List<Uri>?) : Action
        data object OnBack : Action
        data object OnDelete : Action
        data object OnUpdate : Action


    }
}