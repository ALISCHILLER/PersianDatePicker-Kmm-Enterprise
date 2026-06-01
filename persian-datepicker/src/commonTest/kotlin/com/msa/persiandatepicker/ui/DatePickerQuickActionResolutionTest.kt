package com.msa.persiandatepicker.ui

import com.msa.persiandatepicker.core.PersianDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DatePickerQuickActionResolutionTest {
    @Test
    fun resolvedQuickActionsDeduplicateBuiltInTodayAction() {
        val config = DatePickerConfig(
            showTodayAction = true,
            quickActions = listOf(
                DatePickerQuickAction.Today,
                DatePickerQuickAction.JumpToDate("Nowruz") { PersianDate(1404, 1, 1) },
            ),
        )

        val actions = config.resolvedQuickActions()

        assertEquals(2, actions.size)
        assertEquals(1, actions.count { it is DatePickerQuickAction.Today })
        assertIs<DatePickerQuickAction.JumpToDate>(actions.last())
    }

    @Test
    fun resolvedQuickActionsKeepsExplicitTodayWhenBuiltInTodayIsDisabled() {
        val config = DatePickerConfig(
            showTodayAction = false,
            quickActions = listOf(DatePickerQuickAction.Today),
        )

        assertEquals(listOf(DatePickerQuickAction.Today), config.resolvedQuickActions())
    }

    @Test
    fun validatorReportsDedupedTodayAsNonBlockingInfo() {
        val config = DatePickerConfig(
            quickActions = listOf(DatePickerQuickAction.Today),
        )

        val report = DatePickerConfigValidator.validateProductionReady(config)

        assertTrue(report.isReady)
        assertTrue(report.infos.any { it.code == DatePickerDiagnosticCode.DuplicateTodayActionDeduped })
    }
}
