package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianDateRange
import com.zargroup.persiandatepicker.core.PersianYearMonth

internal data class ResolvedInitialRangeSelection(
    val startDate: PersianDate?,
    val endDate: PersianDate?,
) {
    val visibleSeed: PersianDate? get() = startDate ?: endDate
}

internal fun DatePickerConstraints.resolveInitialDateOrNull(
    preferredDate: PersianDate?,
    fallbackDate: PersianDate = PersianDate.today(),
): PersianDate? {
    return preferredDate?.let(::nearestValidOrNull)
        ?: nearestValidOrNull(fallbackDate)
        ?: minDate?.takeIf(::isDateSelectable)
        ?: maxDate?.takeIf(::isDateSelectable)
}

internal fun DatePickerConstraints.resolveVisibleMonth(
    preferredDate: PersianDate?,
    fallbackDate: PersianDate = PersianDate.today(),
): PersianYearMonth {
    return resolveInitialDateOrNull(preferredDate, fallbackDate)?.yearMonth
        ?: minDate?.yearMonth
        ?: maxDate?.yearMonth
        ?: fallbackDate.yearMonth
}

internal fun DatePickerConstraints.resolveInitialRangeSelection(
    preferredStartDate: PersianDate?,
    preferredEndDate: PersianDate?,
): ResolvedInitialRangeSelection {
    val resolvedStart = preferredStartDate?.let(::nearestValidOrNull)
    val resolvedEnd = preferredEndDate?.let(::nearestValidOrNull)

    if (resolvedStart != null && resolvedEnd != null) {
        val ordered = PersianDateRange.ordered(resolvedStart, resolvedEnd)
        return if (isRangeSelectable(ordered.start, ordered.endInclusive)) {
            ResolvedInitialRangeSelection(ordered.start, ordered.endInclusive)
        } else {
            ResolvedInitialRangeSelection(resolvedStart, null)
        }
    }

    return ResolvedInitialRangeSelection(
        startDate = resolvedStart ?: resolvedEnd,
        endDate = null,
    )
}

internal fun DatePickerConstraints.resolveInitialRange(
    preferredStartDate: PersianDate?,
    preferredEndDate: PersianDate?,
): PersianDateRange? {
    val selection = resolveInitialRangeSelection(preferredStartDate, preferredEndDate)
    val start = selection.startDate ?: return null
    val end = selection.endDate ?: return PersianDateRange(start, start)
    return PersianDateRange(start, end)
}
