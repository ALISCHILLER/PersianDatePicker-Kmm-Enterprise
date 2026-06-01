package com.msa.persiandatepicker.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PersianDateRangeContractTest {
    @Test
    fun orderedAlwaysNormalizesStartAndEnd() {
        val a = PersianDate(1404, 2, 10)
        val b = PersianDate(1404, 1, 20)

        val range = PersianDateRange.ordered(a, b)

        assertEquals(b, range.start)
        assertEquals(a, range.endInclusive)
    }

    @Test
    fun rangeLengthIsInclusive() {
        val start = PersianDate(1404, 1, 1)
        val end = PersianDate(1404, 1, 1)

        assertEquals(1, PersianDateRange(start, end).lengthInDays)
    }

    @Test
    fun overlapsDetectsIntersectionAndSeparation() {
        val first = PersianDateRange(PersianDate(1404, 1, 1), PersianDate(1404, 1, 10))
        val overlapping = PersianDateRange(PersianDate(1404, 1, 10), PersianDate(1404, 1, 20))
        val separate = PersianDateRange(PersianDate(1404, 2, 1), PersianDate(1404, 2, 2))

        assertTrue(first.overlaps(overlapping))
        assertFalse(first.overlaps(separate))
    }

    @Test
    fun constructorRejectsReversedRanges() {
        assertFailsWith<IllegalArgumentException> {
            PersianDateRange(PersianDate(1404, 2, 1), PersianDate(1404, 1, 1))
        }
    }
}
