package com.se114.foodapp.ui.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.datastore.WelcomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val welcomeRepository: WelcomeRepository
): ViewModel() {
    private val _uiEvent = Channel<Event>()
    val uiEvent = _uiEvent.receiveAsFlow()
    fun saveOnBoardingState(completed: Boolean) {
        viewModelScope.launch() {
            welcomeRepository.saveOnBoardingState(completed = completed)
            _uiEvent.send(Event.NavigateToAuth)
        }
    }
}

sealed interface Event{
    object NavigateToAuth: Event

}