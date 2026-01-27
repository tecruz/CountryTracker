package com.tecruz.countrytracker.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for the Country Tracker app.
 */
@Database(
    entities = [CountryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CountryDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
}
