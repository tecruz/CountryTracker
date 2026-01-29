package com.tecruz.countrytracker.features.countrydetail.data.di

import com.tecruz.countrytracker.features.countrydetail.data.repository.CountryDetailRepositoryImpl
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing CountryDetail data dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CountryDetailDataModule {

    @Binds
    @Singleton
    abstract fun bindCountryDetailRepository(
        countryDetailRepositoryImpl: CountryDetailRepositoryImpl,
    ): CountryDetailRepository
}
