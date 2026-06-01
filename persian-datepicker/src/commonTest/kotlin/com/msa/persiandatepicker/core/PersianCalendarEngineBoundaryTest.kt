package com.msa.persiandatepicker.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PersianCalendarEngineBoundaryTest {
    @Test
    fun unsupportedYearsAreReportedWithoutThrowingFromIsValidDate() {
        assertFalse(PersianCalendarEngine.isValidDate(PersianCalendarEngine.supportedYearRange.first - 1, 1, 1))
        assertFalse(PersianCalendarEngine.isValidDate(PersianCalendarEngine.supportedYearRange.last + 1, 1, 1))
        assertTrue(PersianCalendarEngine.isValidDate(1404, 1, 1))
    }
}
