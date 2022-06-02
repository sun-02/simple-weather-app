package com.example.simpleweatherapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources as Acr
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.ResourcesMapping.weatherIconsRes
import com.example.simpleweatherapp.ResourcesMapping.weatherImagesRes
import com.example.simpleweatherapp.SimpleWeatherApplication
import com.example.simpleweatherapp.databinding.FragmentSavedLocationsBinding
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.model.openweather.ShortWeather
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.getStatusBarHeight
import com.example.simpleweatherapp.util.intToSignedString
import com.example.simpleweatherapp.util.setToolbarLayoutTopMarginWithRespectOfStatusBarHeight
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class SavedLocationsFragment : Fragment(), OnItemClickListener {

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
        binding.toolbarSavedLocations
            .setToolbarLayoutTopMarginWithRespectOfStatusBarHeight(getStatusBarHeight())
        Timber.d("ToolbarLayoutTopMargin set")
        _application = requireContext().applicationContext as SimpleWeatherApplication

        var _currentWeather: OneCallWeather? = null
        lifecycleScope.launch {
            _currentWeather = viewModel.currentWeather.first()
            Timber.d("Got current weather = $_currentWeather")
        }

        val currentWeather = _currentWeather!!
        val weatherRes =
            weatherIconsRes[currentWeather.weatherIcon] ?: R.drawable.ic_unavailable
        val tempFormatted =
            getString(R.string.temp_n_dew_point_formatted, intToSignedString(currentWeather.temp))

        binding.currentLocation.apply {
            divFavRemove.visibility = View.GONE
            ivFavRemove.visibility = View.GONE
            tvFavLocation.text = currentWeather.name!!.split(", ")[0]
            ivFavWeather.setImageResource(weatherRes)
            tvFavTemp.text = tempFormatted
            Timber.d("Current weather UI is set")
        }

        val dailyForecastItemDivider = DividerItemDecoration(requireContext(),
            RecyclerView.VERTICAL)
        dailyForecastItemDivider.setDrawable(
            Acr.getDrawable(requireContext(), R.drawable.daily_forecast_item_divider)!!
        )
        binding.rvFavoriteLocations.addItemDecoration(dailyForecastItemDivider)
        binding.rvFavoriteLocations.adapter = favoriteLocationsAdapter
        Timber.d("RV for fav locations is set")
        Timber.d("Observing fav locations")
        viewModel.favLocationList.observe(viewLifecycleOwner) {
            _favLocations = it
            Timber.d("Got fav locations = $it")
            val favWeather = viewModel.getFavWeather()
            Timber.d("Got fav weather = $favWeather")
            favoriteLocationsAdapter.submitList(favWeather)
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        viewModel.removeFavLocation(favLocations[position].name)
    }

    override fun onDestroy() {
        super.onDestroy()
        _favWeatherAdapter = null
        _binding?.rvFavoriteLocations?.adapter = null
        _favWeatherAdapter = null
        _binding = null
    }
}