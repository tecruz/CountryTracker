package com.tecruz.countrytracker.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tecruz.countrytracker.core.data.database.CountryDao
import com.tecruz.countrytracker.core.data.database.CountryDatabase
import com.tecruz.countrytracker.core.data.datasource.CountryDataLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCountryDatabase(
        @ApplicationContext context: Context,
        daoProvider: Provider<CountryDao>,
    ): CountryDatabase = Room.databaseBuilder(
        context,
        CountryDatabase::class.java,
        "country_tracker_database",
    )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                    val countries = CountryDataLoader.loadCountriesFromAssets(context)
                    daoProvider.get().insertCountries(countries)
                }
            }
        })
        .build()

    @Provides
    @Singleton
    fun provideCountryDao(database: CountryDatabase): CountryDao = database.countryDao()
}
