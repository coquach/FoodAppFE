package com.example.foodapp.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodapp.BuildConfig
import com.example.foodapp.data.remote.FoodApi
import com.example.foodapp.data.datastore.CartRepository
import com.example.foodapp.data.service.AccountService
import com.example.foodapp.utils.gson.LocalDateDeserializer

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
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
import java.time.Instant
import java.time.LocalDate
import javax.inject.Provider
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {



    @Provides
    @Singleton
    fun provideOkHttpClient(
        account: AccountService,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor  { chain ->
            val originalRequest = chain.request()
            val requestUrl = originalRequest.url.toString()

            if (requestUrl.contains("auth/register")) {
                return@addInterceptor chain.proceed(originalRequest)
            }
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
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
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
    fun provideFoodApi(retrofit: Retrofit): FoodApi {
        return retrofit.create(FoodApi::class.java)
    }
 



    @Provides
    @Singleton
    fun provideLocationService(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }


    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")