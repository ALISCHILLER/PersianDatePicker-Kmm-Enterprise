package com.zargroup.persiandatepicker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianDateRange
import com.zargroup.persiandatepicker.core.PersianYearMonth

@Stable
public class PersianDatePickerState public constructor(
    initialSelectedDate: PersianDate?,
    initialVisibleMonth: PersianYearMonth,
) {
    public var selectedDate: PersianDate? by mutableStateOf(initialSelectedDate)
        private set

    public var visibleMonth: PersianYearMonth by mutableStateOf(initialVisibleMonth)
        private set

    public fun select(date: PersianDate?, constraints: DatePickerConstraints = DatePickerConstraints()): Unit {
        trySelect(date, constraints)
    }

    /**
     * Attempts to set the selected date and returns whether the mutation was accepted.
     * Passing `null` clears the current selection.
     */
    public fun trySelect(date: PersianDate?, constraints: DatePickerConstraints = DatePickerConstraints()): Boolean {
        if (date == null) {
            selectedDate = null
            return true
        }
        if (!constraints.isDateSelectable(date)) return false
        selectedDate = date
        visibleMonth = date.yearMonth
        return true
    }

    public fun applyTap(
        requestedDate: PersianDate,
        constraints: DatePickerConstraints = DatePickerConstraints(),
        policy: DatePickerSelectionPolicy = DatePickerSelectionPolicy(),
    ): Unit {
        val resolved = policy.resolveSingleTap(requestedDate, constraints) ?: return
        selectedDate = resolved
        visibleMonth = resolved.yearMonth
    }

    public fun clearSelection(): Unit {
        selectedDate = null
    }

    public fun showMonth(month: PersianYearMonth): Unit {
        visibleMonth = month
    }

    public fun showDate(date: PersianDate): Unit {
        visibleMonth = date.yearMonth
    }

    public fun nextMonth(): Unit {
        visibleMonth = visibleMonth.plusMonths(1)
    }

    public fun previousMonth(): Unit {
        visibleMonth = visibleMonth.minusMonths(1)
    }
}

@Composable
public fun rememberPersianDatePickerState(
    initialSelectedDate: PersianDate? = PersianDate.today(),
    initialVisibleMonth: PersianYearMonth = (initialSelectedDate ?: PersianDate.today()).yearMonth,
): PersianDatePickerState = remember(initialSelectedDate, initialVisibleMonth) {
    PersianDatePickerState(initialSelectedDate, initialVisibleMonth)
}

@Stable
public class PersianDateRangePickerState public constructor(
    initialStartDate: PersianDate?,
    initialEndDate: PersianDate?,
    initialVisibleMonth: PersianYearMonth,
) {
    public var startDate: PersianDate? by mutableStateOf(initialStartDate)
        private set

    public var endDate: PersianDate? by mutableStateOf(initialEndDate)
        private set

    public var visibleMonth: PersianYearMonth by mutableStateOf(initialVisibleMonth)
        private set

    public val selectedRange: PersianDateRange?
        get() {
            val start = startDate ?: return null
            val end = endDate ?: return null
            return PersianDateRange.ordered(start, end)
        }

    /**
     * Attempts to set a complete or pending range and returns whether it satisfies [constraints].
     * Passing two null values clears the current range.
     */
    public fun validateAndSetRange(
        start: PersianDate?,
        end: PersianDate?,
        constraints: DatePickerConstraints = DatePickerConstraints(),
    ): DatePickerRangeValidationResult {
        if (start == null && end == null) {
            clearSelection()
            return DatePickerRangeValidationResult.Valid
        }
        val first = start ?: end ?: return DatePickerRangeValidationResult.UnselectableDate(PersianDate.today())
        if (end == null) {
            if (!constraints.isDateSelectable(first)) return DatePickerRangeValidationResult.UnselectableDate(first)
            startDate = first
            endDate = null
            visibleMonth = first.yearMonth
            return DatePickerRangeValidationResult.Valid
        }
        val candidate = PersianDateRange.ordered(first, end)
        val result = constraints.validateRange(candidate)
        if (!result.isValid) return result
        startDate = candidate.start
        endDate = candidate.endInclusive
        visibleMonth = candidate.start.yearMonth
        return DatePickerRangeValidationResult.Valid
    }

    public fun trySetRange(
        start: PersianDate?,
        end: PersianDate?,
        constraints: DatePickerConstraints = DatePickerConstraints(),
    ): Boolean = validateAndSetRange(start, end, constraints).isValid

    public fun select(date: PersianDate, constraints: DatePickerConstraints = DatePickerConstraints()): Unit {
        applyTap(date, constraints, DatePickerSelectionPolicy())
    }

    public fun applyTap(
        requestedDate: PersianDate,
        constraints: DatePickerConstraints = DatePickerConstraints(),
        policy: DatePickerSelectionPolicy = DatePickerSelectionPolicy(),
    ): Unit {
        val (newStart, newEnd) = policy.resolveRangeTap(
            currentStart = startDate,
            currentEnd = endDate,
            requestedDate = requestedDate,
            constraints = constraints,
        )
        if (newStart == startDate && newEnd == endDate) return
        startDate = newStart
        endDate = newEnd
        (newEnd ?: newStart)?.let { visibleMonth = it.yearMonth }
    }

    public fun clearSelection(): Unit {
        startDate = null
        endDate = null
    }

    public fun showMonth(month: PersianYearMonth): Unit {
        visibleMonth = month
    }

    public fun nextMonth(): Unit {
        visibleMonth = visibleMonth.plusMonths(1)
    }

    public fun previousMonth(): Unit {
        visibleMonth = visibleMonth.minusMonths(1)
    }
}

@Composable
public fun rememberPersianDateRangePickerState(
    initialStartDate: PersianDate? = null,
    initialEndDate: PersianDate? = null,
    initialVisibleMonth: PersianYearMonth = (initialStartDate ?: PersianDate.today()).yearMonth,
): PersianDateRangePickerState = remember(initialStartDate, initialEndDate, initialVisibleMonth) {
    PersianDateRangePickerState(initialStartDate, initialEndDate, initialVisibleMonth)
}
