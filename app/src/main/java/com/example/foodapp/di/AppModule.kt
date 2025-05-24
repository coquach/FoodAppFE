package com.example.foodapp.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodapp.BuildConfig
import com.example.foodapp.data.remote.AiApi
import com.example.foodapp.data.remote.OpenCageApi
import com.example.foodapp.data.remote.OsrmApi
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.location.LocationManager
import com.example.foodapp.utils.gson.BigDecimalDeserializer
import com.example.foodapp.utils.gson.LocalDateDeserializer
import com.example.foodapp.utils.gson.LocalDateTimeDeserializer
import com.example.foodapp.utils.gson.LocalTimeDeserializer

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideOkHttpClient(
        account: AccountRepository,
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor  { chain ->
            val originalRequest = chain.request()

                val token = runBlocking { account.getUserToken() }
            Log.d("Firebase Token: ", token.toString())
                val requestWithAuth = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(requestWithAuth)
            }
        client.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        return client.build()
    }



    @Provides
    @Singleton
    fun provideRetrofitMainApi(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .registerTypeAdapter(BigDecimal::class.java, BigDecimalDeserializer())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .setLenient()
            .create()
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
           .build()
    }


    @Provides
    @Singleton
    fun provideRetrofitGeocodingApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.opencagedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitNavigationApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://router.project-osrm.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    @Provides
    @Singleton
    fun provideLocationService(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationManager(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): LocationManager {
        return LocationManager(fusedLocationProviderClient, context)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

