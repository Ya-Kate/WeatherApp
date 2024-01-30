package com.example.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.weatherapp.R
import com.example.weatherapp.model.DataDay

class WeatherAdapter(private val listenerDay: Listener?) :
    ListAdapter<DataDay, WeatherViewHolder>(WeatherDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_days, parent, false)
        return WeatherViewHolder(view, listenerDay)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(item: DataDay)
    }
}