package com.tecruz.countrytracker.features.countrylist.domain

import com.tecruz.countrytracker.core.domain.repository.CountryRepository
import com.tecruz.countrytracker.features.countrylist.domain.model.CountryStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.math.roundToInt

/**
 * Use case for retrieving country statistics.
 * Combines visited count and total count to provide comprehensive stats.
 */
class GetCountryStatisticsUseCase(private val repository: CountryRepository) {
    operator fun invoke(): Flow<CountryStatistics> = combine(
        repository.getVisitedCount(),
        repository.getTotalCount(),
    ) { visited, total ->
        CountryStatistics(
            visitedCount = visited,
            totalCount = total,
            percentage = if (total > 0) ((visited * 100.0) / total).roundToInt() else 0,
        )
    }
}
