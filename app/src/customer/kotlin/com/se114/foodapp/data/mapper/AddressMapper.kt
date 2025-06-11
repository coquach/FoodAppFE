package com.se114.foodapp.data.mapper

import com.example.foodapp.data.model.Address
import com.example.foodapp.data.model.AddressUI

fun AddressUI.toAddress() = Address(
    formatAddress = this.formatAddress,
    latitude = this.latitude?: 0.0,
    longtitude = this.longitude?: 0.0,

)