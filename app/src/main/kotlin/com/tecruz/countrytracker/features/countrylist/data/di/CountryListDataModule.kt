package com.tecruz.countrytracker.features.countrylist.data.di

import com.tecruz.countrytracker.features.countrylist.data.repository.CountryListRepositoryImpl
import com.tecruz.countrytracker.features.countrylist.domain.repository.CountryListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing CountryList data dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CountryListDataModule {

    @Binds
    @Singleton
    abstract fun bindCountryListRepository(countryListRepositoryImpl: CountryListRepositoryImpl): CountryListRepository
}
