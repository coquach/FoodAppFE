package com.example.foodapp.navigation

import android.os.Bundle
import androidx.navigation.NavType
import com.example.foodapp.data.model.Export
import com.example.foodapp.data.model.Food
import com.example.foodapp.data.model.Import
import com.example.foodapp.data.model.Order
import com.example.foodapp.data.model.Staff
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

val FoodNavType = object : NavType<Food>(false) {
    override fun get(bundle: Bundle, key: String): Food {
        val foodJson = bundle.getString(key).toString()

        val food = parseValue(foodJson)
        return food.copy(
            images = food.images?.map {image ->
                image.copy(
                    url = URLDecoder.decode(image.url, "UTF-8")
                )
            }
        )
    }

    override fun parseValue(value: String): Food {
        return Json.decodeFromString(Food.serializer(), value)
    }

    override fun serializeAsValue(value: Food): String {
        return Json.encodeToString(
            Food.serializer(), value.copy(
                images = value.images?.map {image ->
                    image.copy(
                        url = URLEncoder.encode(image.url, "UTF-8")
                    )
                }
            )
        )
    }

    override fun put(bundle: Bundle, key: String, value: Food) {
        bundle.putString(key, serializeAsValue(value))
    }

}

val orderNavType = object : NavType<Order>(false) {
    override fun get(bundle: Bundle, key: String): Order {
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



val staffNavType = object : NavType<Staff>(false) {
    override fun get(bundle: Bundle, key: String): Staff {
        val staffJson = bundle.getString(key).toString()

        val staff = parseValue(staffJson)
        return staff.copy(
            avatar = staff.avatar?.copy(
                url = URLDecoder.decode(staff.avatar.url, "UTF-8")
            )
        )
    }

    override fun parseValue(value: String): Staff {
        return Json.decodeFromString(Staff.serializer(), value)
    }

    override fun serializeAsValue(value: Staff): String {
        return Json.encodeToString(
            Staff.serializer(), value.copy(
                avatar = value.avatar?.copy(
                    url = URLEncoder.encode(value.avatar.url, "UTF-8")
                )
            )
        )
    }

    override fun put(bundle: Bundle, key: String, value: Staff) {
        bundle.putString(key, serializeAsValue(value))
    }

}

val importNavType = object : NavType<Import>(false) {
    override fun get(bundle: Bundle, key: String): Import {
        return parseValue(bundle.getString(key).orEmpty())
    }

    override fun parseValue(value: String): Import {
        return Json.decodeFromString(Import.serializer(), value)
    }

    override fun serializeAsValue(value: Import): String {
        return Json.encodeToString(Import.serializer(), value)
    }

    override fun put(bundle: Bundle, key: String, value: Import) {
        bundle.putString(key, serializeAsValue(value))
    }

}

val exportNavItem = object : NavType<Export>(false) {
    override fun get(bundle: Bundle, key: String): Export {
        return parseValue(bundle.getString(key).orEmpty())
    }

    override fun parseValue(value: String): Export {
        return Json.decodeFromString(Export.serializer(), value)
    }

    override fun serializeAsValue(value: Export): String {
        return Json.encodeToString(Export.serializer(), value)
    }

    override fun put(bundle: Bundle, key: String, value: Export) {
        bundle.putString(key, serializeAsValue(value))
    }

}