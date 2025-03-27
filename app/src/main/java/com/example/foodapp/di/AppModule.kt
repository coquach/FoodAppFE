package com.example.foodapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.foodapp.BuildConfig
import com.example.foodapp.data.FoodApi
import com.example.foodapp.data.FoodAppSession
import com.example.foodapp.token.TokenInterceptor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideTokenInterceptor(
        session: FoodAppSession,
        foodApi: Provider<FoodApi>
    ): TokenInterceptor {
        return TokenInterceptor(session, foodApi)
    }


    @Provides
    fun provideClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        val client = OkHttpClient.Builder()

        client.addInterceptor(tokenInterceptor)

        client.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        return client.build()
    }


    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
           .build()
    }

    @Provides
    fun provideFoodApi(retrofit: Retrofit): FoodApi {
        return retrofit.create(FoodApi::class.java)
    }
 

    @Provides
    fun provideSession(@ApplicationContext context: Context): FoodAppSession {
        return FoodAppSession(context)
    }

    @Provides
    fun provideLocationService(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }


    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")