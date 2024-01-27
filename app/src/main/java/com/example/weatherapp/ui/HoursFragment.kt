package com.example.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentHoursBinding
import com.example.weatherapp.model.DataDay
import com.example.weatherapp.ui.adapter.WeatherAdapter
import org.json.JSONArray
import org.json.JSONObject


class HoursFragment : Fragment() {

    private lateinit var binding: FragmentHoursBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        updateCurrentHours()
    }

    private fun initRcView() = with(binding) {
        listHours.layoutManager = LinearLayoutManager(activity)
        adapter = WeatherAdapter(null)
        listHours.adapter = adapter
    }

    private fun updateCurrentHours() {
        model.liveDataCurrent.observe(viewLifecycleOwner) { item ->
            adapter.submitList(getHoursList(item))
        }
    }

    private fun getHoursList(weatherItem: DataDay): List<DataDay> {
        val hoursArray = JSONArray(weatherItem.hours)

        val listWeatherHours = ArrayList<DataDay>()
        for (i in 0 until hoursArray.length()) {
            val item = DataDay(
                "",
                "",
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("text"),
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("icon"),
                (hoursArray[i] as JSONObject).getString("temp_c").toFloat().toInt().toString(),
                "",
                "",
                ""
            )
            listWeatherHours.add(item)
        }
        return listWeatherHours
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}