package com.example.simpleweatherapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpleweatherapp.data.bingmaps.MapsRepository

@Suppress("UNCHECKED_CAST")
class SearchViewModelFactory(
    private val mapsRepository: MapsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
    ) = with(modelClass) {
        when {
            isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(mapsRepository)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}