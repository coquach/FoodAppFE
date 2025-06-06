package com.example.foodapp.utils.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import kotlinx.serialization.json.JsonElement
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateDeserializer : JsonDeserializer<LocalDate> {

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun deserialize(
        json: com.google.gson.JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        val dateString = json?.asString ?: throw JsonParseException("Date string is null")
        return LocalDate.parse(dateString, formatter)
    }
}