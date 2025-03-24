package com.example.foodapp.location

import com.example.foodapp.BuildConfig
import com.example.foodapp.data.model.Address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import okhttp3.OkHttpClient
import okhttp3.Request

import org.json.JSONObject

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeocodingRepository @Inject constructor(private val client: OkHttpClient) {
    private val apiKey = BuildConfig.MAPS_API_KEY

    suspend fun getAddressFromCoordinates(lat: Double, lon: Double): Address? {
        return withContext(Dispatchers.IO) {  // Chạy trong IO thread
            val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lon&key=$apiKey"
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                response.body?.string()?.let { jsonData ->
                    val jsonObject = JSONObject(jsonData)
                    val results = jsonObject.getJSONArray("results")

                    if (results.length() > 0) {
                        val firstResult = results.getJSONObject(0)
                        val addressComponents = firstResult.getJSONArray("address_components")

                        var addressLine1 = ""
                        var city = ""
                        var state = ""
                        var zipCode = ""
                        var country = ""

                        for (i in 0 until addressComponents.length()) {
                            val component = addressComponents.getJSONObject(i)
                            val types = component.getJSONArray("types")
                            val longName = component.getString("long_name")

                            when {
                                "street_number" in types.toString() -> addressLine1 = longName
                                "route" in types.toString() -> addressLine1 += " $longName"
                                "locality" in types.toString() -> city = longName
                                "administrative_area_level_1" in types.toString() -> state = longName
                                "postal_code" in types.toString() -> zipCode = longName
                                "country" in types.toString() -> country = longName
                            }
                        }

                        return@withContext Address(
                            addressLine1 = addressLine1.trim(),
                            city = city,
                            state = state,
                            zipCode = zipCode,
                            country = country,
                            latitude = lat,
                            longitude = lon
                        )
                    }
                }
                return@withContext null
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

}