package com.example.foodapp.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType

import com.example.foodapp.data.model.MenuItem
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.ResetPasswordArgs
import com.example.foodapp.data.model.Staff

import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

val menuItemNavType = object : NavType<MenuItem>(false) {
    override fun get(bundle: Bundle, key: String): MenuItem {
        val menuItemJson = bundle.getString(key).toString()

        val menuItem = parseValue(menuItemJson)
        return menuItem.copy(
            imageUrl = menuItem.imageUrl?.let { URLDecoder.decode(it, "UTF-8") }
        )
    }

    override fun parseValue(value: String): MenuItem {
        return Json.decodeFromString(MenuItem.serializer(), value)
    }

    override fun serializeAsValue(value: MenuItem): String {
        return Json.encodeToString(
            MenuItem.serializer(), value.copy(
                imageUrl = value.imageUrl?.let { URLEncoder.encode(it, "UTF-8") }
            )
        )
    }

    override fun put(bundle: Bundle, key: String, value: MenuItem) {
        bundle.putString(key, serializeAsValue(value))
    }

}

val orderNavType = object : NavType<Order>(false) {
    override fun get(bundle: Bundle, key: String): Order? {
        return parseValue(bundle.getString(key).toString())

    }

    override fun parseValue(value: String): Order {
        return Json.decodeFromString(Order.serializer(), value)
    }

    override fun serializeAsValue(value: Order): String {
        return Json.encodeToString(Order.serializer(), value)
    }

    override fun put(bundle: Bundle, key: String, value: Order) {
        bundle.putString(key, serializeAsValue(value))
    }
}

val resetPasswordNavType = object : NavType<ResetPasswordArgs>(false) {
    override fun get(bundle: Bundle, key: String): ResetPasswordArgs? {
        return parseValue(bundle.getString(key).orEmpty())
    }

    override fun parseValue(value: String): ResetPasswordArgs {
        val decoded = URLDecoder.decode(value, "UTF-8")
        return Json.decodeFromString(decoded)
    }

    override fun put(bundle: Bundle, key: String, value: ResetPasswordArgs) {
        val encoded = URLEncoder.encode(Json.encodeToString(value), "UTF-8")
        bundle.putString(key, encoded)
    }

    override fun serializeAsValue(value: ResetPasswordArgs): String {
        return URLEncoder.encode(Json.encodeToString(value), "UTF-8")
    }
}

val staffNavType = object : NavType<Staff>(false) {
    override fun get(bundle: Bundle, key: String): Staff {
        val staffJson = bundle.getString(key).toString()

        val staff = parseValue(staffJson)
        return staff.copy(
            imageUrl = staff.imageUrl?.let { URLDecoder.decode(it, "UTF-8") }
        )
    }

    override fun parseValue(value: String): Staff {
        return Json.decodeFromString(Staff.serializer(), value)
    }

    override fun serializeAsValue(value: Staff): String {
        return Json.encodeToString(
            Staff.serializer(), value.copy(
                imageUrl = value.imageUrl?.let { URLEncoder.encode(it, "UTF-8") }
            )
        )
    }

    override fun put(bundle: Bundle, key: String, value: Staff) {
        bundle.putString(key, serializeAsValue(value))
    }

}