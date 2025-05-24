package com.se114.foodapp.di

import com.example.foodapp.data.remote.main_api.FoodApi
import com.example.foodapp.data.remote.main_api.FoodTableApi
import com.example.foodapp.data.remote.main_api.ImportApi
import com.example.foodapp.data.remote.main_api.IngredientApi
import com.example.foodapp.data.remote.main_api.StaffApi
import com.example.foodapp.data.remote.main_api.VoucherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFoodApi( retrofit: Retrofit): FoodApi {
        return retrofit.create(FoodApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFoodTableApi( retrofit: Retrofit): FoodTableApi {
        return retrofit.create(FoodTableApi::class.java)
    }

    @Provides
    @Singleton
    fun provideImportApi(retrofit: Retrofit): ImportApi {
        return retrofit.create(ImportApi::class.java)
    }

    @Provides
    @Singleton
    fun provideIngredientApi(retrofit: Retrofit): IngredientApi {
        return retrofit.create(IngredientApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStaffApi(retrofit: Retrofit): StaffApi {
        return retrofit.create(StaffApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVoucherApi(retrofit: Retrofit): VoucherApi {
        return retrofit.create(VoucherApi::class.java)
    }
}