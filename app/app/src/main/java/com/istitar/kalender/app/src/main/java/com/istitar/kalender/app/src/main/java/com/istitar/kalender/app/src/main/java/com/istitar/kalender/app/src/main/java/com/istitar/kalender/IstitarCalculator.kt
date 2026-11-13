package com.istitar.kalender

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object IstitarCalculator {
    fun dayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
