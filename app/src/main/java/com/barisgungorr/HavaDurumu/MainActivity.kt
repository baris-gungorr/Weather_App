package com.barisgungorr.HavaDurumu

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.barisgungorr.hava_durum_app.R
import com.barisgungorr.hava_durum_app.databinding.ActivityMainBinding
import com.barisgungorr.model.WeatherModel
import com.barisgungorr.service.ApiUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    private val LOCATION_REQUEST_CODE = 101

    private val apiKey = "c93b5071758719941ae4a9cec26c918f"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

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
            } else {

                return@setOnEditorActionListener false

            }
        }
        binding.currentLocation.setOnClickListener {
            getCurrentLocation()

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCityWeather(city: String) {
        binding.progressBar.visibility = View.VISIBLE


        val coroutineScope = CoroutineScope(Dispatchers.Main)


        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiUtil.getApiInterface()?.getCityWeatherData(city, apiKey)?.execute()
                }

                if (response != null && response.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    val weatherModel = response.body()
                    if (weatherModel != null) {
                        setData(weatherModel)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Country not found!", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {

        ApiUtil.getApiInterface()?.getCurrentWeatherData(latitude, longitude, apiKey)
            ?.enqueue(object : Callback<WeatherModel> {
                @RequiresApi(Build.VERSION_CODES.O)
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
                    Log.e("ERROR","ERROR")
                }

            })
    }

    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }

                fusedLocationProvider.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            currentLocation = location
                            binding.progressBar.visibility = View.VISIBLE
                            fetchCurrentLocationWeather(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                        }
                    }
            } else {
                showLocationSettingsConfirmationDialog()
            }
        } else {
            requestPermission()
        }
    }

    private fun showLocationSettingsConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Location closed")
        alertDialogBuilder.setMessage("Your location settings are turned off. I need location information. Do you want to go to settings")
        alertDialogBuilder.setPositiveButton("Evet") { _, _ ->
            // Open location settings
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            showLocationSettingsSnackbar()
        }
        alertDialogBuilder.show()
    }

    private fun showLocationSettingsSnackbar() {
        Snackbar.make(
            binding.root,
            "Your location features are turned off. You need location information.",
            Snackbar.LENGTH_LONG
        )
            .setAction("Go to settings") {

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .show()
    }

    private fun requestPermission() {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE)
                as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(body: WeatherModel) {

        binding.apply {
            val currentDate =
                SimpleDateFormat("dd/MM/yyyy hh:mm", Locale("tr", "TR")).format(Date())

            dateTime.text = currentDate.toString()


            maxTemp.text = "Max ${f2c(body.main?.temp_max!!)}°"
            minTemp.text = "Min ${f2c(body?.main?.temp_min!!)}°"
            temp.text = "${f2c(body?.main?.temp!!)}°"

            weatherTitle.text = getTurkishWeather(body.weather[0].main)


            sunriseValue.text = ts2td(body.sys.sunrise.toLong())
            sunsetValue.text = ts2td(body.sys.sunset.toLong())

            pressureValue.text = body.main.pressure.toString()
            humidityValue.text = body.main.humidity.toString() + "%"

            tempFValue.text = ""

            citySearch.setText(body.name)
            temp.text = String.format("%.1f°C", f2c(body?.main?.temp!!))

            windValue.text = body.wind.speed.toString() + "m/s"
            groundValue.text = body.main.grnd_level.toString()
            seaValue.text = body.main.sea_level.toString()
            countryValue.text = body.sys.country

        }
        updateUI(body.weather[0].id)
    }

    private fun getTurkishWeather(condition: String): String {
        return when (condition) {
            "Clouds" -> "Bulutlu"
            "Clear" -> "Açık"
            "Rain" -> "Yağmurlu"
            "Snow" -> "Karlı"
            "Thunderstorm" -> "Gök gürültülü"
            "Drizzle" -> "Çisenti"
            "Mist" -> "Sisli"
            "Haze" -> "Dumanlı"
            "Fog" -> "Sis"
            else -> "Belirsiz"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun ts2td(ts: Long): String {

        val utcTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        utcTime.timeZone = TimeZone.getTimeZone("UTC")

        val localTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        localTime.timeZone = TimeZone.getTimeZone("GMT+03:00")

        val date = utcTime.parse(utcTime.format(Date(ts * 1000)))
        return localTime.format(date)
    }

    private fun f2c(t: Double): Double {

        val celsiusTemp = t - 273.15
        return celsiusTemp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

    private fun updateUI(id: Int) {
        binding.apply {

            when (id) {
                // ThunderStorm
                in 200..232 -> {
                    weatherImg.setImageResource(R.drawable.ic_storm_weather)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.thunderstrom_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.thunderstrom_bg)

                }
                //Drizzle
                in 300..321 -> {
                    weatherImg.setImageResource(R.drawable.ic_few_clouds)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.drizzle_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.drizzle_bg)

                }
                //Rain
                in 500..531 -> {
                    weatherImg.setImageResource(R.drawable.ic_rainy_weather)

                    mainLayout.background = ContextCompat.getDrawable(
                        this@MainActivity,
                        R.drawable.rain_bg
                    )

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.rain_bg)
                }
                //Snow
                in 600..622 -> {
                    weatherImg.setImageResource(R.drawable.ic_snow_weather)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.snow_bg)


                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.snow_bg)

                }
                //Atmosphere
                in 701..781 -> {
                    weatherImg.setImageResource(R.drawable.ic_broken_clouds)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.atmosphere_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.atmosphere_bg)

                }
                //Clear
                800 -> {

                    weatherImg.setImageResource(R.drawable.ic_clear_day)
                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.clear_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.clear_bg)

                }
                //Clouds
                in 801..804 -> {
                    weatherImg.setImageResource(R.drawable.ic_cloudy_weather)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.clouds_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.clouds_bg)
                }

                else -> {
                    weatherImg.setImageResource(R.drawable.ic_unknown)

                    mainLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.unknown_bg)

                    optionsLayout.background = ContextCompat
                        .getDrawable(this@MainActivity, R.drawable.unknown_bg)
                }
            }
        }
    }
}



