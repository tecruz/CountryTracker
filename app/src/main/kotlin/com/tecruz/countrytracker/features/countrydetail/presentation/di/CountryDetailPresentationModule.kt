package com.tecruz.countrytracker.features.countrydetail.presentation.di

import com.tecruz.countrytracker.features.countrydetail.presentation.CountryDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val countryDetailPresentationModule = module {
    viewModelOf(::CountryDetailViewModel)
}
