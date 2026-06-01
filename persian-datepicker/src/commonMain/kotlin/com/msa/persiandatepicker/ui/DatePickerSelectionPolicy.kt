package com.msa.persiandatepicker.ui

import androidx.compose.runtime.Immutable
import com.msa.persiandatepicker.core.PersianDate
import com.msa.persiandatepicker.core.PersianDateRange

/** Determines how the picker behaves when a user taps an unavailable date. */
public enum class UnavailableDateStrategy {
    Ignore,
    SnapToNearestAvailable,
}

/** Declarative policy that keeps validation decisions outside the UI rendering code. */
@Immutable
public data class DatePickerSelectionPolicy public constructor(
    public val unavailableDateStrategy: UnavailableDateStrategy = UnavailableDateStrategy.Ignore,
    public val allowSameDayRange: Boolean = true,
    public val autoCloseAfterSingleSelection: Boolean = false,
) {
    public fun isTapEnabled(
        requestedDate: PersianDate,
        constraints: DatePickerConstraints,
    ): Boolean = constraints.isDateSelectable(requestedDate) ||
        unavailableDateStrategy == UnavailableDateStrategy.SnapToNearestAvailable

    public fun resolveSingleTap(
        requestedDate: PersianDate,
        constraints: DatePickerConstraints,
    ): PersianDate? {
        if (constraints.isDateSelectable(requestedDate)) return requestedDate
        return when (unavailableDateStrategy) {
            UnavailableDateStrategy.Ignore -> null
            UnavailableDateStrategy.SnapToNearestAvailable -> constraints.nearestValidOrNull(requestedDate)
        }
    }

    public fun resolveRangeTap(
        currentStart: PersianDate?,
        currentEnd: PersianDate?,
        requestedDate: PersianDate,
        constraints: DatePickerConstraints,
    ): Pair<PersianDate?, PersianDate?> {
        val resolved = resolveSingleTap(requestedDate, constraints) ?: return currentStart to currentEnd
        if (currentStart == null || currentEnd != null) return resolved to null
        if (!allowSameDayRange && resolved == currentStart) return resolved to null
        val candidate = PersianDateRange.ordered(currentStart, resolved)
        return if (constraints.isRangeSelectable(candidate.start, candidate.endInclusive)) {
            candidate.start to candidate.endInclusive
        } else {
            resolved to null
        }
    }
}

public enum class DatePickerVisualDensity {
    Comfortable,
    Compact,
    Spacious,
}

public enum class DatePickerPanelSize {
    Adaptive,
    Compact,
    Expanded,
}

@Immutable
public data class DatePickerLayoutOptions public constructor(
    public val panelSize: DatePickerPanelSize = DatePickerPanelSize.Adaptive,
    public val density: DatePickerVisualDensity = DatePickerVisualDensity.Comfortable,
    public val showSelectedSummary: Boolean = true,
    public val showGregorianHint: Boolean = true,
    public val showConstraintHint: Boolean = true,
    public val showEventLegend: Boolean = true,
    public val showDualMonthRangeInExpandedPanel: Boolean = true,
)
