package com.msa.persiandatepicker.ui

import com.msa.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DatePickerConstraintsNearestValidTest {
    @Test
    fun nearestValidSearchesForwardAndBackwardWithinBounds() {
        val anchor = PersianDate(1404, 1, 10)
        val next = PersianDate(1404, 1, 11)
        val constraints = DatePickerConstraints(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 1, 20),
            disabledDates = setOf(anchor),
        )

        assertEquals(next, constraints.nearestValidOrNull(anchor))
        assertNull(constraints.nearestValidOrNull(anchor, searchRadiusDays = 0))
    }

    @Test
    fun nearestValidReturnsNullWhenSearchWindowHasNoSelectableDate() {
        val constraints = DatePickerConstraints(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 1, 3),
            dateValidator = { false },
        )

        assertNull(constraints.nearestValidOrNull(PersianDate(1404, 1, 2), searchRadiusDays = 2))
    }
}
