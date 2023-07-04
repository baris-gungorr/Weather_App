package com.barisgungorr.data

import retrofit2.Retrofit

object ApıUtilities {
    private var retrofit:Retrofit? = null

    var BASE_URL = "https://api.openweathermap.org/data/2.5/"

    fun getApiInterface(): WeatherApı?{

        if (retrofit==null) {


            retrofit =

        }

    }

}