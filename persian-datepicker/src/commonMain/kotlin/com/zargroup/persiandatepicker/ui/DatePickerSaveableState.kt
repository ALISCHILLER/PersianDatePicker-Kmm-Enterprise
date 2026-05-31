package com.zargroup.persiandatepicker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianYearMonth

private fun PersianDate?.toToken(): String = this?.let { "${it.year}-${it.month}-${it.day}" }.orEmpty()
private fun PersianYearMonth.toToken(): String = "$year-$month"

private fun parseDateToken(token: String): PersianDate? {
    if (token.isBlank()) return null
    val parts = token.split('-')
    if (parts.size != 3) return null
    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val day = parts[2].toIntOrNull() ?: return null
    return runCatching { PersianDate(year, month, day) }.getOrNull()
}

private fun parseYearMonthToken(token: String): PersianYearMonth? {
    val parts = token.split('-')
    if (parts.size != 2) return null
    val year = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    return runCatching { PersianYearMonth(year, month) }.getOrNull()
}

public fun persianDatePickerStateSaver(): Saver<PersianDatePickerState, List<String>> = Saver(
    save = { state -> listOf(state.selectedDate.toToken(), state.visibleMonth.toToken()) },
    restore = { tokens ->
        val selected = tokens.getOrNull(0)?.let(::parseDateToken)
        val visible = tokens.getOrNull(1)?.let(::parseYearMonthToken) ?: selected?.yearMonth ?: PersianDate.today().yearMonth
        PersianDatePickerState(selected, visible)
    },
)

@Composable
public fun rememberSaveablePersianDatePickerState(
    initialSelectedDate: PersianDate? = PersianDate.today(),
    initialVisibleMonth: PersianYearMonth = (initialSelectedDate ?: PersianDate.today()).yearMonth,
): PersianDatePickerState = rememberSaveable(
    saver = persianDatePickerStateSaver(),
) {
    PersianDatePickerState(initialSelectedDate, initialVisibleMonth)
}

public fun persianDateRangePickerStateSaver(): Saver<PersianDateRangePickerState, List<String>> = Saver(
    save = { state ->
        listOf(
            state.startDate.toToken(),
            state.endDate.toToken(),
            state.visibleMonth.toToken(),
        )
    },
    restore = { tokens ->
        val start = tokens.getOrNull(0)?.let(::parseDateToken)
        val end = tokens.getOrNull(1)?.let(::parseDateToken)
        val visible = tokens.getOrNull(2)?.let(::parseYearMonthToken) ?: start?.yearMonth ?: PersianDate.today().yearMonth
        PersianDateRangePickerState(start, end, visible)
    },
)

@Composable
public fun rememberSaveablePersianDateRangePickerState(
    initialStartDate: PersianDate? = null,
    initialEndDate: PersianDate? = null,
    initialVisibleMonth: PersianYearMonth = (initialStartDate ?: PersianDate.today()).yearMonth,
): PersianDateRangePickerState = rememberSaveable(
    saver = persianDateRangePickerStateSaver(),
) {
    PersianDateRangePickerState(initialStartDate, initialEndDate, initialVisibleMonth)
}
