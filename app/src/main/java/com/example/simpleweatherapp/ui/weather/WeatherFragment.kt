package com.example.simpleweatherapp.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.SimpleWeatherApplication
import com.example.simpleweatherapp.databinding.FragmentWeatherBinding
import com.example.simpleweatherapp.model.bingmaps.ShortLocation
import com.example.simpleweatherapp.model.openweather.OneCallWeather
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.*
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.flow.collect
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WeatherFragment : Fragment(), OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
    EasyPermissions.PermissionCallbacks {

    companion object {
        private const val ACCESS_COARSE_LOCATION_PERMISSIONS_REQUEST = 1
    }

    private val weatherIconsRes = ArrayMap<String, List<Int>>()

    private val uviIconsRes = ArrayMap<ClosedRange<Double>, Int>()

    private val windDirections = ArrayMap<ClosedRange<Int>, String>()

    init {
        weatherIconsRes.apply {
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
        uviIconsRes.apply {
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

//    private val args by navArgs<WeatherFragmentArgs>()

    private var _application: SimpleWeatherApplication? = null
    private val application get() = _application!!

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(
            application.mapsRepository,
            application.weatherRepository,
            this
        )
    }

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val hourlyForecastAdapter = HourlyForecastAdapter(weatherIconsRes)

    private val dailyForecastAdapter = DailyForecastAdapter(
        weatherIconsRes,
        uviIconsRes,
        windDirections,
        this
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set the toolbar top margin with respect of the status bar
        (binding.toolbarWeather.layoutParams as ViewGroup.MarginLayoutParams)
            .topMargin = binding.toolbarLayoutWeather.marginTop + getStatusBarHeight()

        binding.skeletonLayout.showSkeleton()

        _application = requireContext().applicationContext as SimpleWeatherApplication

        lifecycleScope.launchWhenStarted {
            viewModel.isWeatherAvailable.collect { isAvailable ->
                if (isAvailable) {
                    showLayout()
                } else {
                    hideLayout()
                }
            }

        }
        setupWeather()

        lifecycleScope.launchWhenStarted {
            viewModel.locationAndWeather.collect {

                bindWeather(it.first, it.second)
                binding.skeletonLayout.showOriginal()
                
            }
        }
        setViewModelWeather()
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
    }

    private fun bindWeather(sLocation: ShortLocation, weather: OneCallWeather) {
        val weatherIconBigRes =
            weatherIconsRes[weather.weatherIcon]?.get(0) ?: R.drawable.ic_unavailable

        val temp = weather.temp.toInt()
        val tempFormatted = getString(R.string.temp_n_dew_point_formatted, intToSignedString(temp))

        val feelsLike = weather.feelsLike.toInt()
        val feelsLikeFormatted = getString(R.string.feels_like_formatted, intToSignedString(feelsLike))

        val textColor = getColor(requireContext(), R.color.text_color)
        
        val wind = weather.windSpeed.toInt().toString()
        val windFormatted = getString(R.string.wind_speed_formatted, wind)
        val windSpanned = paintStartValue(windFormatted, wind, textColor)
        val windDirText = getResFromRange(windDirections, weather.windDeg) ?: "N/A"
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
            toolbarWeather.title = sLocation.populatedPlace
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
            divTop.visibility = View.VISIBLE
            divBottom.visibility = View.VISIBLE
            divDailyForecast.visibility = View.VISIBLE
            currentTempNWeatherStub.visibility = View.GONE
            rvHourlyForecastStub.root.visibility = View.GONE
            rvHourlyForecast.visibility = View.VISIBLE
            rvDailyForecastStub.root.visibility = View.GONE
            rvDailyForecast.visibility = View.VISIBLE

            swiperefresh.isRefreshing = false
        }
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
//        if (arguments != null) {
//            // Set with search results
//            viewModel.setWeather(args.newShortLocation)
//        } else {
            // Set using location API
            if (hasCoarseLocationPermission()) {
                setWeatherWithCurrentLocation()
            } else {
                requestCoarseLocationPermission()
            }
//        }
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

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun onRefresh() {
        binding.skeletonLayout.showSkeleton()
        setViewModelWeather()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onItemClick(v: View?, position: Int) {



        // Expand clicked daily forecast item
//        lifecycleScope.launch {
//            val dailyForecast = viewModel.locationAndWeather.first().second.dailyForecast
//            for (weather in dailyForecast) {
//                if (weather.expanded) {
//                    weather.expanded = false
//                }
//            }
//            dailyForecast[position].expanded = true
//            dailyForecastAdapter.submitList(dailyForecast)
//            dailyForecastAdapter.notifyDataSetChanged()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _application = null
    }
}