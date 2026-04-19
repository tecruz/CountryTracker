package com.tecruz.countrytracker.core.di

import com.tecruz.countrytracker.core.data.database.CountryDatabase
import com.tecruz.countrytracker.core.util.DispatcherProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreDataModule = module {
    single { DispatcherProvider() }
    single { CountryDatabase.build(androidContext()) }
    single { get<CountryDatabase>().countryDao() }
}
