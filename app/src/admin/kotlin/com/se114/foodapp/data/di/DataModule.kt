package com.se114.foodapp.data.di

import com.example.foodapp.data.remote.main_api.StaffApi
import com.example.foodapp.data.repository.FoodTableRepoImpl
import com.example.foodapp.data.repository.VoucherRepoImpl
import com.example.foodapp.domain.repository.FoodTableRepository
import com.example.foodapp.domain.repository.VoucherRepository
import com.se114.foodapp.data.repository.ImportRepoImpl
import com.se114.foodapp.data.repository.IngredientRepoImpl
import com.se114.foodapp.data.repository.ReportRepoImpl
import com.se114.foodapp.data.repository.StaffRepoImpl
import com.se114.foodapp.data.repository.SupplierRepoImpl
import com.se114.foodapp.domain.repository.ImportRepository
import com.se114.foodapp.domain.repository.IngredientRepository
import com.se114.foodapp.domain.repository.ReportRepository
import com.se114.foodapp.domain.repository.StaffRepository
import com.se114.foodapp.domain.repository.SupplierRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun provideStaffRepository(impl: StaffRepoImpl): StaffRepository

    @Binds
    abstract fun provideSupplierRepository(impl: SupplierRepoImpl): SupplierRepository

    @Binds
    abstract fun provideIngredientRepository(impl: IngredientRepoImpl): IngredientRepository

    @Binds
    abstract fun provideImportRepository(impl: ImportRepoImpl): ImportRepository


    @Binds
    abstract fun provideVoucherRepository(voucherRepoImpl: VoucherRepoImpl): VoucherRepository

    @Binds
    abstract fun provideFoodTableRepository(foodTableRepoImpl: FoodTableRepoImpl): FoodTableRepository

    @Binds
    abstract fun provideReportRepository(reportRepoImpl: ReportRepoImpl): ReportRepository
}