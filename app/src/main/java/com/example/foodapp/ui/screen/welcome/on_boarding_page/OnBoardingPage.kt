package com.example.foodapp.ui.screen.welcome.on_boarding_page

import androidx.annotation.DrawableRes
import com.example.foodapp.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    data object First : OnBoardingPage(
        image = R.drawable.welcome_first,
        title = "Đặt món",
        description = "Chọn món yêu thích, \nchỉ với một chạm!"
    )

    data object Second : OnBoardingPage(
        image = R.drawable.welcome_second,
        title = "Đặt hàng",
        description = "Đặt hàng nhanh, \nthưởng thức tiện lợi!"
    )

    data object Third : OnBoardingPage(
        image = R.drawable.welcome_third,
        title = "Voucher & Ưu đãi",
        description = "Nhận ngay ưu đãi, săn voucher giảm giá cực hấp dẫn"
    )
}