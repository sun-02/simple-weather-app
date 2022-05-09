package com.example.simpleweatherapp.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.drawable.RotateDrawable
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.SimpleWeatherApplication
import com.example.simpleweatherapp.databinding.FragmentWeatherBinding
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.example.simpleweatherapp.util.getAllViews
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext


class WeatherFragment : Fragment(),
    EasyPermissions.PermissionCallbacks {

    companion object {
        private const val ACCESS_COARSE_LOCATION_PERMISSIONS_REQUEST = 1
    }

    private val rotateDrawablePivot = 0.5f

    private val weatherIconResources = ArrayMap<String, List<Int>>()

    private val uviIconResources = ArrayMap<ClosedRange<Double>, Int>()

    private val windDirections = ArrayMap<ClosedRange<Int>, String>()

    init {
        weatherIconResources.apply {
            put("01d", listOf(R.drawable.clear_sky_01d, R.drawable.ic_01d_96))
            put("02d", listOf(R.drawable.few_clouds_02d, R.drawable.ic_02d_96))
            put("03d", listOf(R.drawable.scattered_clouds_03d, R.drawable.ic_03d_96))
            put("04d", listOf(R.drawable.broken_clouds_04d, R.drawable.ic_04d_96))
            put("09d", listOf(R.drawable.shower_rain_09d, R.drawable.ic_09d_96))
            put("10d", listOf(R.drawable.rain_10d, R.drawable.ic_10d_96))
            put("11d", listOf(R.drawable.thunderstorm_11d, R.drawable.ic_11d_96))
            put("13d", listOf(R.drawable.snow_13d, R.drawable.ic_13d_96))
            put("50d", listOf(R.drawable.mist_50d, R.drawable.ic_50d_96))
            put("01n", listOf(R.drawable.clear_sky_01n, R.drawable.ic_01n_96))
            put("02n", listOf(R.drawable.few_clouds_02n, R.drawable.ic_02n_96))
            put("03n", listOf(R.drawable.scattered_clouds_03n, R.drawable.ic_03n_96))
            put("04n", listOf(R.drawable.broken_clouds_04n, R.drawable.ic_04n_96))
            put("09n", listOf(R.drawable.shower_rain_09n, R.drawable.ic_09n_96))
            put("10n", listOf(R.drawable.rain_10n, R.drawable.ic_10n_96))
            put("11n", listOf(R.drawable.thunderstorm_11n, R.drawable.ic_11n_96))
            put("13n", listOf(R.drawable.snow_13n, R.drawable.ic_13n_96))
            put("50n", listOf(R.drawable.mist_50n, R.drawable.ic_50n_96))
        }
        uviIconResources.apply {
            put(0.0..2.999, R.drawable.ic_uvi_1_2_24dp)
            put(3.0..5.999, R.drawable.ic_uvi_3_5_24dp)
            put(6.0..7.999, R.drawable.ic_uvi_6_7_24dp)
            put(8.0..10.999, R.drawable.ic_uvi_8_10_24dp)
            put(11.0..30.00, R.drawable.ic_uvi_11_24dp)
        }
        windDirections.apply {
            put(0..11, "S")
            put(12..34, "SSW")
            put(35..56, "SW")
            put(57..79, "WSW")
            put(80..101, "W")
            put(102..124, "WNW")
            put(125..146, "NW")
            put(147..169, "NNW")
            put(170..191, "N")
            put(192..214, "NNE")
            put(215..236, "NE")
            put(237..259, "ENE")
            put(260..281, "E")
            put(282..304, "ESE")
            put(305..326, "SE")
            put(227..349, "SSE")
            put(350..359, "S")
        }
    }

    private val args by navArgs<WeatherFragmentArgs>()

    private val application = requireContext().applicationContext as SimpleWeatherApplication

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            application.mapsRepository,
            application.weatherRepository,
            this
        )
    }

    private var _binding: FragmentWeatherBinding? = null
    private val binding = _binding!!

    private val hourlyForecastAdapter = HourlyForecastAdapter(weatherIconResources)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.isWeatherAvailable.collect { isAvailable ->
                if (isAvailable) {
                    showLayout()
                } else {
                    hideLayout()
                }
            }

        }
        lifecycleScope.launchWhenStarted {
            viewModel.locationAndWeather.collect {
                bindWeather(it.first, it.second)
            }
        }
        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {
                while (true) {
                    setViewModelWeather()
                    delay(Const.WEATHER_REFRESH_INTERVAL)
                }
            }
        }
    }

    private fun bindWeather(sLocation: ShortLocation, weather: OneCallWeather) {
        val weatherIconBigRes =
            weatherIconResources[weather.weatherIcon]?.get(0) ?: R.drawable.ic_unavailable
        val windDirDescription = getDirText(weather.windDeg) ?: "N/A"
        val windArrow = RotateDrawable().apply {
            drawable = AppCompatResources.getDrawable(
                requireContext(), R.drawable.ic_arrow_16dp)
            pivotX = rotateDrawablePivot
            pivotY = rotateDrawablePivot
            fromDegrees = weather.windDeg.toFloat()
        }
        val uviIconRes = getUviIconRes(weather.uvi) ?: R.drawable.ic_unavailable

        binding.apply {
            toolbarWeather.title = sLocation.name
            tvCurrentTemp.text = weather.temp.toString()
            tvCurrentWeather.text = weather.weatherTitle
            ivWeather.setImageResource(weatherIconBigRes)
            tvCurrentFeelsLike.text = weather.feelsLike.toString()
            tvCurrentWind.text = weather.windSpeed.toString()
            tvCurrentWindDeg.text = windDirDescription
            tvCurrentWindDeg.setCompoundDrawables(
                windArrow,
                null, null, null
            )
            tvCurrentHumidity.text = weather.humidity.toString()
            tvCurrentPressure.text = weather.pressure.toString()
            tvCurrentDewPoint.text = weather.dewPoint.toString()
            tvCurrentUvi.text = weather.uvi.toString()
            tvCurrentUvi.setCompoundDrawables(
                AppCompatResources.getDrawable(requireContext(), uviIconRes),
                null, null, null
            )

            rvHourlyForecast.adapter = hourlyForecastAdapter
            hourlyForecastAdapter.submitList(weather.hourlyForecast)

            val groupDataList = listOf<>()

            val xListAdapter = SimpleExpandableListAdapter(
                requireContext(), groupDataList,
                android.R.layout.simple_expandable_list_item_1, groupFrom,
                groupTo, сhildDataList, android.R.layout.simple_list_item_1,
                childFrom, childTo
            )

            val expandableListView = findViewById(R.id.expListView) as ExpandableListView
            expandableListView.setAdapter(xListAdapter)
        }
    }

    private fun getDirText(windDeg: Int): String? {
        for (k in windDirections.keys) {
            if (k.contains(windDeg)) {
                return windDirections[k]
            }
        }
        return null
    }

    private fun getUviIconRes(uvi: Double): Int? {
        for (k in uviIconResources.keys) {
            if (k.contains(uvi)) {
                return uviIconResources[k]
            }
        }
        return null
    }

    private fun hideLayout() {
        val views = binding.root.getAllViews()
        for (v in views) {
            v.visibility = View.GONE
        }
        binding.tvWeatherUnavailable.visibility = View.VISIBLE
    }

    private fun showLayout() {
        val views = binding.root.getAllViews()
        for (v in views) {
            v.visibility = View.VISIBLE
        }
        binding.tvWeatherUnavailable.visibility = View.GONE

    }

    private fun setViewModelWeather() {
        if (arguments != null) {
            // Set with search results
            viewModel.setWeather(args.newShortLocation)
        } else {
            // Set using location API
            if (hasCoarseLocationPermission()) {
                setWeatherWithCurrentLocation()
            } else {
                requestCoarseLocationPermission()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setWeatherWithCurrentLocation() {
        application.fusedLocationClient.lastLocation.addOnCompleteListener { locationTask ->
            val lastLocation = locationTask.result
            if (lastLocation != null &&
                System.currentTimeMillis() - lastLocation.time < Const.LOCATION_REFRESH_INTERVAL) {
                viewModel.setWeather(lastLocation)
            } else {
                application.fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).addOnCompleteListener {
                    val currentLocation = locationTask.result
                    if (currentLocation != null) {
                        viewModel.setWeather(currentLocation)
                    } else {
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

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestCoarseLocationPermission()
        }
    }



    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        setWeatherWithCurrentLocation()
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}