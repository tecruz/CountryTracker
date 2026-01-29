package com.tecruz.countrytracker.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing country data.
 */
@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey
    val code: String,
    val name: String,
    val region: String,
    val visited: Boolean = false,
    val visitedDate: Long? = null,
    val notes: String = "",
    val rating: Int = 0,
    val flagEmoji: String = "",
)
