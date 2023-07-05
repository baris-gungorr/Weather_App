package com.barisgungorr.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApıUtilities {
    private var retrofit:Retrofit? = null

    var BASE_URL = "https://api.openweathermap.org/data/2.5/"

    fun getApiInterface(): WeatherApı?{

        if (retrofit==null) {


            retrofit =Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()

        }
        return retrofit?.create(WeatherApı::class.java)

    }

}