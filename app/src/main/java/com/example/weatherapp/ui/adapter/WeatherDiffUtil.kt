package com.example.weatherapp.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapp.model.DataDay

class WeatherDiffUtil :DiffUtil.ItemCallback<DataDay>() {
    override fun areItemsTheSame(oldItem: DataDay, newItem: DataDay): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: DataDay, newItem: DataDay): Boolean {
        return oldItem == newItem
    }

}