package com.example.simpleweatherapp.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.DateTimeFormatters.timeFormatter
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.ResourcesMapping.uviIconsRes
import com.example.simpleweatherapp.ResourcesMapping.weatherImagesRes
import com.example.simpleweatherapp.ResourcesMapping.windDirectionsRes
import com.example.simpleweatherapp.SimpleWeatherApplication
import com.example.simpleweatherapp.databinding.FragmentWeatherBinding
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime
import java.time.temporal.ChronoUnit


class WeatherFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
    DialogInterface.OnClickListener, View.OnClickListener, EasyPermissions.PermissionCallbacks,
    Toolbar.OnMenuItemClickListener {

    companion object {
        private const val ACCESS_COARSE_LOCATION_PERMISSIONS_REQUEST = 1
    }

    private var isFavorite: Boolean = false

    private val args: WeatherFragmentArgs by navArgs()

    private var _application: SimpleWeatherApplication? = null
    private val application get() = _application!!

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory(
            application.mapsRepository,
            application.weatherRepository,
            requireActivity()
        )
    }

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private var _hourlyForecastAdapter: HourlyForecastAdapter? = null
    private val hourlyForecastAdapter get() = _hourlyForecastAdapter!!

    private var _dailyForecastAdapter: DailyForecastAdapter? = null
    private val dailyForecastAdapter get() = _dailyForecastAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        Timber.d("Binding initialized")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated started")
        _application = requireContext().applicationContext as SimpleWeatherApplication
        _dailyForecastAdapter = DailyForecastAdapter(this)
        _hourlyForecastAdapter = HourlyForecastAdapter()
        Timber.d("Preparing weather UI")
        setupWeather()
    }

    private fun setupWeather() {
        lifecycleScope.launch {
            Timber.d("Collecting weather availability")
            viewModel.isWeatherAvailable.collect { isAvailable ->
                if (!isAvailable) {
                    val dialog = ApiUnavailableDialogFragment()
                    dialog.setTargetFragment(this@WeatherFragment, 1)
                    Timber.d("Navigating to ApiUnavailableDialogFragment")
                    dialog.show(
                        parentFragmentManager, ApiUnavailableDialogFragment.TAG)
                }
            }
        }
        binding.apply {
            toolbarWeather.apply {
                Timber.d("Toolbar setup")
                setToolbarLayoutTopMarginWithRespectOfStatusBarHeight(getStatusBarHeight())
                Timber.d("ToolbarLayoutTopMargin is set")
                setNavigationOnClickListener(this@WeatherFragment)
                setOnMenuItemClickListener(this@WeatherFragment)
            }

            rvHourlyForecast.adapter = hourlyForecastAdapter
            rvDailyForecast.apply {
                layoutManager =
                    object : LinearLayoutManager(requireContext()){
                        override fun canScrollVertically(): Boolean { return false }
                    }
                adapter = dailyForecastAdapter
                val dailyForecastItemDivider = DividerItemDecoration(requireContext(),
                    RecyclerView.VERTICAL)
                dailyForecastItemDivider.setDrawable(
                    AppCompatResources.getDrawable(requireContext(), R.drawable.daily_forecast_item_divider)!!
                )
                addItemDecoration(dailyForecastItemDivider)
            }
            swiperefresh.setOnRefreshListener(this@WeatherFragment)
        }

        lifecycleScope.launch {
            Timber.d("Observing addTo/removeFrom favorites button click")
            viewModel.favLocationList.observe(viewLifecycleOwner) { locations ->
                setFavorite(locations)
            }
        }
        Timber.d("Weather UI is prepared")


        lifecycleScope.launch {
            Timber.d("Collecting weather changes")
            viewModel.currentWeather.collect {
                bindWeather(it)
            }
        }

        Timber.d("Refreshing location and weather")
        refreshWeather()
    }

    private fun setFavorite(locations: List<ShortLocation>) {
        val sLocation = viewModel.savedState.get<ShortLocation>(Const.LAST_LOCATION_KEY)
        binding.toolbarWeather.menu!!.getItem(0).apply {
            if (locations.contains(sLocation)) {
                isFavorite = true
                setIcon(R.drawable.ic_heart_filled_24dp)
            } else {
                isFavorite = false
                setIcon(R.drawable.ic_heart_24dp)
            }
            Timber.d("Weather isFavorite = $isFavorite")
        }
    }

    private fun bindWeather(weather: OneCallWeather) {
        Timber.d("Binding weather UI")
        val titlePopPlace = weather.name!!.split(", ")[0]
        val titleTime = weather.dateTime.toLocalTime().format(timeFormatter)

        val weatherIconBigRes =
            weatherImagesRes[weather.weatherIcon] ?: R.drawable.ic_unavailable

        val temp = weather.temp
        val tempFormatted = getString(R.string.temp_n_dew_point_formatted, intToSignedString(temp))

        val feelsLike = weather.feelsLike
        val feelsLikeFormatted = getString(R.string.feels_like_formatted, intToSignedString(feelsLike))

        val textColor = getColor(requireContext(), R.color.text_color)
        
        val wind = weather.windSpeed.toString()
        val windFormatted = getString(R.string.wind_speed_formatted, wind)
        val windSpanned = paintStartValue(windFormatted, wind, textColor)
        val windDirRes = getResFromRange(windDirectionsRes, weather.windDeg)
        val windDirText = getString(windDirRes ?: R.string.unavailable)
        val windArrow = AppCompatResources.getDrawable(
            requireContext(), R.drawable.ic_arrow_rotate)!!
        windArrow.level = (weather.windDeg * Const.DEGREE_TO_LEVEL_COEF).toInt()
        
        val humidity = weather.humidity.toString()
        val humidityFormatted = getString(R.string.humidity_formatted, humidity, "%")
        val humiditySpanned = paintStartValue(humidityFormatted, humidity, textColor)

        val pressure = weather.pressure.toString()
        val pressureFormatted = getString(R.string.pressure_formatted, pressure)
        val pressureSpanned = paintStartValue(pressureFormatted, pressure, textColor)
        
        val dewPoint = weather.dewPoint.toString()
        val dewPointFormatted = getString(R.string.temp_n_dew_point_formatted, dewPoint)
        val dewPointSpanned = paintStartValue(dewPointFormatted, dewPoint, textColor)

        val visibility = (weather.visibility / 1000).toString()
        val visibilityFormatted = getString(R.string.visibility_formatted, visibility)
        val visibilitySpanned = paintStartValue(visibilityFormatted, visibility, textColor)

        val uvi = weather.uvi.toString()
        val uviFormatted = getString(R.string.uvi_formatted, uvi)
        val uviSpanned = paintEndValue(uviFormatted, uvi, textColor)
        val uviIconRes = getResFromRange(uviIconsRes, weather.uvi) ?: R.drawable.ic_unavailable
        val uviIcon = AppCompatResources.getDrawable(requireContext(), uviIconRes)
        
        binding.apply {
            toolbarWeather.title = "$titlePopPlace, $titleTime"
            tvCurrentTemp.text = tempFormatted
            tvCurrentWeather.text = weather.weatherTitle
            ivWeather.setImageResource(weatherIconBigRes)
            tvCurrentFeelsLike.text = feelsLikeFormatted
            tvCurrentWind.text = windSpanned
            tvCurrentWindDeg.text = windDirText
            tvCurrentWindDeg.setCompoundDrawablesWithIntrinsicBounds(
                windArrow, null, null, null)
            tvCurrentHumidity.text = humiditySpanned
            tvCurrentPressure.text = pressureSpanned
            tvCurrentDewPoint.text = dewPointSpanned
            tvCurrentVisibility.text = visibilitySpanned
            tvCurrentUvi.text = uviSpanned
            tvCurrentUvi.setCompoundDrawablesWithIntrinsicBounds(
                uviIcon, null, null, null)

            hourlyForecastAdapter.submitList(weather.hourlyForecast)
            dailyForecastAdapter.submitList(weather.dailyForecast)

            // Replacing stubs with actual views before turning skeleton off
            currentTempAndWeatherStub.visibility = View.GONE
            rvHourlyForecastStub.root.visibility = View.GONE
            rvHourlyForecast.visibility = View.VISIBLE
            rvDailyForecastStub.root.visibility = View.GONE
            rvDailyForecast.visibility = View.VISIBLE

            setFavorite(viewModel.favLocationList.value!!)

            swiperefresh.isRefreshing = false
            Timber.d("Weather UI binded")

            binding.skeletonLayout.showOriginal()
            Timber.d("Skeleton is off")

            val now = LocalTime.now()
            val weatherTimeStamp = weather.dateTime.toLocalTime()
            if (weatherTimeStamp.until(now, ChronoUnit.MINUTES) > 5) {
                showDataOutdatedSnackbar()
            }
        }
    }

    private fun showDataOutdatedSnackbar() {
        Snackbar.make(binding.root, R.string.data_outdated, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.refresh) {
                Timber.d("Refreshing weather UI from DataOutdated snackbar")
                refreshWeather()
            }.show()
    }

    private fun refreshWeather() {
        binding.skeletonLayout.showSkeleton()
        Timber.d("Skeleton is on")
        if (args.newShortLocation != null) {
            Timber.d("Set VM weather from args")
            viewModel.refreshWeather(args.newShortLocation!!)
            return
        }
        Timber.d("Set VM weather with location API")
        if (hasCoarseLocationPermission()) {
            Timber.d("Has location services permission")
            refreshWeatherWithCurrentLocation()
        } else {
            Timber.d("Hasn't location services permission")
            requestCoarseLocationPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshWeatherWithCurrentLocation() {
        Timber.d("Getting location from fusedLocationClient")
        application.fusedLocationClient.lastLocation.addOnCompleteListener { locationTask ->
//            val lastLocation = locationTask.result
//            if (lastLocation != null &&
//                System.currentTimeMillis() - lastLocation.time < Const.LOCATION_REFRESH_INTERVAL) {
//                Timber.d("Setting VM with last location services location")
//                viewModel.setWeather(lastLocation)
//            } else {
                application.fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).addOnCompleteListener {
                    val currentLocation = locationTask.result
                    if (currentLocation != null) {
                        Timber.d("Got current location:(lat=${currentLocation.latitude}, " +
                                "lon=${currentLocation.longitude})from fusedLocationClient. Setting VM")
                        viewModel.refreshWeather(currentLocation)
                    } else {
                        Timber.d("fusedLocationClient returned null. Setting VM with default location if present")
                        viewModel.refreshWeather()
                    }
                }
//            }
        }
    }

    private fun hasCoarseLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    private fun requestCoarseLocationPermission() =
        EasyPermissions.requestPermissions(
            this,
            "To show weather relevant to you location, app needs access to Location API",
            ACCESS_COARSE_LOCATION_PERMISSIONS_REQUEST,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Timber.d("Permission granted after request. Refreshing location and weather")
        refreshWeatherWithCurrentLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Timber.d("Permission permanently denied after request. Showing system permission dialog")
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            Timber.d("Permission denied after request. Requesting again with rationale")
            requestCoarseLocationPermission()
        }
    }

    override fun onRefresh() {
        Timber.d("Refreshing weather UI with swipe")
        binding.skeletonLayout.showSkeleton()
        refreshWeather()
    }

    override fun onItemClick(view: View?, position: Int) {
        val action = WeatherFragmentDirections.actionToDailyForecastExtendedFragment(position)
        findNavController().navigate(action)
        Timber.d("Navigating to ForecastExtendedFragment")
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                Timber.d("Refreshing weather UI from ApiUnavailable dialog")
                (dialog as AlertDialog).dismiss()
                refreshWeather()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                Timber.d("ApiUnavailable dialog cancelled. Exiting app")
                (dialog as AlertDialog).dismiss()
                requireActivity().moveTaskToBack(true)
                requireActivity().finish()
            }
        }
    }

    override fun onClick(v: View?) {
        if (v is ImageButton) {
            Timber.d("Got toolbar nav button click. Navigating to SavedLocationsFragment")
            val action = WeatherFragmentDirections.actionToSavedLocationsFragment()
            findNavController().navigate(action)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean =
        when (item!!.itemId) {
            R.id.add_to_remove_from_favorites_button -> {
                Timber.d("Got \"add_to_remove_from_favorites_button\" click")
                val currentLocation = viewModel.savedState.get<ShortLocation>(Const.LAST_LOCATION_KEY)
                Timber.d("Got sLocation = $currentLocation from saved state")
                if (isFavorite) {
                    viewModel.removeFavLocation(currentLocation!!.name)
                    Timber.d("Removed sLocation = $currentLocation from favorites")
                } else {
                    viewModel.addFavLocation(currentLocation!!)
                    Timber.d("Added sLocation = $currentLocation to favorites")
                }
                true
            }
            else -> false
        }


    override fun onDestroyView() {
        super.onDestroyView()
        _hourlyForecastAdapter = null
        _dailyForecastAdapter = null
        binding.rvHourlyForecast.adapter = null
        binding.rvDailyForecast.adapter = null
        _binding = null
        _application = null
        Timber.d("_binding, adapters and _application cleared")
    }
}