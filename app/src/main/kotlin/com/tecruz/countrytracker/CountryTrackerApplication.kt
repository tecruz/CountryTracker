package com.tecruz.countrytracker

import android.app.Application
import com.tecruz.countrytracker.core.di.coreDataModule
import com.tecruz.countrytracker.core.di.corePresentationModule
import com.tecruz.countrytracker.features.countrydetail.domain.di.countryDetailDomainModule
import com.tecruz.countrytracker.features.countrydetail.presentation.di.countryDetailPresentationModule
import com.tecruz.countrytracker.features.countrylist.domain.di.countryListDomainModule
import com.tecruz.countrytracker.features.countrylist.presentation.di.countryListPresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CountryTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CountryTrackerApplication)
            modules(
                coreDataModule,
                corePresentationModule,
                countryListDomainModule,
                countryListPresentationModule,
                countryDetailDomainModule,
                countryDetailPresentationModule,
            )
        }
    }
}
