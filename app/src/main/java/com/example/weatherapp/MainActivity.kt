package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.MainFragment
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val file = File(Environment.getExternalStorageDirectory(), "worldcities")

        supportFragmentManager.beginTransaction()
            .replace(R.id.activity, MainFragment())
//            .addToBackStack("")
            .commit()
    }
}




