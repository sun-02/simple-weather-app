package com.example.simpleweatherapp.ui.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.simpleweatherapp.DateTimeFormatters.dowShortFormatter
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.SimpleWeatherApplication
import com.example.simpleweatherapp.databinding.FragmentDailyForecastExtendedBinding
import com.example.simpleweatherapp.model.openweather.DailyForecast
import com.example.simpleweatherapp.util.getStatusBarHeight
import com.example.simpleweatherapp.util.setToolbarLayoutTopMarginWithRespectOfStatusBarHeight
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek

class DailyForecastExtendedFragment : Fragment() {

    private val args: DailyForecastExtendedFragmentArgs by navArgs()

    private var _binding: FragmentDailyForecastExtendedBinding? = null
    private val binding get() = _binding!!

    private var _application: SimpleWeatherApplication? = null
    private val application get() = _application!!

    private val viewModel: WeatherViewModel by activityViewModels {
        WeatherViewModelFactory(
            application.mapsRepository,
            application.weatherRepository,
            requireActivity()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentDailyForecastExtendedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarDailyForecast
            .setToolbarLayoutTopMarginWithRespectOfStatusBarHeight(getStatusBarHeight())

        _application = requireContext().applicationContext as SimpleWeatherApplication


        lateinit var dailyForecastList: List<DailyForecast>
        lifecycleScope.launch {
            dailyForecastList = viewModel.currentWeather.first().dailyForecast!!
        }

        binding.pager.adapter = DailyForecastExtendedAdapter(dailyForecastList)
        binding.pager.setCurrentItem(args.position, false)

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            val date = dailyForecastList[position].date
            tab.setCustomView(R.layout.tab_custom_view)
            val dowTv = (tab.customView as ViewGroup).getChildAt(0) as TextView
            dowTv.text = date.format(dowShortFormatter)
            if(date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY) {
                dowTv.setTextColor(getColor(requireContext(), R.color.text_color_red))
            }
            tab.text = date.dayOfMonth.toString()
        }.attach()

        binding.toolbarDailyForecast.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _application = null
    }
}