package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianYearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DatePickerStateMutationTest {
    @Test
    fun singleTrySelectReportsRejectedDates() {
        val initial = PersianDate(1404, 1, 1)
        val blocked = PersianDate(1404, 1, 2)
        val state = PersianDatePickerState(initial, initial.yearMonth)
        val constraints = DatePickerConstraints(disabledDates = setOf(blocked))

        assertFalse(state.trySelect(blocked, constraints))
        assertEquals(initial, state.selectedDate)
        assertEquals(PersianYearMonth(1404, 1), state.visibleMonth)
    }

    @Test
    fun rangeTrySetRangeValidatesWholeRange() {
        val start = PersianDate(1404, 1, 1)
        val end = PersianDate(1404, 1, 5)
        val blocked = PersianDate(1404, 1, 3)
        val state = PersianDateRangePickerState(null, null, start.yearMonth)
        val constraints = DatePickerConstraints(disabledDates = setOf(blocked))

        assertFalse(state.trySetRange(start, end, constraints))
        assertNull(state.selectedRange)

        assertTrue(state.trySetRange(start, PersianDate(1404, 1, 2), constraints))
        assertEquals(start, state.selectedRange?.start)
        assertEquals(PersianDate(1404, 1, 2), state.selectedRange?.endInclusive)
    }
}
