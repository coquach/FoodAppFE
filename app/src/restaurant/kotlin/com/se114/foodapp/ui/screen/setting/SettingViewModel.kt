package com.se114.foodapp.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.datastore.CartRepository
import com.example.foodapp.data.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val accountService: AccountService

) : ViewModel() {

    private val _event = MutableSharedFlow<SettingEvents>()
    val event = _event.asSharedFlow()

    fun onLogoutClicked() {
        viewModelScope.launch() {
            try {
                _event.emit(SettingEvents.NavigateToAuth)
                val result = accountService.signOut()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onShowLogoutDialog() {
        viewModelScope.launch {
            _event.emit(SettingEvents.OnLogout)
        }
    }



    sealed class SettingEvents {
        data object NavigateToAuth : SettingEvents()
        data object OnLogout : SettingEvents()

    }
}