package com.zargroup.persiandatepicker.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class PersianDateParserTest {
    @Test
    fun parsesPersianAndLatinDigits() {
        assertEquals(PersianDate(1404, 12, 1), PersianDateParser.parseOrNull("۱۴۰۴/۱۲/۰۱"))
        assertEquals(PersianDate(1404, 12, 1), PersianDateParser.parseOrNull("1404-12-01"))
    }

    @Test
    fun reportsInvalidFormatAndDateSeparately() {
        assertIs<PersianDateParseResult.InvalidFormat>(PersianDateParser.parse("1404/12"))
        assertIs<PersianDateParseResult.InvalidDate>(PersianDateParser.parse("1404/12/31"))
        assertNull(PersianDateParser.parseOrNull("not-a-date"))
    }

    @Test
    fun stableKeysAreDeterministic() {
        assertEquals("1404-01-09", PersianDate(1404, 1, 9).toStableKey())
    }
}
