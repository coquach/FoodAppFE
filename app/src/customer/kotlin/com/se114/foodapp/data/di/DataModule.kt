package com.se114.foodapp.data.di

import com.example.foodapp.data.local.daos.CartDao
import com.example.foodapp.data.remote.AiApi
import com.example.foodapp.data.remote.OpenCageApi
import com.example.foodapp.data.remote.main_api.FeedbackApi
import com.se114.foodapp.data.local.CustomerDatabase
import com.se114.foodapp.data.repository.CartRepoImpl
import com.se114.foodapp.data.repository.ChatBoxRepoImpl
import com.se114.foodapp.data.repository.FeedbackRepoImpl
import com.se114.foodapp.data.repository.OpenCageRepoImpl
import com.se114.foodapp.domain.repository.CartRepository
import com.se114.foodapp.domain.repository.ChatBoxRepository
import com.se114.foodapp.domain.repository.FeedbackRepository
import com.se114.foodapp.domain.repository.OpenCageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideCartRepository(customerDatabase: CustomerDatabase): CartRepository {
        return CartRepoImpl(customerDatabase)
    }

    @Provides
    @Singleton
    fun provideFeedbackRepository(feedbackApi: FeedbackApi): FeedbackRepository {
        return FeedbackRepoImpl(feedbackApi)
    }

    @Provides
    @Singleton
    fun provideOpenCageRepository(openCageApi: OpenCageApi): OpenCageRepository {
        return OpenCageRepoImpl(openCageApi)
    }

    @Provides
    @Singleton
    fun provideChatBoxRepository(aiApi: AiApi): ChatBoxRepository {
        return ChatBoxRepoImpl(aiApi)
    }
}