package com.example.simpleweatherapp.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.simpleweatherapp.SimpleWeatherApplication
import com.example.simpleweatherapp.databinding.FragmentSearchBinding
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.ui.OnItemClickListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchFragment : Fragment(), TextWatcher, OnItemClickListener {
    private val viewModel: SearchViewModel by viewModels {
        val application = requireContext().applicationContext as SimpleWeatherApplication
        SearchViewModelFactory(application.mapsRepository)
    }
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationsListAdapter: LocationsListAdapter
    private lateinit var shortLocations: List<ShortLocation>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set the toolbar top margin with respect of the status bar
        (binding.searchToolbar.layoutParams as ViewGroup.MarginLayoutParams)
            .topMargin = binding.searchToolbarLayout.marginTop + getStatusBarHeight()

        binding.searchToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchEditText.addTextChangedListener(this)

        val recyclerView = binding.searchRecyclerView
        locationsListAdapter = LocationsListAdapter(this)
        recyclerView.adapter = locationsListAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.locations.collect { locations ->
                    this@SearchFragment.shortLocations = locations.distinct()
                    locationsListAdapter.submitList(locations)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mapsApiAvailable.collect { mapsApiAvailable ->
                    if (mapsApiAvailable) {
                        binding.noWifiImageView.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        binding.noWifiImageView.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (s!!.length >= 2) {
            viewModel.queryLocation(s.toString())
        }
    }

    override fun onItemClick(v: View?, position: Int) {
        val action =
            SearchFragmentDirections.actionToCurrentWeatherFragment(shortLocations[position])
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}