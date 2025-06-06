package com.se114.foodapp.di

import android.content.Context
import androidx.room.Room
import com.se114.foodapp.data.local.StaffDatabase
import com.se114.foodapp.utils.Constants.STAFF_DATABASE
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
    ): StaffDatabase {
        return Room.databaseBuilder(
            context,
            StaffDatabase::class.java,
            STAFF_DATABASE
        ).build()
    }

}