package com.example.foodapp.data.di

import com.example.foodapp.data.repository.AccountRepoImpl
import com.example.foodapp.data.repository.FoodRepoImpl
import com.example.foodapp.data.repository.MenuRepoImpl
import com.example.foodapp.data.repository.OrderRepoImpl
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.domain.repository.FoodRepository
import com.example.foodapp.domain.repository.MenuRepository
import com.example.foodapp.domain.repository.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
abstract class MainDataModule {


 @Binds
abstract fun provideAccountRepository(accountRepoImpl: AccountRepoImpl): AccountRepository

@Binds
abstract fun provideFoodRepository(foodRepoImpl: FoodRepoImpl): FoodRepository


@Binds
abstract fun provideMenuRepository(menuRepoImpl: MenuRepoImpl): MenuRepository

@Binds
abstract fun provideOrderRepository(orderRepoImpl: OrderRepoImpl): OrderRepository





}