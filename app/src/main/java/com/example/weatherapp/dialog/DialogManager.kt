package com.example.weatherapp.dialog

import android.R
import android.app.AlertDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object DialogManager {

    fun locationSettingDialog(context: Context, listener: Listener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("Enable location?")
        dialog.setMessage("Location disable? do uoy want enable location? ")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            listener.onClock(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun searchLocationDialog(context: Context, listener: Listener) {
        val csvFilePath = "worldcities.csv"
        val cities: MutableList<String> = mutableListOf()

        try {
            val inputStream: InputStream = context.assets.open(csvFilePath)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val csvParser = CSVParserBuilder().withSeparator(',').build()
            val csvReader = CSVReaderBuilder(reader).withCSVParser(csvParser).build()

            var record: Array<String>?
//            Skip the title
            reader.readLine()
            csvReader.readNext()
            while (csvReader.readNext().also { record = it } != null) {
                val cityName = record?.get(0)
                if (!cityName.isNullOrBlank()) {
                    cities.add(cityName)
                }
            }
            csvReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val builder = AlertDialog.Builder(context)
        val adName = AutoCompleteTextView(context)
        builder.setView(adName)

        val adapter = ArrayAdapter(context, R.layout.simple_dropdown_item_1line, cities)
        adName.setAdapter(adapter)

        val dialog = builder.create()
        dialog.setTitle("City name:")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            listener.onClock(adName.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { _, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener {
        fun onClock(city: String?)
    }
}