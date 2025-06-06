package com.se114.foodapp.di

import com.example.foodapp.data.remote.OsrmApi
import com.se114.foodapp.data.remote.ExportApi
import com.example.foodapp.data.remote.main_api.FoodTableApi
import com.example.foodapp.data.remote.main_api.InventoryApi
import com.example.foodapp.data.remote.main_api.OrderApi
import com.example.foodapp.data.remote.main_api.StaffApi
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
    fun provideFoodTableApi(@Named("MainApi") retrofit: Retrofit): FoodTableApi {
        return retrofit.create(FoodTableApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExportApi(@Named("MainApi") retrofit: Retrofit): ExportApi {
        return retrofit.create(ExportApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStaffApi(@Named("MainApi") retrofit: Retrofit): StaffApi {
        return retrofit.create(StaffApi::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryApi(@Named("MainApi") retrofit: Retrofit): InventoryApi {
        return retrofit.create(InventoryApi::class.java)
    }
    @Provides
    @Singleton
    fun provideOrderApi(@Named("MainApi") retrofit: Retrofit): OrderApi {
        return retrofit.create(OrderApi::class.java)
    }




}