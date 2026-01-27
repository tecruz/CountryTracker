package com.tecruz.countrytracker.core.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for country operations.
 */
@Dao
interface CountryDao {

    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE visited = 1 ORDER BY visitedDate DESC")
    fun getVisitedCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE visited = 0 ORDER BY name ASC")
    fun getUnvisitedCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE region = :region ORDER BY name ASC")
    fun getCountriesByRegion(region: String): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE code = :code")
    suspend fun getCountryByCode(code: String): CountryEntity?

    @Query("SELECT COUNT(*) FROM countries WHERE visited = 1")
    fun getVisitedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM countries")
    fun getTotalCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountry(country: CountryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Update
    suspend fun updateCountry(country: CountryEntity)

    @Delete
    suspend fun deleteCountry(country: CountryEntity)

    @Query("DELETE FROM countries")
    suspend fun deleteAllCountries()

    @Query("SELECT DISTINCT region FROM countries ORDER BY region ASC")
    fun getAllRegions(): Flow<List<String>>
}
