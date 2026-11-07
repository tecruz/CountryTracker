package com.tecruz.countrytracker.features.countrylist.presentation.di

import com.tecruz.countrytracker.core.util.MapPathParser
import com.tecruz.countrytracker.core.util.SvgPathParser
import com.tecruz.countrytracker.features.countrylist.presentation.CountryListViewModel
import com.tecruz.countrytracker.features.countrylist.presentation.components.worldmap.WorldMapGeometryLoader
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val countryListPresentationModule = module {
    single<MapPathParser> { SvgPathParser }
    singleOf(::WorldMapGeometryLoader)
    viewModelOf(::CountryListViewModel)
}
