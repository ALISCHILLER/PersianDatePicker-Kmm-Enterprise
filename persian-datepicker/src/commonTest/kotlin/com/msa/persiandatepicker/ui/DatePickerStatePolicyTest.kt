package com.msa.persiandatepicker.ui

import com.msa.persiandatepicker.core.PersianDate
import com.msa.persiandatepicker.core.PersianYearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DatePickerStatePolicyTest {
    @Test
    fun singleStateIgnoresUnavailableDateByDefault() {
        val state = PersianDatePickerState(
            initialSelectedDate = PersianDate(1404, 1, 1),
            initialVisibleMonth = PersianYearMonth(1404, 1),
        )
        val blocked = PersianDate(1404, 1, 2)
        val constraints = DatePickerConstraints(disabledDates = setOf(blocked))

        state.applyTap(blocked, constraints, DatePickerSelectionPolicy())

        assertEquals(PersianDate(1404, 1, 1), state.selectedDate)
        assertEquals(PersianYearMonth(1404, 1), state.visibleMonth)
    }

    @Test
    fun singleStateCanSnapUnavailableDateToNearestAvailableDate() {
        val state = PersianDatePickerState(
            initialSelectedDate = null,
            initialVisibleMonth = PersianYearMonth(1404, 1),
        )
        val blocked = PersianDate(1404, 1, 10)
        val constraints = DatePickerConstraints(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 1, 20),
            disabledDates = setOf(blocked),
        )
        val policy = DatePickerSelectionPolicy(
            unavailableDateStrategy = UnavailableDateStrategy.SnapToNearestAvailable,
        )

        state.applyTap(blocked, constraints, policy)

        assertEquals(PersianDate(1404, 1, 11), state.selectedDate)
    }

    @Test
    fun rangeStateRespectsSameDayPolicy() {
        val start = PersianDate(1404, 2, 4)
        val state = PersianDateRangePickerState(start, null, start.yearMonth)
        val policy = DatePickerSelectionPolicy(allowSameDayRange = false)

        state.applyTap(start, DatePickerConstraints(), policy)

        assertEquals(start, state.startDate)
        assertNull(state.endDate)
    }
}
