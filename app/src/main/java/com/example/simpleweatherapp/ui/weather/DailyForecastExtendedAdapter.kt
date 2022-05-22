package com.example.simpleweatherapp.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.DateTimeFormatters.timeFormatter
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.databinding.ItemDailyForecastExtendedBinding
import com.example.simpleweatherapp.model.openweather.DailyForecast
import com.example.simpleweatherapp.util.*
import com.example.simpleweatherapp.ResourcesMapping.weatherImagesRes
import com.example.simpleweatherapp.ResourcesMapping.uviIconsRes
import com.example.simpleweatherapp.ResourcesMapping.windDirectionsRes

class DailyForecastExtendedAdapter(private val dailyForecastList: List<DailyForecast>) :
    RecyclerView.Adapter<DailyForecastExtendedAdapter.DailyForecastExtendedViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyForecastExtendedViewHolder =
        DailyForecastExtendedViewHolder(
            ItemDailyForecastExtendedBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        )

    override fun onBindViewHolder(holder: DailyForecastExtendedViewHolder, position: Int) {
        val dailyForecast = dailyForecastList[position]
        holder.bind(dailyForecast)
    }

    class DailyForecastExtendedViewHolder(
        val binding: ItemDailyForecastExtendedBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(df: DailyForecast) {
            val textColor = ContextCompat.getColor(itemView.context, R.color.text_color)
            
            val weatherImageRes =
                weatherImagesRes[df.weatherIcon] ?: R.drawable.ic_unavailable

            val tempMorning = df.mornTemp.toInt()
            val tempMorningFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(tempMorning))

            val tempDay = df.dayTemp.toInt()
            val tempDayFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(tempDay))

            val tempEvening = df.eveTemp.toInt()
            val tempEveningFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(tempEvening))

            val tempNight = df.nightTemp.toInt()
            val tempNightFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(tempNight))

            val feelsLikeMorning = df.mornFeelsLike.toInt()
            val feelsLikeMorningFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(feelsLikeMorning))

            val feelsLikeDay = df.dayFeelsLike.toInt()
            val feelsLikeDayFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(feelsLikeDay))

            val feelsLikeEvening = df.eveFeelsLike.toInt()
            val feelsLikeEveningFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(feelsLikeEvening))

            val feelsLikeNight = df.nightFeelsLike.toInt()
            val feelsLikeNightFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(feelsLikeNight))
            
            val wind = df.windSpeed.toInt().toString()
            val windFormatted = itemView.context
                .getString(R.string.wind_speed_formatted, wind)
            val windSpanned = paintStartValue(windFormatted, wind, textColor)
            val windDirRes = getResFromRange(windDirectionsRes, df.windDeg)
            val windDirText = itemView.context.getString(windDirRes ?: R.string.unavailable)
            val windArrow = AppCompatResources.getDrawable(
                itemView.context, R.drawable.ic_arrow_rotate
            )!!
            windArrow.level = (df.windDeg * Const.DEGREE_TO_LEVEL_COEF).toInt()

            val humidity = df.humidity.toString()
            val humidityFormatted = itemView.context
                .getString(R.string.humidity_formatted, humidity, "%")
            val humiditySpanned = paintStartValue(humidityFormatted, humidity, textColor)

            val pressure = df.pressure.toString()
            val pressureFormatted = itemView.context
                .getString(R.string.pressure_newline_formatted, pressure)
            val pressureSpanned = paintStartValue(pressureFormatted, pressure, textColor)

            val uvi = df.uvi.toInt().toString()
            val uviFormatted = itemView.context
                .getString(R.string.uvi_formatted, uvi)
            val uviSpanned = paintEndValue(uviFormatted, uvi, textColor)
            val uviIconRes = getResFromRange(uviIconsRes, df.uvi) ?: R.drawable.ic_unavailable
            val uviIcon = AppCompatResources.getDrawable(itemView.context, uviIconRes)
            
            binding.apply {
                ivDailyWeather.setImageResource(weatherImageRes)
                tvDailyMorningTemp.text = tempMorningFormatted
                tvDailyDayTemp.text = tempDayFormatted
                tvDailyEveningTemp.text = tempEveningFormatted
                tvDailyNightTemp.text = tempNightFormatted
                tvFeelsLikeMorning.text = feelsLikeMorningFormatted
                tvFeelsLikeDay.text = feelsLikeDayFormatted
                tvFeelsLikeEvening.text = feelsLikeEveningFormatted
                tvFeelsLikeNight.text = feelsLikeNightFormatted
                tvDailyDayTemp.text = tempDayFormatted
                tvDailyEveningTemp.text = tempEveningFormatted
                tvDailyNightTemp.text = tempNightFormatted
                tvDailyWind.text = windSpanned
                tvDailyWindDeg.text = windDirText
                tvDailyWindDeg.setCompoundDrawablesWithIntrinsicBounds(
                    windArrow, null, null, null)
                tvDailyHumidity.text = humiditySpanned
                tvDailyPressure.text = pressureSpanned
                tvDailyUvi.setCompoundDrawablesWithIntrinsicBounds(
                    null, uviIcon, null, null)
                tvDailyUvi.text = uviSpanned
                tvDailySunrise.text = df.sunrise.format(timeFormatter)
                tvDailySunset.text = df.sunset.format(timeFormatter)
                tvDailyMoonrise.text = df.moonrise.format(timeFormatter)
                tvDailyMoonset.text = df.moonset.format(timeFormatter)
            }
        }
    }

    override fun getItemCount(): Int = dailyForecastList.size
}