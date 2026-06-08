package com.tecruz.countrytracker.core.di

import com.tecruz.countrytracker.core.presentation.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val corePresentationModule = module {
    viewModelOf(::MainViewModel)
}
