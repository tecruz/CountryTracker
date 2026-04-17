package com.tecruz.countrytracker.features.countrylist.data.di

import com.tecruz.countrytracker.features.countrylist.data.repository.CountryListRepositoryImpl
import com.tecruz.countrytracker.features.countrylist.domain.repository.CountryListRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val countryListDataModule = module {
    singleOf(::CountryListRepositoryImpl) bind CountryListRepository::class
}
