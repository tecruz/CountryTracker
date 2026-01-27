@file:OptIn(ExperimentalCoroutinesApi::class)

package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.features.countrydetail.domain.GetCountryByCodeUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsVisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsUnvisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryNotesUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryRatingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CountryDetailUiState(
    val country: Country? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    private val getCountryByCodeUseCase: GetCountryByCodeUseCase,
    private val markCountryAsVisitedUseCase: MarkCountryAsVisitedUseCase,
    private val markCountryAsUnvisitedUseCase: MarkCountryAsUnvisitedUseCase,
    private val updateCountryNotesUseCase: UpdateCountryNotesUseCase,
    private val updateCountryRatingUseCase: UpdateCountryRatingUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val countryCode: String = checkNotNull(savedStateHandle["countryCode"])

    private val _refreshTrigger = MutableStateFlow(0)
    private val _error = MutableStateFlow<String?>(null)
    private val _isSaving = MutableStateFlow(false)

    val uiState: StateFlow<CountryDetailUiState> = combine(
        _refreshTrigger.flatMapLatest {
            flow {
                emit(getCountryByCodeUseCase(countryCode))
            }.catch { e ->
                _error.value = e.message ?: "Failed to load country"
                emit(null)
            }
        },
        _error,
        _isSaving
    ) { country, error, isSaving ->
        CountryDetailUiState(
            country = country,
            isLoading = false,
            error = error,
            isSaving = isSaving
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CountryDetailUiState()
    )

    fun markAsVisited(date: Long, notes: String, rating: Int) {
        val currentCountry = uiState.value.country ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                markCountryAsVisitedUseCase(currentCountry, date, notes, rating)
                refreshCountry()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to mark as visited"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun markAsUnvisited() {
        val currentCountry = uiState.value.country ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                markCountryAsUnvisitedUseCase(currentCountry)
                refreshCountry()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to mark as unvisited"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateNotes(notes: String) {
        val currentCountry = uiState.value.country ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                updateCountryNotesUseCase(currentCountry, notes)
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
        val currentCountry = uiState.value.country ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                updateCountryRatingUseCase(currentCountry, rating)
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
