package com.example.foodapp.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType

import com.example.foodapp.data.model.FoodItem
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.ResetPasswordArgs
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

val foodItemNavType = object : NavType<FoodItem>(false) {
    override fun get(bundle: Bundle, key: String): FoodItem? {
        return parseValue(bundle.getString(key).toString()).copy(
            imageUrl = URLDecoder.decode(
                parseValue(bundle.getString(key).toString()).imageUrl,
                "UTF-8"
            )
        )
    }

    override fun parseValue(value: String): FoodItem {
        return Json.decodeFromString(FoodItem.serializer(), value)
    }

    override fun serializeAsValue(value: FoodItem): String {
        return Json.encodeToString(
            FoodItem.serializer(), value.copy(
                imageUrl = URLEncoder.encode(value.imageUrl, "UTF-8"),
            )
        )
    }

    override fun put(bundle: Bundle, key: String, value: FoodItem) {
        bundle.putString(key, serializeAsValue(value))
    }

}

val orderNavType = object : NavType<Order>(false) {
    override fun get(bundle: Bundle, key: String): Order? {
        return parseValue(bundle.getString(key).toString()).copy(
            address = parseValue(bundle.getString(key).toString()).address,
            createdAt = parseValue(bundle.getString(key).toString()).createdAt,
            updatedAt = parseValue(bundle.getString(key).toString()).updatedAt
        )
    }

    override fun parseValue(value: String): Order {
        return Json.decodeFromString(Order.serializer(), value)
    }

    override fun serializeAsValue(value: Order): String {
        return Json.encodeToString(Order.serializer(), value.copy(
            address = value.address,
            createdAt = value.createdAt,
            updatedAt = value.updatedAt
        ))
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