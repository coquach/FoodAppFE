package com.example.foodapp.utils.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeDeserializer : JsonDeserializer<LocalTime> {

    override fun deserialize(
        json: com.google.gson.JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalTime? {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return LocalTime.parse(json?.asString, formatter)
    }

}