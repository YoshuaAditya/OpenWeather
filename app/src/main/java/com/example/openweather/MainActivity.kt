package com.example.openweather

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore


class MainActivity : AppCompatActivity() {

    companion object{
        private val Context.dataStore by preferencesDataStore(name = "settings")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainViewModel=MainViewModel(applicationContext.dataStore)
        mainViewModel.getCurrentLocation(this)
    }
}