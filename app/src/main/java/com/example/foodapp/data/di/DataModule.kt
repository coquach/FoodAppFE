package com.example.foodapp.data.di

import com.example.foodapp.data.remote.main_api.FoodApi
import com.example.foodapp.data.remote.main_api.OrderApi
import com.example.foodapp.data.remote.main_api.VoucherApi
import com.example.foodapp.data.repository.AccountRepoImpl
import com.example.foodapp.data.repository.FoodRepoImpl
import com.example.foodapp.data.repository.MenuRepoImpl
import com.example.foodapp.data.repository.OrderRepoImpl
import com.example.foodapp.data.repository.VoucherRepoImpl
import com.example.foodapp.domain.repository.AccountRepository
import com.example.foodapp.domain.repository.FoodRepository
import com.example.foodapp.domain.repository.MenuRepository
import com.example.foodapp.domain.repository.OrderRepository
import com.example.foodapp.domain.repository.VoucherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DataModule {


    @Provides
    @Singleton
    fun provideAccountRepository(): AccountRepository {
        return AccountRepoImpl()
    }

    @Provides
    @Singleton
    fun provideFoodRepository(foodApi: FoodApi): FoodRepository {
        return FoodRepoImpl(foodApi)
    }

    @Provides
    @Singleton
    fun provideMenuRepository(foodApi: FoodApi): MenuRepository {
        return MenuRepoImpl(foodApi)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(orderApi: OrderApi): OrderRepository {
        return OrderRepoImpl(orderApi)
    }

    @Singleton
    @Provides
    fun provideVoucherRepository(voucherApi: VoucherApi): VoucherRepository {
        return VoucherRepoImpl(voucherApi)
    }

}