package com.example.norris_app.viewmodel

import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class CountriesViewModel : ViewModel() {

    private val countries = ArrayList<String>()

    init {

        val locales = Locale.getAvailableLocales()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }
        countries.sort()
    }

    fun getCountries(): ArrayList<String> {
        return countries
    }
}