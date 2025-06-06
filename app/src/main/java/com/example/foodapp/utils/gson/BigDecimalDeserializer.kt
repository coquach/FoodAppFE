package com.example.foodapp.utils.gson

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer

import java.lang.reflect.Type
import java.math.BigDecimal

class BigDecimalDeserializer : JsonDeserializer<BigDecimal> {
    override fun deserialize(
        json: com.google.gson.JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BigDecimal? {
        return json?.asDouble?.let { BigDecimal.valueOf(it) }
    }
}