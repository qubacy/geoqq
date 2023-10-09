package com.qubacy.geoqq.ui.common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object TimeUtils {
    fun longToHoursMinutesSecondsFormattedString(
        gmtTimeAsLong: Long, locale: Locale, timeZone: TimeZone
    ): String {
        // todo: should it be changeable in order to deal with different time formats (12- or 24-hours)??
        val hoursMinutesSecondsTimeFormatter = SimpleDateFormat("hh:mm:ss", locale)

        val zonedTime = gmtTimeAsLong + timeZone.rawOffset + timeZone.dstSavings
        val calendar = Calendar.getInstance(timeZone).apply {
            timeInMillis = zonedTime
        }

        return hoursMinutesSecondsTimeFormatter.format(calendar.time)
    }
}