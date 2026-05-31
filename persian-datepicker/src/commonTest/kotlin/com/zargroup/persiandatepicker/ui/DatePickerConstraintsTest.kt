package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatePickerConstraintsTest {
    @Test
    fun constraintsRespectBoundsAndDisabledDates() {
        val constraints = DatePickerConstraints(
            minDate = PersianDate(1403, 1, 1),
            maxDate = PersianDate(1403, 1, 10),
            disabledDates = setOf(PersianDate(1403, 1, 5)),
        )

        assertFalse(constraints.isDateSelectable(PersianDate(1402, 12, 29)))
        assertFalse(constraints.isDateSelectable(PersianDate(1403, 1, 5)))
        assertTrue(constraints.isDateSelectable(PersianDate(1403, 1, 6)))
    }

    @Test
    fun rangeLimitIsInclusive() {
        val constraints = DatePickerConstraints(maxRangeLength = 7)
        assertTrue(constraints.isRangeSelectable(PersianDate(1403, 1, 1), PersianDate(1403, 1, 7)))
        assertFalse(constraints.isRangeSelectable(PersianDate(1403, 1, 1), PersianDate(1403, 1, 8)))
    }

    @Test
    fun rangeValidationLimitPreventsUnboundedScans() {
        val constraints = DatePickerConstraints(maxRangeValidationDays = 3)
        assertTrue(constraints.isRangeSelectable(PersianDate(1404, 1, 1), PersianDate(1404, 1, 3)))
        assertFalse(constraints.isRangeSelectable(PersianDate(1404, 1, 1), PersianDate(1404, 1, 4)))
    }
}
