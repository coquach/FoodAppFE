package com.se114.foodapp.ui.screen.home.banner

import androidx.annotation.DrawableRes
import com.example.foodapp.R

sealed class Slider(
    @DrawableRes
    val image: Int
) {
    data object First : Slider(R.drawable.banner_first)
    data object Second : Slider(R.drawable.banner_second)
    data object Third : Slider(R.drawable.banner_third)
}