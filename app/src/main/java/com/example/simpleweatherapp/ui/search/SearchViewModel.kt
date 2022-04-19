package com.example.simpleweatherapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweatherapp.model.Location
import com.example.simpleweatherapp.data.MapsRepository
import com.example.simpleweatherapp.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val mapsRepository: MapsRepository
    ) : ViewModel() {

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()

    private val _mapsApiAvailable = MutableStateFlow(true)
    val mapsApiAvailable: StateFlow<Boolean> = _mapsApiAvailable.asStateFlow()

    fun queryLocation(query: String) {
        viewModelScope.launch {
            mapsRepository.getLocations(query).let { result ->
                if (result is Result.Success) {
                    _locations.value = result.data.locations
                    _mapsApiAvailable.value = true
                } else {
                    _mapsApiAvailable.value = false
                }
            }
        }
    }
}