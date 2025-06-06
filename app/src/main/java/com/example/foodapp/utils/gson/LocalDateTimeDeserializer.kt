package com.example.foodapp.utils.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeDeserializer: JsonDeserializer<LocalDateTime> {

        private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")


    override fun deserialize(
        json: com.google.gson.JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        return try {
            json?.asString?.let { LocalDateTime.parse(it, formatter) }
        } catch (e: Exception) {
            null
        }
    }
}