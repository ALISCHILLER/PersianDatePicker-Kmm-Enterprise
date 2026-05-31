package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DatePickerSelectionPolicyTest {
    @Test
    fun ignoreStrategyRejectsUnavailableDate() {
        val constraints = DatePickerConstraints(disabledDates = setOf(PersianDate(1404, 1, 1)))
        val policy = DatePickerSelectionPolicy(unavailableDateStrategy = UnavailableDateStrategy.Ignore)

        assertNull(policy.resolveSingleTap(PersianDate(1404, 1, 1), constraints))
    }

    @Test
    fun snapStrategyFindsNearestDate() {
        val disabled = PersianDate(1404, 1, 1)
        val constraints = DatePickerConstraints(disabledDates = setOf(disabled))
        val policy = DatePickerSelectionPolicy(unavailableDateStrategy = UnavailableDateStrategy.SnapToNearestAvailable)

        assertEquals(PersianDate(1404, 1, 2), policy.resolveSingleTap(disabled, constraints))
    }

    @Test
    fun rangePolicyRejectsSameDayWhenDisabled() {
        val date = PersianDate(1404, 2, 2)
        val policy = DatePickerSelectionPolicy(allowSameDayRange = false)

        assertEquals(date to null, policy.resolveRangeTap(date, null, date, DatePickerConstraints()))
    }
}
