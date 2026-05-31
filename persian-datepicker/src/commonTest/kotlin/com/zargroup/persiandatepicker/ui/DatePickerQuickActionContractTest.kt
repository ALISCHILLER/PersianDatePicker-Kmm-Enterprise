package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DatePickerQuickActionContractTest {
    @Test
    fun quickActionLabelsRespectLocalizedStrings() {
        val persian = DatePickerStrings.persian()
        val english = DatePickerStrings.english()

        assertEquals("امروز", DatePickerQuickAction.Today.label(persian))
        assertEquals("Today", DatePickerQuickAction.Today.label(english))
        assertEquals("Reset", DatePickerQuickAction.ClearSelection("Reset").label(english))
    }

    @Test
    fun jumpToDateCanBeOptional() {
        val action = DatePickerQuickAction.JumpToDate(
            customLabel = "Fiscal start",
            targetDateProvider = { null },
        )

        assertEquals("Fiscal start", action.label(DatePickerStrings.english()))
        assertNull(action.targetDateProvider())
    }

    @Test
    fun jumpToDateCanProvideStableDate() {
        val date = PersianDate(1404, 1, 1)
        val action = DatePickerQuickAction.JumpToDate(
            customLabel = "Nowruz",
            targetDateProvider = { date },
        )

        assertEquals(date, action.targetDateProvider())
    }
}
