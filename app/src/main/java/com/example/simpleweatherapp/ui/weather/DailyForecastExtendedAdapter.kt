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
import java.time.Instant
import java.time.ZoneOffset

class DailyForecastExtendedAdapter(
    private val dailyForecastList: List<DailyForecast>
) : RecyclerView.Adapter<DailyForecastExtendedAdapter.DailyForecastExtendedViewHolder>() {

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
        private val binding: ItemDailyForecastExtendedBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(df: DailyForecast) {
            val textColor = ContextCompat.getColor(itemView.context, R.color.text_color)
            
            val weatherImageRes =
                weatherImagesRes[df.weatherIcon] ?: R.drawable.ic_unavailable

            val tempMorning = df.mornTemp
            val tempMorningFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(tempMorning))

            val tempDay = df.dayTemp
            val tempDayFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(tempDay))

            val tempEvening = df.eveTemp
            val tempEveningFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(tempEvening))

            val tempNight = df.nightTemp
            val tempNightFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(tempNight))

            val feelsLikeMorning = df.mornFeelsLike
            val feelsLikeMorningFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(feelsLikeMorning))

            val feelsLikeDay = df.dayFeelsLike
            val feelsLikeDayFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(feelsLikeDay))

            val feelsLikeEvening = df.eveFeelsLike
            val feelsLikeEveningFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(feelsLikeEvening))

            val feelsLikeNight = df.nightFeelsLike
            val feelsLikeNightFormatted = itemView.context
                .getString(R.string.temp_formatted, intToSignedString(feelsLikeNight))
            
            val wind = df.windSpeed.toString()
            val windFormatted = itemView.context
                .getString(R.string.wind_speed_formatted, wind)
            val windSpanned = paintStartValue(windFormatted, wind, textColor)
            val windDirRes = getResFromRange(windDirectionsRes, df.windDeg)
            val windDirText = itemView.context.getString(windDirRes ?: R.string.unavailable)
            val windArrow = AppCompatResources.getDrawable(
                itemView.context, R.drawable.ic_arrow_rotate
            )!!
            windArrow.level = (df.windDeg * Const.ROTATION_ANGLE_TO_LEVEL_COEF).toInt()

            val humidity = df.humidity.toString()
            val humidityFormatted = itemView.context
                .getString(R.string.humidity_formatted, humidity, "%")
            val humiditySpanned = paintStartValue(humidityFormatted, humidity, textColor)

            val pressure = df.pressure.toString()
            val pressureFormatted = itemView.context
                .getString(R.string.pressure_newline_formatted, pressure)
            val pressureSpanned = paintStartValue(pressureFormatted, pressure, textColor)

            val uvi = df.uvi.toString()
            val uviFormatted = itemView.context
                .getString(R.string.uvi_formatted, uvi)
            val uviSpanned = paintEndValue(uviFormatted, uvi, textColor)
            val uviIconRes = getResFromRange(uviIconsRes, df.uvi) ?: R.drawable.ic_unavailable
            val uviIcon = AppCompatResources.getDrawable(itemView.context, uviIconRes)
            
            binding.apply {
                ivDailyExWeather.setImageResource(weatherImageRes)
                tvDailyExMorningTemp.text = tempMorningFormatted
                tvDailyExDayTemp.text = tempDayFormatted
                tvDailyExEveningTemp.text = tempEveningFormatted
                tvDailyExNightTemp.text = tempNightFormatted
                tvDailyExFeelsLikeMorning.text = feelsLikeMorningFormatted
                tvDailyExFeelsLikeDay.text = feelsLikeDayFormatted
                tvDailyExFeelsLikeEvening.text = feelsLikeEveningFormatted
                tvDailyExFeelsLikeNight.text = feelsLikeNightFormatted
                tvDailyExDayTemp.text = tempDayFormatted
                tvDailyExEveningTemp.text = tempEveningFormatted
                tvDailyExNightTemp.text = tempNightFormatted
                tvDailyExWind.text = windSpanned
                tvDailyExWindDeg.text = windDirText
                tvDailyExWindDeg.setCompoundDrawablesWithIntrinsicBounds(
                    windArrow, null, null, null)
                tvDailyExHumidity.text = humiditySpanned
                tvDailyExPressure.text = pressureSpanned
                tvDailyExUvi.setCompoundDrawablesWithIntrinsicBounds(
                    null, uviIcon, null, null)
                tvDailyExUvi.text = uviSpanned
                tvDailyExSunrise.text = getFormattedTime(df.sunrise, df.zoneOffset)
                tvDailyExSunset.text = getFormattedTime(df.sunset, df.zoneOffset)
                tvDailyExMoonrise.text = getFormattedTime(df.moonrise, df.zoneOffset)
                tvDailyExMoonset.text = getFormattedTime(df.moonset, df.zoneOffset)
            }
        }
    }

    override fun getItemCount(): Int = dailyForecastList.size

    companion object {
        private fun getFormattedTime(instant: Instant, zoneOffset: ZoneOffset) =
            DtUtil.localTimeOfInstant(instant, zoneOffset).format(timeFormatter)
    }

}