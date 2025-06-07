package com.se114.foodapp.di

import com.example.foodapp.data.remote.main_api.OrderApi
import com.se114.foodapp.data.remote.OsrmApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("NavigationApi")
    fun provideRetrofitNavigationApi(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://router.project-osrm.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOsrmApi(@Named("NavigationApi") retrofit: Retrofit): OsrmApi {
        return retrofit.create(OsrmApi::class.java)
    }


    @Provides
    @Singleton
    fun provideOrderApi(@Named("MainApi") retrofit: Retrofit): OrderApi {
        return retrofit.create(OrderApi::class.java)
    }




}