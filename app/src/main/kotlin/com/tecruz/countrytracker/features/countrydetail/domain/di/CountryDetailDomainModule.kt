package com.tecruz.countrytracker.features.countrydetail.domain.di

import com.tecruz.countrytracker.features.countrydetail.domain.GetCountryByCodeUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsUnvisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsVisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryNotesUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryRatingUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val countryDetailDomainModule = module {
    factoryOf(::GetCountryByCodeUseCase)
    factoryOf(::MarkCountryAsVisitedUseCase)
    factoryOf(::MarkCountryAsUnvisitedUseCase)
    factoryOf(::UpdateCountryNotesUseCase)
    factoryOf(::UpdateCountryRatingUseCase)
}
