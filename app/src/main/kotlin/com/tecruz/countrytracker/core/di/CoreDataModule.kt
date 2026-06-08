package com.tecruz.countrytracker.core.di

import com.tecruz.countrytracker.core.data.database.CountryDatabase
import com.tecruz.countrytracker.core.data.repository.CountryRepositoryImpl
import com.tecruz.countrytracker.core.data.repository.MapRepositoryImpl
import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import com.tecruz.countrytracker.core.domain.repository.MapRepository
import com.tecruz.countrytracker.core.util.DispatcherProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::DispatcherProvider)
    single { CountryDatabase.build(androidContext()) }
    single { get<CountryDatabase>().countryDao() }
    singleOf(::MapRepositoryImpl) bind MapRepository::class
    singleOf(::CountryRepositoryImpl) bind CountryRepository::class
}
