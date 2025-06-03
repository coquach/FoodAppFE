package com.se114.foodapp.di

import com.example.foodapp.data.remote.AiApi
import com.example.foodapp.data.remote.OpenCageApi
import com.example.foodapp.data.remote.main_api.FeedbackApi

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOpenCageApi(@Named("GeocodingApi") retrofit: Retrofit): OpenCageApi {
        return retrofit.create(OpenCageApi::class.java)
    }


    @Provides
    @Singleton
    fun provideAIApi(@Named("MainApi") retrofit: Retrofit): AiApi {
        return retrofit.create(AiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFeedbackApi(@Named("MainApi") retrofit: Retrofit): FeedbackApi {
        return retrofit.create(FeedbackApi::class.java)
    }
}