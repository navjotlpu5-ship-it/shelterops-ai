package com.example.network.weather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMeteoWeatherResponse(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val elevation: Double? = null,
    val timezone: String? = null,
    val current: CurrentWeatherDto? = null,
    val hourly: HourlyWeatherDto? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    val time: String? = null,
    @Json(name = "temperature_2m") val temperature2m: Double? = null,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Int? = null,
    val precipitation: Double? = null,
    val rain: Double? = null,
    @Json(name = "surface_pressure") val surfacePressure: Double? = null,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double? = null,
    @Json(name = "wind_direction_10m") val windDirection10m: Double? = null,
    @Json(name = "weather_code") val weatherCode: Int? = null
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherDto(
    val time: List<String>? = null,
    @Json(name = "temperature_2m") val temperature2m: List<Double>? = null,
    val precipitation: List<Double>? = null,
    @Json(name = "surface_pressure") val surfacePressure: List<Double>? = null
)

data class LiveWeatherData(
    val temperatureCelsius: Double,
    val relativeHumidityPercent: Int,
    val rainfallMm: Double,
    val windSpeedKmh: Double,
    val surfacePressureHpa: Double,
    val conditionDescription: String,
    val wmoCode: Int,
    val fetchedAt: String,
    val isLive: Boolean,
    val dataSource: String
)
