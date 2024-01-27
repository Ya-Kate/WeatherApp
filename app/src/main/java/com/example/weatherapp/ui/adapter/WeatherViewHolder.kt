package com.example.weatherapp.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.databinding.ItemDaysBinding
import com.example.weatherapp.model.DataDay

class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemDaysBinding.bind(view)

    fun bind(item: DataDay) = with(binding) {
        date.text = item.time
        tvCondition.text = item.condition
        currentTemp.text = item.currentTemp
    }

}