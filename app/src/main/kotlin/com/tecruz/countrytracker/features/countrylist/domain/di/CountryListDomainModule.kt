package com.tecruz.countrytracker.features.countrylist.domain.di

import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val countryListDomainModule = module {
    factoryOf(::GetAllCountriesUseCase)
    factoryOf(::GetCountryStatisticsUseCase)
}
