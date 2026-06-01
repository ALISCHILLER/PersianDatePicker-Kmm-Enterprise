package com.msa.persiandatepicker.ui

import com.msa.persiandatepicker.core.PersianCalendarEngine
import com.msa.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatePickerConfigContractTest {
    @Test
    fun configRejectsEmptyYearRange() {
        assertFailsWith<IllegalArgumentException> {
            DatePickerConfig(yearRange = IntRange.EMPTY)
        }
    }

    @Test
    fun constraintsCanReportWhetherAnySelectableDateExists() {
        val closedConstraints = DatePickerConstraints(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 1, 3),
            dateValidator = { false },
        )
        val openConstraints = DatePickerConstraints(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 1, 3),
        )

        assertFalse(closedConstraints.hasSelectableDate())
        assertTrue(openConstraints.hasSelectableDate())
    }
    @Test
    fun configRejectsUnsupportedYearRanges() {
        assertFailsWith<IllegalArgumentException> {
            DatePickerConfig(yearRange = (PersianCalendarEngine.supportedYearRange.first - 1)..1400)
        }
        assertFailsWith<IllegalArgumentException> {
            DatePickerConfig(yearRange = 1400..(PersianCalendarEngine.supportedYearRange.last + 1))
        }
    }
}
