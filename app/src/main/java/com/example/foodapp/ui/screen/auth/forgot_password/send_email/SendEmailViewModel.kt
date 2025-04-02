package com.example.foodapp.ui.screen.auth.forgot_password.send_email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendEmailViewModel @Inject constructor() : ViewModel() {
    fun onLoginClick() {
       viewModelScope.launch {
           _event.emit(SendEmailEvents.NavigateToLogin)
       }
    }

    private val _event = MutableSharedFlow<SendEmailEvents>();
    val event = _event.asSharedFlow()




    sealed class SendEmailEvents{
        object NavigateToLogin : SendEmailEvents()
    }
}