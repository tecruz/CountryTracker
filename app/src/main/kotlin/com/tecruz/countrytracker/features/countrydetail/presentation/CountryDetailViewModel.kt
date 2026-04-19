package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.core.domain.DataError
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.core.presentation.toUiText
import com.tecruz.countrytracker.core.util.DispatcherProvider
import com.tecruz.countrytracker.features.countrydetail.domain.GetCountryByCodeUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsUnvisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsVisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryNotesUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryRatingUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.model.CountryDetail
import com.tecruz.countrytracker.features.countrydetail.presentation.model.toUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountryDetailViewModel(
    private val getCountryByCodeUseCase: GetCountryByCodeUseCase,
    private val markCountryAsVisitedUseCase: MarkCountryAsVisitedUseCase,
    private val markCountryAsUnvisitedUseCase: MarkCountryAsUnvisitedUseCase,
    private val updateCountryNotesUseCase: UpdateCountryNotesUseCase,
    private val updateCountryRatingUseCase: UpdateCountryRatingUseCase,
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val countryCode: String = checkNotNull(savedStateHandle["countryCode"])

    private val _state = MutableStateFlow(CountryDetailState())
    val state: StateFlow<CountryDetailState> = _state.asStateFlow()

    private val _events = Channel<CountryDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var currentCountryDomain: CountryDetail? = null

    init {
        loadCountry()
    }

    private fun loadCountry() {
        viewModelScope.launch(dispatcherProvider.main) {
            _state.update { it.copy(isLoading = true) }
            try {
                val country = getCountryByCodeUseCase(countryCode)
                if (country != null) {
                    currentCountryDomain = country
                    _state.update {
                        it.copy(
                            country = country.toUi(),
                            isLoading = false,
                            error = null,
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = DataError.Local.NOT_FOUND.toUiText(),
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to load country", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = DataError.Local.UNKNOWN.toUiText(),
                    )
                }
            }
        }
    }

    fun onAction(action: CountryDetailAction) {
        when (action) {
            is CountryDetailAction.OnMarkAsVisited -> markAsVisited(action.date, action.notes, action.rating)
            is CountryDetailAction.OnMarkAsUnvisited -> markAsUnvisited()
            is CountryDetailAction.OnUpdateNotes -> updateNotes(action.notes)
            is CountryDetailAction.OnUpdateRating -> updateRating(action.rating)
            is CountryDetailAction.OnClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun markAsVisited(date: Long, notes: String, rating: Int) {
        val country = currentCountryDomain ?: return
        viewModelScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isSaving = true) }
            try {
                markCountryAsVisitedUseCase(country, date, notes, rating)
                loadCountry()
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to mark as visited", e)
                _state.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun markAsUnvisited() {
        val country = currentCountryDomain ?: return
        viewModelScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isSaving = true) }
            try {
                markCountryAsUnvisitedUseCase(country)
                loadCountry()
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to mark as unvisited", e)
                _state.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun updateNotes(notes: String) {
        val country = currentCountryDomain ?: return
        viewModelScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isSaving = true) }
            try {
                updateCountryNotesUseCase(country, notes)
                loadCountry()
            } catch (e: IllegalArgumentException) {
                android.util.Log.e("CountryDetailVM", "Invalid notes", e)
                _state.update { it.copy(error = UiText.DynamicString(e.message ?: "Invalid notes")) }
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to update notes", e)
                _state.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun updateRating(rating: Int) {
        val country = currentCountryDomain ?: return
        viewModelScope.launch(dispatcherProvider.io) {
            _state.update { it.copy(isSaving = true) }
            try {
                updateCountryRatingUseCase(country, rating)
                loadCountry()
            } catch (e: IllegalArgumentException) {
                android.util.Log.e("CountryDetailVM", "Invalid rating", e)
                _state.update { it.copy(error = UiText.DynamicString(e.message ?: "Invalid rating")) }
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to update rating", e)
                _state.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }
}
