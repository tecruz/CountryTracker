@file:OptIn(ExperimentalCoroutinesApi::class)

package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.core.navigation.Screen
import com.tecruz.countrytracker.features.countrydetail.domain.GetCountryByCodeUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsUnvisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsVisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryNotesUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryRatingUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.presentation.model.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    private val getCountryByCodeUseCase: GetCountryByCodeUseCase,
    private val markCountryAsVisitedUseCase: MarkCountryAsVisitedUseCase,
    private val markCountryAsUnvisitedUseCase: MarkCountryAsUnvisitedUseCase,
    private val updateCountryNotesUseCase: UpdateCountryNotesUseCase,
    private val updateCountryRatingUseCase: UpdateCountryRatingUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val countryCode: String = checkNotNull(savedStateHandle[Screen.CountryDetail.ARG_COUNTRY_CODE])

    private val _refreshTrigger = MutableStateFlow(0)
    private val _error = MutableStateFlow<String?>(null)
    private val _isSaving = MutableStateFlow(false)

    // Thread-safe storage of domain model for operations
    private val _currentCountryDomain = MutableStateFlow<CountryDetail?>(null)

    val uiState: StateFlow<CountryDetailUiState> = combine(
        _refreshTrigger.flatMapLatest {
            flow {
                val country = getCountryByCodeUseCase(countryCode)
                _currentCountryDomain.value = country
                emit(country)
            }.catch { e ->
                _error.value = e.message ?: "Failed to load country"
                emit(null)
            }
        },
        _error,
        _isSaving,
    ) { country, error, isSaving ->
        CountryDetailUiState(
            country = country?.toUi(),
            isLoading = false,
            error = error,
            isSaving = isSaving,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CountryDetailUiState(),
    )

    fun markAsVisited(date: Long, notes: String, rating: Int) {
        val country = _currentCountryDomain.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                markCountryAsVisitedUseCase(country, date, notes, rating)
                refreshCountry()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to mark as visited"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun markAsUnvisited() {
        val country = _currentCountryDomain.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                markCountryAsUnvisitedUseCase(country)
                refreshCountry()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to mark as unvisited"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateNotes(notes: String) {
        val country = _currentCountryDomain.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                updateCountryNotesUseCase(country, notes)
                refreshCountry()
            } catch (e: IllegalArgumentException) {
                _error.value = e.message ?: "Invalid notes"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update notes"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateRating(rating: Int) {
        val country = _currentCountryDomain.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                updateCountryRatingUseCase(country, rating)
                refreshCountry()
            } catch (e: IllegalArgumentException) {
                _error.value = e.message ?: "Invalid rating"
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update rating"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun refreshCountry() {
        _refreshTrigger.value++
    }
}
