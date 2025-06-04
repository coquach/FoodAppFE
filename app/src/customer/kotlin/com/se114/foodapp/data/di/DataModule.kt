package com.se114.foodapp.data.di

import com.se114.foodapp.domain.repository.CartRepository
import com.se114.foodapp.data.repository.CartRepoImpl
import com.example.foodapp.data.repository.OrderRepoImpl
import com.example.foodapp.data.repository.VoucherRepoImpl
import com.se114.foodapp.data.repository.ChatBoxRepoImpl
import com.se114.foodapp.data.repository.FeedbackRepoImpl
import com.se114.foodapp.data.repository.OpenCageRepoImpl
import com.example.foodapp.domain.repository.OrderRepository
import com.example.foodapp.domain.repository.VoucherRepository
import com.se114.foodapp.domain.repository.ChatBoxRepository
import com.se114.foodapp.domain.repository.FeedbackRepository
import com.se114.foodapp.domain.repository.OpenCageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun provideCartRepository(cartRepoImpl: CartRepoImpl): CartRepository


    @Binds
    abstract fun provideFeedbackRepository(feedbackRepoImpl: FeedbackRepoImpl): FeedbackRepository


    @Binds
    abstract fun provideOpenCageRepository(openCageRepoImpl: OpenCageRepoImpl): OpenCageRepository

    @Binds
    abstract fun provideChatBoxRepository(chatBoxRepoImpl: ChatBoxRepoImpl): ChatBoxRepository

    @Binds
    abstract fun provideVoucherRepository(voucherRepoImpl: VoucherRepoImpl): VoucherRepository

    @Binds
    abstract fun provideOrderRepository(orderRepoImpl: OrderRepoImpl): OrderRepository
}