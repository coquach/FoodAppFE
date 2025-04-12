package com.se114.foodapp.ui.screen.warehouse

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WarehouseViewModel @Inject constructor() : ViewModel() {

    sealed class WarehouseState {

    }
}