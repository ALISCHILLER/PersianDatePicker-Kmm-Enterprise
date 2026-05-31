package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianYearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalendarMonthCellsTest {
    @Test
    fun gridAlwaysReturnsFiveOrSixWeeks() {
        val cells = buildCalendarMonthCells(
            yearMonth = PersianYearMonth(1403, 1),
            weekConfiguration = WeekConfiguration.persian(),
            showAdjacentMonthDays = true,
        )

        assertTrue(cells.size == 35 || cells.size == 42)
        assertEquals(0, cells.size % 7)
    }
}
