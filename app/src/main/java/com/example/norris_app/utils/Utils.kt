package com.example.norris_app.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Utils {

    companion object {

        fun isValidDate(value: String): Boolean {
            var date: Date? = null
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                date = sdf.parse(value)
                if (value != sdf.format(date)) {
                    date = null
                }
            } catch (ex: ParseException) {
                ex.printStackTrace()
            }

            return date != null
        }
    }

}