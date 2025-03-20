package com.example.foodapp.ui.screen.auth

import androidx.lifecycle.ViewModel
import com.example.foodapp.data.FoodApi

abstract class BaseAuthViewModel(open val foodApi: FoodApi) : ViewModel() {
    var error: String = ""
    var errorDescription = ""
}