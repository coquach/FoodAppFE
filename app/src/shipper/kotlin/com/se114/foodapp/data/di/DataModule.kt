package com.se114.foodapp.data.di

import com.example.foodapp.data.repository.FoodTableRepoImpl
import com.example.foodapp.data.repository.InventoryRepoImpl
import com.example.foodapp.data.repository.OrderRepoImpl
import com.example.foodapp.data.repository.StaffRepoImpl
import com.example.foodapp.data.repository.VoucherRepoImpl
import com.example.foodapp.domain.repository.FoodTableRepository
import com.example.foodapp.domain.repository.InventoryRepository
import com.example.foodapp.domain.repository.OrderRepository
import com.example.foodapp.domain.repository.StaffRepository
import com.example.foodapp.domain.repository.VoucherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {



    @Binds
    abstract fun provideOrderRepository(orderRepoImpl: OrderRepoImpl): OrderRepository



}
