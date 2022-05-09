package com.example.simpleweatherapp.model.openweather

enum class UnitSystem(private val value: String) {
    METRIC("metric"), IMPERIAL("imperial");

    override fun toString(): String = this.value
}