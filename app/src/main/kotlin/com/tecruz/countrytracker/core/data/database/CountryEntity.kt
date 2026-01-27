package com.tecruz.countrytracker.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tecruz.countrytracker.core.domain.model.Country

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
    val flagEmoji: String = ""
)

/**
 * Extension function to convert entity to domain model
 */
fun CountryEntity.toDomain(): Country {
    return Country(
        code = code,
        name = name,
        region = region,
        visited = visited,
        visitedDate = visitedDate,
        notes = notes,
        rating = rating,
        flagEmoji = flagEmoji
    )
}

/**
 * Extension function to convert domain model to entity
 */
fun Country.toEntity(): CountryEntity {
    return CountryEntity(
        code = code,
        name = name,
        region = region,
        visited = visited,
        visitedDate = visitedDate,
        notes = notes,
        rating = rating,
        flagEmoji = flagEmoji
    )
}
