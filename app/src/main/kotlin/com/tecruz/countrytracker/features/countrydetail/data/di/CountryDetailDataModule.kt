package com.tecruz.countrytracker.features.countrydetail.data.di

import com.tecruz.countrytracker.features.countrydetail.data.repository.CountryDetailRepositoryImpl
import com.tecruz.countrytracker.features.countrydetail.domain.repository.CountryDetailRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val countryDetailDataModule = module {
    singleOf(::CountryDetailRepositoryImpl) bind CountryDetailRepository::class
}
