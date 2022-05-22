package com.example.simpleweatherapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.data.bingmaps.MapsRepository
import com.example.simpleweatherapp.model.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val mapsRepository: MapsRepository
) : ViewModel() {

    private val _locations = MutableStateFlow<List<ShortLocation>>(emptyList())
    val locations: StateFlow<List<ShortLocation>> = _locations.asStateFlow()

    private val _mapsApiAvailable = MutableStateFlow(true)
    val mapsApiAvailable: StateFlow<Boolean> = _mapsApiAvailable.asStateFlow()

    fun queryLocationByName(name: String) {
        viewModelScope.launch {
            mapsRepository.getLocationsList(name).let { result ->
                if (result is Result.Success) {
                    _locations.value = result.data
                    _mapsApiAvailable.value = true
                } else {
                    _mapsApiAvailable.value = false
                }
            }
        }
    }
}