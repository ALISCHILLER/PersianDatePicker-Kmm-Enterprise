package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatePickerDiagnosticsTest {
    @Test
    fun standardPresetPassesPreflight() {
        val config = PersianDatePickerEnterprisePresets.standardPersian(
            minDate = PersianDate(1400, 1, 1),
            maxDate = PersianDate(1401, 12, 29),
        )

        val diagnostics = DatePickerConfigValidator.validate(config)

        assertEquals("CONFIG_OK", diagnostics.single().code)
        assertFalse(DatePickerConfigValidator.hasErrors(config))
    }

    @Test
    fun noSelectableDateIsReportedAsError() {
        val config = DatePickerConfig(
            constraints = DatePickerConstraints(
                minDate = PersianDate(1402, 1, 1),
                maxDate = PersianDate(1402, 1, 1),
                dateValidator = { false },
            ),
            yearRange = 1402..1402,
        )

        val diagnostics = DatePickerConfigValidator.validate(config, selectableSearchRadiusDays = 1)

        assertTrue(diagnostics.any { it.code == "NO_SELECTABLE_DATE" })
        assertTrue(DatePickerConfigValidator.hasErrors(config))
    }

    @Test
    fun bookingWindowKeepsYearRangeAlignedWithConstraints() {
        val today = PersianDate(1403, 12, 20)
        val config = PersianDatePickerEnterprisePresets.bookingWindow(today = today, daysAhead = 20)

        val minDate = requireNotNull(config.constraints.minDate)
        val maxDate = requireNotNull(config.constraints.maxDate)
        assertTrue(minDate.year in config.yearRange)
        assertTrue(maxDate.year in config.yearRange)
    }
}
