package com.msa.persiandatepicker.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersianDateFormatterContractTest {
    @Test
    fun numericFormatterCanRenderPersianAndLatinDigits() {
        val date = PersianDate(1404, 1, 9)

        assertEquals("۱۴۰۴ / ۰۱ / ۰۹", PersianDateFormatter.numeric().format(date, DigitMode.Persian))
        assertEquals("1404/01/09", PersianDateFormatter.compact().format(date, DigitMode.Latin))
    }

    @Test
    fun gregorianHintKeepsBaseFormatterAndAddsGregorianDate() {
        val date = PersianDate(1404, 1, 1)
        val formatted = PersianDateFormatter.withGregorianHint(PersianDateFormatter.compact()).format(date, DigitMode.Latin)

        assertTrue(formatted.startsWith("1404/01/01"))
        assertTrue(formatted.contains("2025-03-21"))
    }
}
