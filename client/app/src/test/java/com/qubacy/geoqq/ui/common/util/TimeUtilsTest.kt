package com.qubacy.geoqq.ui.common.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class TimeUtilsTest {
    data class LongToHoursMinutesSecondsFormattedStringTestCase(
        val timeAsLong: Long,
        val locale: Locale,
        val timeZone: TimeZone,
        val expectedString: String
    )

    // TODO: isn't working with JVM!!!

    @Test
    fun longToHoursMinutesSecondsFormattedStringTest() {
        val testCases = listOf(
            LongToHoursMinutesSecondsFormattedStringTestCase(
                1696872678000,
                Locale.forLanguageTag("RU"),
                TimeZone.getTimeZone("America/Los_Angeles"),
                "03:31 AM"
            ),
            LongToHoursMinutesSecondsFormattedStringTestCase(
                1696872678000,
                Locale.UK,
                TimeZone.getTimeZone("Asia/Krasnoyarsk"),
                "05:31 PM"
            ),
            LongToHoursMinutesSecondsFormattedStringTestCase(
                1696872678000,
                Locale.US,
                TimeZone.getTimeZone("Asia/Tel_Aviv"),
                "01:31 PM"
            ),
        )

        for (testCase in testCases) {
            val formattedString = TimeUtils.longToHoursMinutesSecondsFormattedString(
                testCase.timeAsLong,testCase.locale, testCase.timeZone)

            assertEquals(testCase.expectedString, formattedString)
        }
    }
}