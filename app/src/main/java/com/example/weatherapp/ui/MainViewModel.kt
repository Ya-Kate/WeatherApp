package com.example.weatherapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.DataDay

class MainViewModel : ViewModel() {

    val liveDataCurrent = MutableLiveData<DataDay>()
    val liveDataList = MutableLiveData<List<DataDay>>()


}