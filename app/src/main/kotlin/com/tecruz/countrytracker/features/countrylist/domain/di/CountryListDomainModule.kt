package com.tecruz.countrytracker.features.countrylist.domain.di

import com.tecruz.countrytracker.features.countrylist.domain.GetAllCountriesUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetAllRegionsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetCountryStatisticsUseCase
import com.tecruz.countrytracker.features.countrylist.domain.GetVisitedCountryCodesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val countryListDomainModule = module {
    factoryOf(::GetAllCountriesUseCase)
    factoryOf(::GetCountryStatisticsUseCase)
    factoryOf(::GetAllRegionsUseCase)
    factoryOf(::GetVisitedCountryCodesUseCase)
}
