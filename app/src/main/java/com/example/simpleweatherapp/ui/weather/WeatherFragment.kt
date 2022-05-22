package com.example.simpleweatherapp.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.DateTimeFormatters.dateTimeFormatter
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.SimpleWeatherApplication
import com.example.simpleweatherapp.databinding.FragmentWeatherBinding
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.*
import com.example.simpleweatherapp.ResourcesMapping.uviIconsRes
import com.example.simpleweatherapp.ResourcesMapping.weatherImagesRes
import com.example.simpleweatherapp.ResourcesMapping.windDirectionsRes
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.*
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.time.LocalTime
import java.time.temporal.ChronoUnit


class WeatherFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
    DialogInterface.OnClickListener, EasyPermissions.PermissionCallbacks {

    companion object {
        private const val ACCESS_COARSE_LOCATION_PERMISSIONS_REQUEST = 1
    }

    private val args: WeatherFragmentArgs by navArgs()

    private var _application: SimpleWeatherApplication? = null
    private val application get() = _application!!

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory(
            application.mapsRepository,
            application.weatherRepository,
            this
        )
    }

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val hourlyForecastAdapter = HourlyForecastAdapter()

    private val dailyForecastAdapter = DailyForecastAdapter(this)

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
        Timber.d("onViewCreated start")
        binding.toolbarWeather
            .setToolbarLayoutTopMarginWithRespectOfStatusBarHeight(getStatusBarHeight())
        Timber.d("ToolbarLayoutTopMargin setted")

        _application = requireContext().applicationContext as SimpleWeatherApplication

        lifecycleScope.launchWhenStarted {
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
        Timber.d("Preparing weather UI")
        setupWeather()
        lifecycleScope.launchWhenStarted {
            viewModel.locationAndWeather.collect {
                Timber.d("Binding weather UI")
                bindWeather(it.first, it.second)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Refreshing location and weather")
        refreshWeather()
    }

    override fun onPause() {
        super.onPause()
        Timber.d("Paused")
    }

    private fun showLayout() {
        val views = binding.root.getAllViews()
        for (v in views) {
            v.visibility = View.VISIBLE
        }
        binding.tvWeatherUnavailable.visibility = View.GONE
        Timber.d("Layout showed, \"Weather unavailable sign hidden\"")
    }

    private fun hideLayout() {
        val views = binding.root.getAllViews()
        for (v in views) {
            v.visibility = View.GONE
        }
        binding.tvWeatherUnavailable.visibility = View.VISIBLE
        Timber.d("Layout hidden, \"Weather unavailable sign showed\"")
    }

    private fun setupWeather() {
        binding.apply {
            rvHourlyForecast.adapter = hourlyForecastAdapter
            rvDailyForecast.layoutManager =
                object : LinearLayoutManager(requireContext()){
                    override fun canScrollVertically(): Boolean { return false }
                }
            rvDailyForecast.adapter = dailyForecastAdapter
            val dailyForecastItemDivider = DividerItemDecoration(requireContext(),
                RecyclerView.VERTICAL)
            dailyForecastItemDivider.setDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.daily_forecast_item_divider)!!
            )
            rvDailyForecast.addItemDecoration(dailyForecastItemDivider)

            swiperefresh.setOnRefreshListener(this@WeatherFragment)
        }
        Timber.d("Weather UI is prepared")
    }

    private fun bindWeather(sLocation: ShortLocation, weather: OneCallWeather) {
        val weatherIconBigRes =
            weatherImagesRes[weather.weatherIcon] ?: R.drawable.ic_unavailable

        val temp = weather.temp.toInt()
        val tempFormatted = getString(R.string.temp_n_dew_point_formatted, intToSignedString(temp))

        val feelsLike = weather.feelsLike.toInt()
        val feelsLikeFormatted = getString(R.string.feels_like_formatted, intToSignedString(feelsLike))

        val textColor = getColor(requireContext(), R.color.text_color)
        
        val wind = weather.windSpeed.toInt().toString()
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
        
        val dewPoint = weather.dewPoint.toInt().toString()
        val dewPointFormatted = getString(R.string.temp_n_dew_point_formatted, dewPoint)
        val dewPointSpanned = paintStartValue(dewPointFormatted, dewPoint, textColor)

        val visibility = (weather.visibility / 1000).toString()
        val visibilityFormatted = getString(R.string.visibility_formatted, visibility)
        val visibilitySpanned = paintStartValue(visibilityFormatted, visibility, textColor)

        val uvi = weather.uvi.toInt().toString()
        val uviFormatted = getString(R.string.uvi_formatted, uvi)
        val uviSpanned = paintEndValue(uviFormatted, uvi, textColor)
        val uviIconRes = getResFromRange(uviIconsRes, weather.uvi) ?: R.drawable.ic_unavailable
        val uviIcon = AppCompatResources.getDrawable(requireContext(), uviIconRes)
        
        binding.apply {
            toolbarWeather.title =
                "${sLocation.populatedPlace}, ${weather.dateTime.format(dateTimeFormatter)}"
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
//        if (arguments != null) {
//            // Set with search results
//            viewModel.setWeather(args.newShortLocation)
//        } else {
            // Set using location API
            if (hasCoarseLocationPermission()) {
                Timber.d("Has location services permission")
                refreshWeatherWithCurrentLocation()
            } else {
                Timber.d("Doesn't has location services permission")
                requestCoarseLocationPermission()
            }
//        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshWeatherWithCurrentLocation() {
        Timber.d("Getting location from location services")
        application.fusedLocationClient.lastLocation.addOnCompleteListener { locationTask ->
            val lastLocation = locationTask.result
            if (lastLocation != null &&
                System.currentTimeMillis() - lastLocation.time < Const.LOCATION_REFRESH_INTERVAL) {
                Timber.d("Setting VM with last location")
                viewModel.setWeather(lastLocation)
            } else {
                application.fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).addOnCompleteListener {
                    val currentLocation = locationTask.result
                    if (currentLocation != null) {
                        Timber.d("Setting VM with current location")
                        viewModel.setWeather(currentLocation)
                    } else {
                        Timber.d("Setting VM with no location")
                        viewModel.setWeather()
                    }
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _application = null
        Timber.d("_binding and _application cleared")
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
}