package com.barisgungorr.weather_app

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.databinding.DataBindingUtil
import com.barisgungorr.data.ApıUtilities
import com.barisgungorr.model.WeatherModel
import com.barisgungorr.weather_app.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    private val LOCATION_REQUEST_CODE =101

    private val apiKey = "c93b5071758719941ae4a9cec26c918f"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()

        binding.citySearch.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                getCityWeather(binding.citySearch.text.toString())

                val view = this.currentFocus

                if (view != null) {
                    val inm: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                    inm.hideSoftInputFromWindow(view.windowToken, 0)

                    binding.citySearch.clearFocus()
                }
                return@setOnEditorActionListener true
            }

            else{

                return@setOnEditorActionListener false


            }

        }
        binding.currentLocation.setOnClickListener{
            getCurrentLocation()

        }
    }

    private fun getCityWeather(city:String) {
        binding.progressBar.visibility = View.VISIBLE

        ApıUtilities.getApiInterface()?.getCityWeatherData(city,apiKey)?.enqueue(
            object : Callback<WeatherModel>{
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful) {
                        binding.progressBar.visibility = View.GONE
                       response.body()?.let {
                           setData(it)
                       }
                    }
                    else{
                        Toast.makeText(this@MainActivity,"Şehir bulunamadı!",Toast.LENGTH_LONG).show()
                        binding .progressBar.visibility = View.GONE

                    }

                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            }


        )


    }

    private fun fetchCurrentLocationWeather(latitude:String,longitude:String) {

        ApıUtilities.getApiInterface()?.getCurrentWeatherData(latitude,longitude,apiKey)
            ?.enqueue(object : Callback<WeatherModel>{
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful) {
                        binding.progressBar.visibility = View.GONE
                        response.body()?.let {
                            setData(it)
                        }

                    }
                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    TODO("Not yet implemented")
                }


            })

    }

    private fun getCurrentLocation() {

        if (checkPermission()) {

        }

    }




}