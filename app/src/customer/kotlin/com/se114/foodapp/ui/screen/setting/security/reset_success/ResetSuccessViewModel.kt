package com.se114.foodapp.ui.screen.setting.security.reset_success

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.domain.use_case.auth.LogOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetSuccessViewModel @Inject constructor(
    private val logOutUseCase: LogOutUseCase
) : ViewModel() {
    private val _event = Channel<Event>()
    val event = _event.receiveAsFlow()

    fun logOut() {
        viewModelScope.launch {
            logOutUseCase()
            _event.send(Event.LogOut)
        }
    }

}
sealed interface Event{
    object LogOut: Event
}