package com.tecruz.countrytracker.core.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecruz.countrytracker.core.data.datasource.CountryDataLoader

/**
 * Room database for the Country Tracker app.
 */
@Database(
    entities = [CountryEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class CountryDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao

    companion object {
        fun build(context: android.content.Context): CountryDatabase =
            Room.databaseBuilder(context, CountryDatabase::class.java, "country_database")
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // This will run on a background thread by Room
                        val countries = CountryDataLoader.loadCountriesFromAssets(context)
                        countries.forEach { country ->
                            val contentValues = android.content.ContentValues().apply {
                                put("code", country.code)
                                put("name", country.name)
                                put("region", country.region)
                                put("flagEmoji", country.flagEmoji)
                                put("visited", if (country.visited) 1 else 0)
                                put("notes", country.notes)
                                put("rating", country.rating)
                            }
                            db.insert(
                                "countries",
                                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE,
                                contentValues,
                            )
                        }
                    }
                })
                .build()
    }
}
