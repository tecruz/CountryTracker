package com.tecruz.countrytracker.core.data.datasource

import android.content.Context
import android.util.Log
import com.tecruz.countrytracker.core.data.database.CountryEntity
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Utility class for loading country data from assets.
 */
object CountryDataLoader {

    private const val TAG = "CountryDataLoader"

    /**
     * Load all country metadata from assets.
     */
    fun loadCountriesFromAssets(context: Context): List<CountryEntity> {
        val jsonString = try {
            context.assets
                .open("countries_metadata.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read countries_metadata.json from assets", e)
            return emptyList()
        }

        val jsonObject = try {
            JSONObject(jsonString)
        } catch (e: JSONException) {
            Log.e(TAG, "Failed to parse countries JSON", e)
            return emptyList()
        }

        val countries = mutableListOf<CountryEntity>()

        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val code = keys.next()
            try {
                val countryData = jsonObject.getJSONObject(code)
                countries.add(
                    CountryEntity(
                        code = code,
                        name = countryData.getString("name"),
                        region = countryData.getString("continent"),
                        flagEmoji = countryData.getString("flag"),
                    ),
                )
            } catch (e: JSONException) {
                Log.w(TAG, "Skipping malformed entry for country code: $code", e)
            }
        }

        return countries.sortedBy { it.name }
    }
}
