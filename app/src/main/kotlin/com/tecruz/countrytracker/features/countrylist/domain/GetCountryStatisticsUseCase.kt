package com.tecruz.countrytracker.features.countrylist.domain

import com.tecruz.countrytracker.features.countrylist.domain.repository.CountryListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case for retrieving country statistics.
 * Combines visited count and total count to provide comprehensive stats.
 */
class GetCountryStatisticsUseCase @Inject constructor(private val repository: CountryListRepository) {
    operator fun invoke(): Flow<CountryStatistics> = combine(
        repository.getVisitedCount(),
        repository.getTotalCount(),
    ) { visited, total ->
        CountryStatistics(
            visitedCount = visited,
            totalCount = total,
            percentage = if (total > 0) (visited * 100) / total else 0,
        )
    }
}

data class CountryStatistics(val visitedCount: Int, val totalCount: Int, val percentage: Int)
