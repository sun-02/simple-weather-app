package com.example.simpleweatherapp.ui.weather

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simpleweatherapp.Const
import com.example.simpleweatherapp.databinding.ItemDailyForecastBinding
import com.example.simpleweatherapp.model.openweather.DailyForecast
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.ui.OnItemClickListener
import com.example.simpleweatherapp.util.getResFromRange
import com.example.simpleweatherapp.util.intToSignedString
import com.example.simpleweatherapp.util.paintEndValue
import com.example.simpleweatherapp.util.paintStartValue
import java.time.DayOfWeek


class DailyForecastAdapter(
    private val weatherIconsRes: ArrayMap<String, List<Int>>,
    private val uviIconsRes: ArrayMap<ClosedRange<Double>, Int>,
    private val windDirections: ArrayMap<ClosedRange<Int>, String>,
    private val listener: OnItemClickListener
) : ListAdapter<DailyForecast,
        DailyForecastAdapter.DailyForecastViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<DailyForecast>() {

        private val dateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("MMMM d", Locale("en", "US"))
        private val dowFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("EEEE", Locale("en", "US"))

        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DailyForecastViewHolder {
        return DailyForecastViewHolder(
            ItemDailyForecastBinding.inflate(LayoutInflater.from(parent.context)),
            weatherIconsRes,
            uviIconsRes,
            windDirections,
            listener
        )
    }

    override fun onBindViewHolder(
        holder: DailyForecastViewHolder,
        position: Int
    ) {
        val dailyForecast = getItem(position)
        holder.bind(dailyForecast)
    }

    class DailyForecastViewHolder(
        private val binding: ItemDailyForecastBinding,
        private val weatherIconsRes: ArrayMap<String, List<Int>>,
        private val uviIconsRes: ArrayMap<ClosedRange<Double>, Int>,
        private val windDirections: ArrayMap<ClosedRange<Int>, String>,
        private val listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(
            df: DailyForecast
        ) {
            val redColor = ContextCompat.getColor(itemView.context, R.color.red)
            val textColor = ContextCompat.getColor(itemView.context, R.color.text_color)

            val dowFormatted = when (position) {
                0 -> itemView.context.getString(R.string.today)
                1 -> itemView.context.getString(R.string.tomorrow)
                else -> df.date.format(dowFormatter)
            }
            val dowSpanned = if(
                df.date.dayOfWeek == DayOfWeek.SATURDAY || df.date.dayOfWeek == DayOfWeek.SUNDAY
            ) {
                paintStartValue(dowFormatted, dowFormatted, redColor)
            } else {
                paintStartValue(dowFormatted, dowFormatted, textColor)
            }

            val dateFormatted = df.date.format(dateFormatter)

            val weatherIconRes =
                weatherIconsRes[df.weatherIcon]?.get(1) ?: R.drawable.ic_unavailable

            val tempDay = df.dayTemp.toInt()
            val tempDayFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(tempDay))

            val tempNight = df.nightTemp.toInt()
            val tempNightFormatted = itemView.context
                .getString(R.string.temp_n_dew_point_formatted, intToSignedString(tempNight))

            val wind = df.windSpeed.toInt().toString()
            val windFormatted = itemView.context
                .getString(R.string.wind_speed_formatted, wind)
            val windSpanned = paintStartValue(windFormatted, wind, textColor)
            val windDirText = getResFromRange(windDirections, df.windDeg) ?: "N/A"
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
                .getString(R.string.pressure_formatted, pressure)
            val pressureSpanned = paintStartValue(pressureFormatted, pressure, textColor)

            val uvi = df.uvi.toInt().toString()
            val uviFormatted = itemView.context
                .getString(R.string.uvi_formatted, uvi)
            val uviSpanned = paintEndValue(uviFormatted, uvi, textColor)
            val uviIconRes = getResFromRange(uviIconsRes, df.uvi) ?: R.drawable.ic_unavailable
            val uviIcon = AppCompatResources.getDrawable(itemView.context, uviIconRes)

            binding.apply {
                tvDow.text = dowSpanned
                tvDate.text = dateFormatted
                ivWeather.setImageResource(weatherIconRes)
                tvTempDay.text = tempDayFormatted
                tvTempNight.text = tempNightFormatted
                tvWind.text = windSpanned
                tvWindDeg.text = windDirText
                tvWindDeg.setCompoundDrawablesWithIntrinsicBounds(
                    windArrow, null, null, null)
                tvHumidity.text = humiditySpanned
                tvPressure.text = pressureSpanned
                tvUvi.setCompoundDrawablesWithIntrinsicBounds(
                    null, uviIcon, null, null)
                tvUvi.text = uviSpanned
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(v, layoutPosition)
        }
    }
}