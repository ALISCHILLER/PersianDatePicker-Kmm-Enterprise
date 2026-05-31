package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianCalendarEngine
import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianYearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DatePickerEnterpriseUltraContractTest {
    @Test
    fun monthGridDoesNotCrashAtSupportedCalendarBoundaries() {
        val firstSupportedMonth = PersianYearMonth(PersianCalendarEngine.supportedYearRange.first, 1)
        val lastSupportedMonth = PersianYearMonth(PersianCalendarEngine.supportedYearRange.last, 12)

        val firstCells = buildCalendarMonthCells(firstSupportedMonth, WeekConfiguration.persian(), showAdjacentMonthDays = true)
        val lastCells = buildCalendarMonthCells(lastSupportedMonth, WeekConfiguration.persian(), showAdjacentMonthDays = true)

        assertTrue(firstCells.size == 35 || firstCells.size == 42)
        assertTrue(lastCells.size == 35 || lastCells.size == 42)
        assertTrue(firstCells.any { it.position == MonthCellPosition.Empty || it.position == MonthCellPosition.Current })
        assertTrue(lastCells.any { it.position == MonthCellPosition.Empty || it.position == MonthCellPosition.Current })
    }

    @Test
    fun diagnosticsFailWhenSelectableConstraintWindowDoesNotIntersectVisibleYearRange() {
        val config = DatePickerConfig(
            constraints = DatePickerConstraints(
                minDate = PersianDate(1404, 1, 1),
                maxDate = PersianDate(1404, 1, 5),
            ),
            yearRange = 1405..1406,
        )

        val report = DatePickerConfigValidator.validateProductionReady(config)

        assertFalse(report.isReady)
        assertTrue(report.errors.any { it.code == "NO_SELECTABLE_DATE_IN_YEAR_RANGE" })
    }

    @Test
    fun configAwareInitialSelectionRespectsVisibleYearRange() {
        val config = DatePickerConfig(
            constraints = DatePickerConstraints(
                minDate = PersianDate(1404, 1, 1),
                maxDate = PersianDate(1405, 1, 5),
            ),
            yearRange = 1405..1405,
        )

        val resolved = config.resolveInitialDateOrNull(PersianDate(1404, 1, 2), fallbackDate = PersianDate(1404, 1, 3))

        assertEquals(PersianDate(1405, 1, 1), resolved)
    }

    @Test
    fun stateNavigationCanBeBoundedByVisibleYearRange() {
        val state = PersianDatePickerState(
            initialSelectedDate = null,
            initialVisibleMonth = PersianYearMonth(1404, 1),
        )

        assertFalse(state.previousMonth(1404..1404))
        assertEquals(PersianYearMonth(1404, 1), state.visibleMonth)
        assertTrue(state.nextMonth(1404..1404))
        assertEquals(PersianYearMonth(1404, 2), state.visibleMonth)
    }

    @Test
    fun yearMonthSafeArithmeticReturnsNullOutsideSupportedRange() {
        val firstSupportedMonth = PersianYearMonth(PersianCalendarEngine.supportedYearRange.first, 1)
        val lastSupportedMonth = PersianYearMonth(PersianCalendarEngine.supportedYearRange.last, 12)

        assertNull(firstSupportedMonth.tryMinusMonths(1))
        assertNull(lastSupportedMonth.tryPlusMonths(1))
    }
}
