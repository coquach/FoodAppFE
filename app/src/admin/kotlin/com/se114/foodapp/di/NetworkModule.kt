package com.se114.foodapp.di

import com.example.foodapp.data.remote.AiApi
import com.example.foodapp.data.remote.main_api.FoodTableApi
import com.example.foodapp.data.remote.main_api.ImportApi
import com.example.foodapp.data.remote.main_api.IngredientApi
import com.example.foodapp.data.remote.main_api.InventoryApi
import com.example.foodapp.data.remote.main_api.StaffApi
import com.example.foodapp.data.remote.main_api.SupplierApi
import com.se114.foodapp.data.remote.ReportApi
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
    fun provideImportApi(@Named("MainApi") retrofit: Retrofit): ImportApi {
        return retrofit.create(ImportApi::class.java)
    }

    @Provides
    @Singleton
    fun provideIngredientApi(@Named("MainApi") retrofit: Retrofit): IngredientApi {
        return retrofit.create(IngredientApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStaffApi(@Named("MainApi") retrofit: Retrofit): StaffApi {
        return retrofit.create(StaffApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSupplierApi(@Named("MainApi") retrofit: Retrofit): SupplierApi {
        return retrofit.create(SupplierApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReportApi(@Named("MainApi") retrofit: Retrofit): ReportApi {
        return retrofit.create(ReportApi::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryApi(@Named("MainApi") retrofit: Retrofit): InventoryApi {
        return retrofit.create(InventoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAiApi(@Named("MainApi") retrofit: Retrofit): AiApi {
        return retrofit.create(AiApi::class.java)
    }
}