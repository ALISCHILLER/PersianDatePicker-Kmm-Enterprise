package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianYearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DatePickerInitialSelectionTest {
    @Test
    fun initialDateSnapsToNearestSelectableDateWhenPreferredIsBlocked() {
        val blocked = PersianDate(1404, 1, 1)
        val constraints = DatePickerConstraints(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 1, 10),
            disabledDates = setOf(blocked),
        )

        assertEquals(PersianDate(1404, 1, 2), constraints.resolveInitialDateOrNull(blocked))
    }

    @Test
    fun visibleMonthFallsBackToConstrainedMonthWhenNoDateIsSelectable() {
        val constraints = DatePickerConstraints(
            minDate = PersianDate(1404, 2, 1),
            maxDate = PersianDate(1404, 2, 3),
            dateValidator = { false },
        )

        assertNull(constraints.resolveInitialDateOrNull(PersianDate(1404, 2, 2)))
        assertEquals(PersianYearMonth(1404, 2), constraints.resolveVisibleMonth(PersianDate(1404, 2, 2)))
    }

    @Test
    fun initialRangeIsOrderedAndValidated() {
        val constraints = DatePickerConstraints(maxRangeLength = 7)

        val range = constraints.resolveInitialRange(
            preferredStartDate = PersianDate(1404, 1, 7),
            preferredEndDate = PersianDate(1404, 1, 1),
        )

        assertEquals(PersianDate(1404, 1, 1), range?.start)
        assertEquals(PersianDate(1404, 1, 7), range?.endInclusive)
    }

    @Test
    fun invalidInitialRangeFallsBackToSingleStartDate() {
        val constraints = DatePickerConstraints(maxRangeLength = 3)
        val start = PersianDate(1404, 1, 1)

        val range = constraints.resolveInitialRange(
            preferredStartDate = start,
            preferredEndDate = PersianDate(1404, 1, 10),
        )

        assertEquals(start, range?.start)
        assertEquals(start, range?.endInclusive)
    }
    @Test
    fun singleDayInitialRangeStaysCompleteWhenBothDatesAreProvided() {
        val date = PersianDate(1404, 1, 3)

        val selection = DatePickerConstraints().resolveInitialRangeSelection(
            preferredStartDate = date,
            preferredEndDate = date,
        )

        assertEquals(date, selection.startDate)
        assertEquals(date, selection.endDate)
    }

    @Test
    fun startOnlyInitialRangeRemainsPending() {
        val date = PersianDate(1404, 1, 3)

        val selection = DatePickerConstraints().resolveInitialRangeSelection(
            preferredStartDate = date,
            preferredEndDate = null,
        )

        assertEquals(date, selection.startDate)
        assertNull(selection.endDate)
    }

}
