package com.example.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemDaysBinding
import com.example.weatherapp.model.DataDay
import com.squareup.picasso.Picasso

class WeatherAdapter(val listenerDay: Listener?) :
    ListAdapter<DataDay, WeatherAdapter.Holder>(WeatherDiffUtil()) {

    class Holder(view: View, listenerDay: Listener?) : RecyclerView.ViewHolder(view) {
        private val binding = ItemDaysBinding.bind(view)
        var check: DataDay? = null

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_days, parent, false)
        return Holder(view, listenerDay)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(item: DataDay)
    }
}