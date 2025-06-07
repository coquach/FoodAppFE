package com.se114.foodapp.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.use_case.auth.FirebaseResult
import com.example.foodapp.domain.use_case.auth.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase,


) : ViewModel() {


    private val _uiState = MutableStateFlow(SettingState.UiSate())
    val uiState get() = _uiState.asStateFlow()

    private val _event = Channel<SettingState.Event>()
    val event get() = _event.receiveAsFlow()

    private fun logout() {
        viewModelScope.launch() {
            logOutUseCase().collect { result ->
                when (result) {
                    is FirebaseResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is FirebaseResult.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }

                    is FirebaseResult.Failure -> {
                        _uiState.update { it.copy(error = result.error, isLoading = false) }
                        _event.send(SettingState.Event.ShowError)
                    }
                }
            }
        }
    }

    fun onAction(action: SettingState.Action){
        when(action){
            SettingState.Action.OnLogout -> {
                logout()
            }

            SettingState.Action.OnContactClicked -> {
                viewModelScope.launch {
                    _event.send(SettingState.Event.NavigateToContact)
                }
            }

            SettingState.Action.OnHelpClicked -> {
                viewModelScope.launch {
                    _event.send(SettingState.Event.NavigateToHelp)
                }
            }
            SettingState.Action.OnPrivacyClicked -> {
                viewModelScope.launch {
                    _event.send(SettingState.Event.NavigateToPrivacy)
                }
            }

        }
    }


}

object SettingState{
    data class UiSate(
        val isLoading: Boolean = false,
        val error: String? = null,
    )
    sealed interface Event{
        data object ShowError : Event

        data object NavigateToHelp : Event
        data object NavigateToContact : Event
        data object NavigateToPrivacy : Event

    }
    sealed interface Action{
        data object OnLogout : Action
        data object OnHelpClicked : Action
        data object OnContactClicked : Action
        data object OnPrivacyClicked : Action
    }
}