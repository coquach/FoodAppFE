package com.se114.foodapp.di

import android.content.Context
import androidx.room.Room
import com.se114.foodapp.data.local.CustomerDatabase
import com.se114.foodapp.utils.Constants.CUSTOMER_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CustomerDatabase {
        return Room.databaseBuilder(
            context,
            CustomerDatabase::class.java,
            CUSTOMER_DATABASE
        ).build()
    }

}