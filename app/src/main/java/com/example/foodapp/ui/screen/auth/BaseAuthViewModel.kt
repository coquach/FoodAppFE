package com.example.foodapp.ui.screen.auth

import androidx.lifecycle.ViewModel
import com.example.foodapp.data.remote.FoodApi

abstract class BaseAuthViewModel() : ViewModel() {


    var error: String = ""
    var errorDescription = ""
}