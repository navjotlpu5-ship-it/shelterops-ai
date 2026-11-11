package com.example.network.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getLiveWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,precipitation,rain,surface_pressure,wind_speed_10m,wind_direction_10m,weather_code",
        @Query("hourly") hourly: String = "precipitation,surface_pressure,temperature_2m",
        @Query("timezone") timezone: String = "Asia/Kolkata",
        @Query("past_days") pastDays: Int = 1
    ): OpenMeteoWeatherResponse
}
