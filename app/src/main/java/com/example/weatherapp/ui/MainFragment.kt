package com.example.weatherapp.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.dialog.DialogManager
import com.example.weatherapp.extension.isPermissionGranted
import com.example.weatherapp.model.DataDay
import com.example.weatherapp.ui.adapter.VPAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

const val API_KAY = "bb93f58132644133971193737231412"

class MainFragment : Fragment() {
    private lateinit var locationClient: FusedLocationProviderClient
    private val fragmentList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private lateinit var hours: String
    private lateinit var days: String

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hours = requireContext().getString(R.string.hours)
        days = requireContext().getString(R.string.days)
        checkPermission()
//        permissionListener()
//        pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        init()
        updateCurrentCard()
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun init() {
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val tabList = listOf(hours, days)
        val adapter = VPAdapter(activity as FragmentActivity, fragmentList)
        binding.viewPage.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPage) { tab, position ->
            tab.text = tabList[position]
        }.attach()

        binding.location.setOnClickListener {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
            checkLocation()
        }

        binding.imageSearch.setOnClickListener {
            DialogManager.searchLocationDialog(requireContext(), object : DialogManager.Listener {
                override fun onClock(city: String?) {
                    if (city != null) {
                        getUrlWeather(city)
                    }
                }
            })
        }

    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingDialog(requireContext(), object :
                DialogManager.Listener {
                override fun onClock(name: String?) {
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
//        check GPS on/of
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                getUrlWeather("${it.result.latitude},${it.result.longitude}")
            }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        {
            Toast.makeText(activity, "permission is $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun updateCurrentCard() {
        model.liveDataCurrent.observe(viewLifecycleOwner) { item ->
            val maxMinTemp = "${item.maxTemp}°C/${item.minTemp}°C"
            val currentTemp = item.currentTemp + "°C"
            binding.data.text = item.time
            binding.tvCountry.text = item.country
            binding.tvCity.text = item.city
            binding.tvTemp.text =
                if (item.currentTemp.isEmpty()) {
                    maxMinTemp
                } else {
                    currentTemp
                }
            binding.tvCondition.text = item.condition
            binding.tvMaxMinTemp.text =
                if (item.currentTemp.isEmpty()) {
                    ""
                } else {
                    maxMinTemp
                }
            Picasso.get().load("https:" + item.imageWeather).into(binding.imageWeather)
        }
    }

    private fun getUrlWeather(city: String) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KAY +
                "&q=" +
                city +
                "&days=" +
                "10" +
                "&aqi=no&alerts=no"

        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                parsWeatherData(result)
            },
            { error ->
                Log.d("MyLog", "Error result: $error")
            }
        )
        queue.add(request)
    }

    private fun parsWeatherData(result: String) {
        val mainJsonObjectWeather = JSONObject(result)
        val listDays = parsListDays(mainJsonObjectWeather)

        parseCurrentData(mainJsonObjectWeather, listDays[0])
    }

    private fun parsListDays(jsonObjectWeather: JSONObject): List<DataDay> {
        val list = ArrayList<DataDay>()
        val country = jsonObjectWeather.getJSONObject("location").getString("country")
        val city = jsonObjectWeather.getJSONObject("location").getString("name")
        val daysArray = jsonObjectWeather.getJSONObject("forecast")
            .getJSONArray("forecastday")

        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = DataDay(
                country,
                city,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONArray("hour").toString(),
            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: DataDay) {
        val item = DataDay(
            mainObject.getJSONObject("location").getString("country"),
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition")
                .getString("text"),
            mainObject.getJSONObject("current").getJSONObject("condition")
                .getString("icon"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            weatherItem.hours
        )
        model.liveDataCurrent.value = item
        Log.d("myLog", "maxT: ${weatherItem.maxTemp}")
    }

}