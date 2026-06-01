package com.msa.persiandatepicker.ui

import com.msa.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DatePickerRangeValidationResultTest {
    @Test
    fun validateRangeReturnsTypedResultForMaxLength() {
        val constraints = DatePickerConstraints(maxRangeLength = 2)

        val result = constraints.validateRange(
            PersianDate(1403, 1, 1),
            PersianDate(1403, 1, 4),
        )

        val typed = assertIs<DatePickerRangeValidationResult.ExceedsMaxRangeLength>(result)
        assertEquals(4, typed.actualLength)
        assertEquals(2, typed.maxLength)
    }

    @Test
    fun validateRangeReturnsUnselectableDateForDisabledDayInsideRange() {
        val disabled = PersianDate(1403, 1, 3)
        val constraints = DatePickerConstraints(disabledDates = setOf(disabled))

        val result = constraints.validateRange(
            PersianDate(1403, 1, 1),
            PersianDate(1403, 1, 5),
        )

        val typed = assertIs<DatePickerRangeValidationResult.UnselectableDate>(result)
        assertEquals(disabled, typed.date)
    }

    @Test
    fun validateAndSetRangeKeepsStateUntouchedWhenRejected() {
        val state = PersianDateRangePickerState(
            initialStartDate = PersianDate(1403, 1, 1),
            initialEndDate = PersianDate(1403, 1, 2),
            initialVisibleMonth = PersianDate(1403, 1, 1).yearMonth,
        )
        val constraints = DatePickerConstraints(maxRangeLength = 1)

        val result = state.validateAndSetRange(
            start = PersianDate(1403, 1, 10),
            end = PersianDate(1403, 1, 12),
            constraints = constraints,
        )

        assertIs<DatePickerRangeValidationResult.ExceedsMaxRangeLength>(result)
        assertEquals(PersianDate(1403, 1, 1), state.startDate)
        assertEquals(PersianDate(1403, 1, 2), state.endDate)
    }

    @Test
    fun validateAndSetRangeAcceptsSingleDayRange() {
        val date = PersianDate(1403, 1, 10)
        val state = PersianDateRangePickerState(null, null, date.yearMonth)

        val result = state.validateAndSetRange(date, date)

        assertTrue(result.isValid)
        assertEquals(date, state.startDate)
        assertEquals(date, state.endDate)
    }
}
