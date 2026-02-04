package com.tecruz.countrytracker.core.di

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
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
import java.io.IOException
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCountryDatabase(@ApplicationContext context: Context): CountryDatabase = Room.databaseBuilder(
        context,
        CountryDatabase::class.java,
        "country_tracker_database",
    )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                try {
                    val countries = CountryDataLoader.loadCountriesFromAssets(context)
                    db.beginTransaction()
                    try {
                        countries.forEach { country ->
                            db.insert(
                                "countries",
                                SQLiteDatabase.CONFLICT_REPLACE,
                                ContentValues().apply {
                                    put("code", country.code)
                                    put("name", country.name)
                                    put("region", country.region)
                                    put("visited", if (country.visited) 1 else 0)
                                    putNull("visitedDate")
                                    put("notes", country.notes)
                                    put("rating", country.rating)
                                    put("flagEmoji", country.flagEmoji)
                                },
                            )
                        }
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                } catch (e: IOException) {
                    Log.e("DatabaseModule", "Failed to seed database", e)
                } catch (e: SQLException) {
                    Log.e("DatabaseModule", "Failed to seed database", e)
                }
            }
        })
        .build()

    @Provides
    @Singleton
    fun provideCountryDao(database: CountryDatabase): CountryDao = database.countryDao()
}
