package com.tecruz.countrytracker.core.data.datasource

import android.content.Context
import com.tecruz.countrytracker.core.data.database.CountryEntity
import org.json.JSONObject

/**
 * Utility class for loading country data from assets.
 */
object CountryDataLoader {

    /**
     * Load all country metadata from assets.
     */
    fun loadCountriesFromAssets(context: Context): List<CountryEntity> {
        val jsonString = context.assets
            .open("countries_metadata.json")
            .bufferedReader()
            .use { it.readText() }

        val jsonObject = JSONObject(jsonString)
        val countries = mutableListOf<CountryEntity>()

        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val code = keys.next()
            val countryData = jsonObject.getJSONObject(code)

            countries.add(
                CountryEntity(
                    code = code,
                    name = countryData.getString("name"),
                    region = countryData.getString("continent"),
                    flagEmoji = countryData.getString("flag")
                )
            )
        }

        return countries.sortedBy { it.name }
    }
}
