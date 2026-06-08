package com.tecruz.countrytracker.features.countrydetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.core.domain.DataError
import com.tecruz.countrytracker.core.domain.model.Country
import com.tecruz.countrytracker.core.presentation.UiText
import com.tecruz.countrytracker.core.presentation.toUiText
import com.tecruz.countrytracker.core.util.DispatcherProvider
import com.tecruz.countrytracker.features.countrydetail.domain.GetCountryByCodeUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsUnvisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.MarkCountryAsVisitedUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryNotesUseCase
import com.tecruz.countrytracker.features.countrydetail.domain.UpdateCountryRatingUseCase
import com.tecruz.countrytracker.features.countrydetail.presentation.model.toUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CountryDetailViewModel(
    getCountryByCodeUseCase: GetCountryByCodeUseCase,
    private val markCountryAsVisitedUseCase: MarkCountryAsVisitedUseCase,
    private val markCountryAsUnvisitedUseCase: MarkCountryAsUnvisitedUseCase,
    private val updateCountryNotesUseCase: UpdateCountryNotesUseCase,
    private val updateCountryRatingUseCase: UpdateCountryRatingUseCase,
    savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    private val countryCode: String = checkNotNull(savedStateHandle["countryCode"])

    private val _internalState = MutableStateFlow(InternalState())

    val state: StateFlow<CountryDetailState> = combine(
        getCountryByCodeUseCase(countryCode),
        _internalState,
    ) { country, internal ->
        currentCountryDomain = country
        CountryDetailState(
            country = country?.toUi(),
            isLoading = internal.isLoading,
            isSaving = internal.isSaving,
            error = internal.error ?: if (country == null && !internal.isLoading) {
                DataError.Local.NOT_FOUND.toUiText()
            } else {
                null
            },
        )
    }.onStart {
        _internalState.update { it.copy(isLoading = false) } // Initially false since flow will emit
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CountryDetailState(isLoading = true),
    )

    private val _events = Channel<CountryDetailEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var currentCountryDomain: Country? = null

    fun onAction(action: CountryDetailAction) {
        when (action) {
            is CountryDetailAction.OnMarkAsVisited -> markAsVisited(action.date, action.notes, action.rating)
            is CountryDetailAction.OnMarkAsUnvisited -> markAsUnvisited()
            is CountryDetailAction.OnUpdateNotes -> updateNotes(action.notes)
            is CountryDetailAction.OnUpdateRating -> updateRating(action.rating)
            is CountryDetailAction.OnClearError -> _internalState.update { it.copy(error = null) }
        }
    }

    private fun markAsVisited(date: Long, notes: String, rating: Int) {
        val country = currentCountryDomain ?: return
        viewModelScope.launch(dispatcherProvider.io) {
            _internalState.update { it.copy(isSaving = true) }
            try {
                markCountryAsVisitedUseCase(country, date, notes, rating)
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to mark as visited", e)
                _internalState.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
            } finally {
                _internalState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun markAsUnvisited() {
        val country = currentCountryDomain ?: return
        viewModelScope.launch(dispatcherProvider.io) {
            _internalState.update { it.copy(isSaving = true) }
            try {
                markCountryAsUnvisitedUseCase(country)
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to mark as unvisited", e)
                _internalState.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
            } finally {
                _internalState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun updateNotes(notes: String) {
        val country = currentCountryDomain ?: return
        viewModelScope.launch(dispatcherProvider.io) {
            _internalState.update { it.copy(isSaving = true) }
            try {
                updateCountryNotesUseCase(country, notes)
            } catch (e: IllegalArgumentException) {
                android.util.Log.e("CountryDetailVM", "Invalid notes", e)
                _internalState.update { it.copy(error = UiText.DynamicString(e.message ?: "Invalid notes")) }
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to update notes", e)
                _internalState.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
            } finally {
                _internalState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun updateRating(rating: Int) {
        val country = currentCountryDomain ?: return
        viewModelScope.launch(dispatcherProvider.io) {
            _internalState.update { it.copy(isSaving = true) }
            try {
                updateCountryRatingUseCase(country, rating)
            } catch (e: IllegalArgumentException) {
                android.util.Log.e("CountryDetailVM", "Invalid rating", e)
                _internalState.update { it.copy(error = UiText.DynamicString(e.message ?: "Invalid rating")) }
            } catch (e: Exception) {
                android.util.Log.e("CountryDetailVM", "Failed to update rating", e)
                _internalState.update { it.copy(error = DataError.Local.UNKNOWN.toUiText()) }
            } finally {
                _internalState.update { it.copy(isSaving = false) }
            }
        }
    }

    private data class InternalState(
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val error: UiText? = null,
    )
}
