package com.zargroup.persiandatepicker.ui

import androidx.compose.ui.unit.LayoutDirection
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeekConfigurationContractTest {
    @Test
    fun persianWeekStartsOnSaturdayAndUsesRtl() {
        val week = WeekConfiguration.persian()

        assertEquals(DayOfWeek.SATURDAY, week.orderedDays.first())
        assertEquals(LayoutDirection.Rtl, week.layoutDirection)
        assertTrue(week.isWeekend(DayOfWeek.FRIDAY))
    }

    @Test
    fun internationalWeekStartsOnMondayAndUsesLtr() {
        val week = WeekConfiguration.international()

        assertEquals(DayOfWeek.MONDAY, week.orderedDays.first())
        assertEquals(LayoutDirection.Ltr, week.layoutDirection)
        assertTrue(week.isWeekend(DayOfWeek.SATURDAY))
        assertTrue(week.isWeekend(DayOfWeek.SUNDAY))
    }
}
