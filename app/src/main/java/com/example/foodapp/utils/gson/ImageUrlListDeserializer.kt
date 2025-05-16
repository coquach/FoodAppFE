package com.example.foodapp.utils.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ImageUrlListDeserializer : JsonDeserializer<List<String>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<String> {
        return try {
            json?.asJsonArray?.mapNotNull { element ->
                element.asJsonObject.get("url")?.asString
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}