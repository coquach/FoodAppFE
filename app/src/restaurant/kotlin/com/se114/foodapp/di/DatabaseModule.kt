package com.se114.foodapp.di

import android.content.Context
import androidx.room.Room
import com.se114.foodapp.data.local.AdminDatabase
import com.se114.foodapp.utils.Constants.ADMIN_DATABASE
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
    ): AdminDatabase {
        return Room.databaseBuilder(
            context,
            AdminDatabase::class.java,
            ADMIN_DATABASE
        ).build()
    }

}


