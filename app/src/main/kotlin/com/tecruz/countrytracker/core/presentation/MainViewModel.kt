package com.tecruz.countrytracker.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecruz.countrytracker.core.domain.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Main ViewModel to handle app-wide initialization and state.
 */
class MainViewModel(private val mapRepository: MapRepository) : ViewModel() {

    private val _isInitialized = MutableStateFlow(mapRepository.isMapDataLoaded())
    val isInitialized = _isInitialized.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        if (!mapRepository.isMapDataLoaded()) {
            viewModelScope.launch {
                mapRepository.loadMapData()
                _isInitialized.value = true
            }
        }
    }
}
