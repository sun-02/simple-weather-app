package com.example.simpleweatherapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.ResourcesMapping.weatherIconsRes
import com.example.simpleweatherapp.SimpleWeatherApplication
import com.example.simpleweatherapp.databinding.FragmentSavedLocationsBinding
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.getStatusBarHeight
import com.example.simpleweatherapp.util.intToSignedString
import com.example.simpleweatherapp.util.setToolbarLayoutTopMarginWithRespectOfStatusBarHeight
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import androidx.appcompat.content.res.AppCompatResources as Acr

class SavedLocationsFragment : Fragment(), OnItemClickListener, View.OnClickListener,
    Toolbar.OnMenuItemClickListener {

    private var _binding: FragmentSavedLocationsBinding? = null
    private val binding get() = _binding!!

    private var _favWeatherAdapter: FavWeatherAdapter? = FavWeatherAdapter(this)
    private val favoriteLocationsAdapter get() = _favWeatherAdapter!!

    private var _favLocations: List<ShortLocation>? = null
    private val favLocations get() = _favLocations!!

    private var _application: SimpleWeatherApplication? = null
    private val application get() = _application!!

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory(
            application.mapsRepository,
            application.weatherRepository,
            requireActivity()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedLocationsBinding.inflate(inflater, container, false)
        Timber.d("Binding initialized")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated start")
        binding.toolbarSavedLocations.apply {
            setToolbarLayoutTopMarginWithRespectOfStatusBarHeight(getStatusBarHeight())
            setNavigationOnClickListener(this@SavedLocationsFragment)
            setOnMenuItemClickListener(this@SavedLocationsFragment)
        }
        Timber.d("ToolbarLayoutTopMargin set")
        _application = requireContext().applicationContext as SimpleWeatherApplication

        viewModel.favLocationList.observe(viewLifecycleOwner) {
            _favLocations = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val currentWeather = viewModel.currentLocationWeather.first()
                Timber.d("Got current weather = $currentWeather")
                val weatherRes =
                    weatherIconsRes[currentWeather.weatherIcon] ?: R.drawable.ic_unavailable
                val tempFormatted =
                    getString(R.string.temp_formatted, intToSignedString(currentWeather.temp))

                binding.inclCurrentLocation.apply {
                    divFavRemove.visibility = View.GONE
                    ivFavRemove.visibility = View.GONE
                    tvFavLocation.text = currentWeather.name.split(", ")[0]
                    ivFavWeather.setImageResource(weatherRes)
                    tvFavTemp.text = tempFormatted
                    root.setOnClickListener(this@SavedLocationsFragment)
                    Timber.d("Current weather UI is set")
                }
            }
        }

        val dailyForecastItemDivider = DividerItemDecoration(requireContext(),
            RecyclerView.VERTICAL)
        dailyForecastItemDivider.setDrawable(
            Acr.getDrawable(requireContext(), R.drawable.daily_forecast_item_divider)!!
        )
        binding.rvFavoriteLocations.addItemDecoration(dailyForecastItemDivider)
        binding.rvFavoriteLocations.adapter = favoriteLocationsAdapter
        Timber.d("RV for fav locations is set")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Timber.d("Collecting fav weather")
                viewModel.favWeatherList.collect {
                    favoriteLocationsAdapter.submitList(it)
                    Timber.d("Fav weather $it submitted to adapter")
                }
            }
        }
        viewModel.refreshFavWeather()
    }

    override fun onItemClick(v: View?, position: Int) {
        val sLocation = favLocations[position]
        if (v!!.id == R.id.iv_fav_remove) {
            Timber.d("Got item $sLocation delete button click. Deleting item")
            viewModel.removeFavLocation(sLocation)
        } else {
            Timber.d("Got item $sLocation view click. Navigating to WeatherFragment")
            val action = SavedLocationsFragmentDirections.actionToCurrentWeatherFragment(sLocation)
            findNavController().navigate(action)
        }
    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.incl_current_location) {
            Timber.d("Got current location view click. Navigating to WeatherFragment")
            val action = SavedLocationsFragmentDirections.actionToCurrentWeatherFragment()
            findNavController().navigate(action)
        }
        if (v is ImageButton) {
            Timber.d("Got toolbar back click. Navigating up")
            findNavController().navigateUp()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.find_locations_by_name_button) {
            Timber.d("Got menu search item click. Navigating to SearchFragment")
            val action = SavedLocationsFragmentDirections.actionToSearchFragment()
            findNavController().navigate(action)
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        _favWeatherAdapter = null
        _binding?.rvFavoriteLocations?.adapter = null
        _binding = null
    }
}