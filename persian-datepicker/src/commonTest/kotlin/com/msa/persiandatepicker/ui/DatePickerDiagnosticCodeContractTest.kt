package com.msa.persiandatepicker.ui

import com.msa.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DatePickerDiagnosticCodeContractTest {
    @Test
    fun reportSupportsStableCodeLookups() {
        val report = DatePickerValidationReport(
            diagnostics = listOf(
                DatePickerDiagnostic(
                    severity = DatePickerDiagnosticSeverity.Warning,
                    code = DatePickerDiagnosticCode.ClearOnlyQuickActions,
                    message = "Only clear action is configured.",
                ),
            ),
        )

        assertTrue(report.hasCode(DatePickerDiagnosticCode.ClearOnlyQuickActions))
        assertTrue(report.hasWarning(DatePickerDiagnosticCode.ClearOnlyQuickActions))
        assertFalse(report.hasError(DatePickerDiagnosticCode.ClearOnlyQuickActions))
        assertFalse(report.hasInfo(DatePickerDiagnosticCode.ClearOnlyQuickActions))
    }

    @Test
    fun validatorUsesStableCodesForBlockingErrors() {
        val config = DatePickerConfig(
            constraints = DatePickerConstraints(
                minDate = PersianDate(1404, 1, 1),
                maxDate = PersianDate(1404, 1, 2),
                dateValidator = { false },
            ),
            yearRange = 1404..1404,
        )

        val report = DatePickerConfigValidator.validateProductionReady(
            config = config,
            selectableSearchRadiusDays = 1,
        )

        assertFalse(report.isReady)
        assertTrue(report.hasError(DatePickerDiagnosticCode.NoSelectableDate))
    }
}
