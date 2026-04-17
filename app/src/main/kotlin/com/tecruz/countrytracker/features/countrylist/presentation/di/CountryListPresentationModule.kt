package com.tecruz.countrytracker.features.countrylist.presentation.di

import com.tecruz.countrytracker.features.countrylist.presentation.CountryListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val countryListPresentationModule = module {
    viewModelOf(::CountryListViewModel)
}
