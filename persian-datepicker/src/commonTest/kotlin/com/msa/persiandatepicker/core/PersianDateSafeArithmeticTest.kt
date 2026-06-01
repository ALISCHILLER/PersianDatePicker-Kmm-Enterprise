package com.msa.persiandatepicker.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersianDateSafeArithmeticTest {
    @Test
    fun tryPlusDaysReturnsDateInsideSupportedRange() {
        assertEquals(PersianDate(1403, 1, 2), PersianDate(1403, 1, 1).tryPlusDays(1))
    }

    @Test
    fun tryMinusDaysReturnsNullOutsideSupportedRange() {
        val earliest = PersianDate(PersianCalendarEngine.supportedYearRange.first, 1, 1)

        assertNull(earliest.tryMinusDays(1))
    }
}
