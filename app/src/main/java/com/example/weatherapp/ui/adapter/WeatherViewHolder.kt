package com.example.weatherapp.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemDaysBinding
import com.example.weatherapp.model.DataDay
import com.squareup.picasso.Picasso

class WeatherViewHolder (view: View, listenerDay: WeatherAdapter.Listener?) : RecyclerView.ViewHolder(view) {

    private val binding = ItemDaysBinding.bind(view)
    private var check: DataDay? = null

    init {
        itemView.setOnClickListener {
            check?.let { it1 -> listenerDay?.onClick(it1) }
        }
    }

    fun bind(item: DataDay) = with(binding) {
        check = item
        val temp = item.currentTemp.ifEmpty {
            "${item.maxTemp}°C/${item.minTemp}°C"
        }
        date.text = item.time
        tvCondition.text = item.condition
        currentTemp.text = temp
        Picasso.get().load("https:" + item.imageWeather).into(binding.image)
    }

}