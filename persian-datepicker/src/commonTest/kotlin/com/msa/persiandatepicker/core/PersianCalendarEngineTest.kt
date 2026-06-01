package com.msa.persiandatepicker.core

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PersianCalendarEngineTest {
    @Test
    fun knownPersianDatesConvertToGregorian() {
        assertEquals(LocalDate(2024, 3, 20), PersianDate(1403, 1, 1).toGregorian())
        assertEquals(LocalDate(2025, 3, 21), PersianDate(1404, 1, 1).toGregorian())
        assertEquals(LocalDate(2021, 3, 20), PersianDate(1399, 12, 30).toGregorian())
        assertEquals(LocalDate(2021, 3, 21), PersianDate(1400, 1, 1).toGregorian())
    }

    @Test
    fun knownGregorianDatesConvertToPersian() {
        assertEquals(PersianDate(1403, 1, 1), PersianCalendarEngine.fromGregorian(LocalDate(2024, 3, 20)))
        assertEquals(PersianDate(1404, 1, 1), PersianCalendarEngine.fromGregorian(LocalDate(2025, 3, 21)))
        assertEquals(PersianDate(1399, 12, 30), PersianCalendarEngine.fromGregorian(LocalDate(2021, 3, 20)))
    }

    @Test
    fun monthLengthsRespectLeapYears() {
        assertTrue(PersianCalendarEngine.isLeapYear(1399))
        assertEquals(30, PersianCalendarEngine.monthLength(1399, 12))
        assertFalse(PersianCalendarEngine.isLeapYear(1400))
        assertEquals(29, PersianCalendarEngine.monthLength(1400, 12))
    }

    @Test
    fun dateArithmeticCrossesMonthAndYearBoundaries() {
        assertEquals(PersianDate(1403, 1, 1), PersianDate(1402, 12, 29).plusDays(1))
        assertEquals(PersianDate(1404, 1, 1), PersianDate(1403, 12, 30).plusDays(1))
        assertEquals(31, PersianDate(1403, 1, 1).daysUntil(PersianDate(1403, 2, 1)))
    }

    @Test
    fun localizedParsingSupportsPersianAndLatinDigits() {
        assertEquals(PersianDate(1403, 1, 5), PersianDate.parseOrNull("۱۴۰۳/۰۱/۰۵"))
        assertEquals(PersianDate(1403, 1, 5), PersianDate.parseOrNull("1403-01-05"))
    }
}
