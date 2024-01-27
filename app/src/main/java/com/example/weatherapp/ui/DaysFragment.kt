package com.example.weatherapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentDaysBinding
import com.example.weatherapp.model.DataDay
import com.example.weatherapp.ui.adapter.WeatherAdapter

class DaysFragment : Fragment(), WeatherAdapter.Listener {
    private lateinit var binding: FragmentDaysBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.liveDataList.observe(viewLifecycleOwner) { listDays ->
//            submitList from 1 index
            adapter.submitList(listDays.subList(1, listDays.size))
        }

    }

    private fun init() {
        adapter = WeatherAdapter(this@DaysFragment)
        binding.listDays.layoutManager = LinearLayoutManager(activity)
        binding.listDays.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(itemDay: DataDay) {
        model.liveDataCurrent.value = itemDay
    }
}




