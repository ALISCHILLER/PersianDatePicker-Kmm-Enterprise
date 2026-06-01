package com.msa.persiandatepicker.ui

import com.msa.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatePickerValidationReportTest {
    @Test
    fun productionReadyReportGroupsSeverities() {
        val config = DatePickerConfig(
            constraints = DatePickerConstraints(
                minDate = PersianDate(1404, 1, 1),
                maxDate = PersianDate(1404, 1, 3),
                dateValidator = { false },
            ),
            yearRange = 1404..1404,
        )

        val report = DatePickerConfigValidator.validateProductionReady(
            config = config,
            selectableSearchRadiusDays = 2,
        )

        assertFalse(report.isReady)
        assertTrue(report.errors.any { it.code == DatePickerDiagnosticCode.NoSelectableDate })
        assertTrue(report.summary.contains("errors=1"))
    }

    @Test
    fun requireProductionReadyThrowsOnlyForBlockingErrors() {
        val validConfig = PersianDatePickerEnterprisePresets.standardPersian(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 1, 10),
        )
        DatePickerConfigValidator.requireProductionReady(validConfig)

        val invalidConfig = DatePickerConfig(
            constraints = DatePickerConstraints(
                minDate = PersianDate(1404, 1, 1),
                maxDate = PersianDate(1404, 1, 1),
                dateValidator = { false },
            ),
            yearRange = 1404..1404,
        )

        val exception = assertFailsWith<DatePickerConfigurationException> {
            DatePickerConfigValidator.requireProductionReady(invalidConfig)
        }
        assertEquals(DatePickerDiagnosticCode.NoSelectableDate, exception.errors.single().code)
    }

    @Test
    fun standardEnglishPresetUsesLatinDigitsAndInternationalWeek() {
        val config = PersianDatePickerEnterprisePresets.standardEnglish(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 1, 10),
        )

        assertEquals(DatePickerStrings.english(), config.strings)
        assertEquals(WeekConfiguration.international(), config.weekConfiguration)
        assertTrue(DatePickerConfigValidator.validateProductionReady(config).isReady)
    }
}
