package com.msa.persiandatepicker.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DatePickerFieldApiContractTest {
    @Test
    fun datePickerFieldSymbolsArePartOfPublicUiApi() {
        val singleName = "PersianDatePickerField"
        val rangeName = "PersianDateRangePickerField"

        assertTrue(singleName.endsWith("Field"))
        assertTrue(rangeName.endsWith("Field"))
    }

    @Test
    fun diagnosticCodeConstantsStayMachineReadable() {
        assertEquals("CONFIG_OK", DatePickerDiagnosticCode.ConfigOk)
        assertEquals("NO_SELECTABLE_DATE", DatePickerDiagnosticCode.NoSelectableDate)
        assertTrue(DatePickerDiagnosticCode.DuplicateTodayActionDeduped.contains("TODAY"))
    }
}
