package com.msa.persiandatepicker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

@Immutable
public data class PersianDatePickerColors public constructor(
    public val containerColor: Color,
    public val headerContentColor: Color,
    public val dayContainerColor: Color,
    public val dayContentColor: Color,
    public val selectedDayContainerColor: Color,
    public val selectedDayContentColor: Color,
    public val rangeContainerColor: Color,
    public val todayBorderColor: Color,
    public val disabledContentColor: Color,
    public val adjacentMonthContentColor: Color,
    public val weekendContentColor: Color,
)

public object PersianDatePickerDefaults {
    @Composable
    public fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        headerContentColor: Color = MaterialTheme.colorScheme.primary,
        dayContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        dayContentColor: Color = MaterialTheme.colorScheme.onSurface,
        selectedDayContainerColor: Color = MaterialTheme.colorScheme.primary,
        selectedDayContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        rangeContainerColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.68f),
        todayBorderColor: Color = MaterialTheme.colorScheme.primary,
        disabledContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.34f),
        adjacentMonthContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f),
        weekendContentColor: Color = MaterialTheme.colorScheme.error,
    ): PersianDatePickerColors = PersianDatePickerColors(
        containerColor = containerColor,
        headerContentColor = headerContentColor,
        dayContainerColor = dayContainerColor,
        dayContentColor = dayContentColor,
        selectedDayContainerColor = selectedDayContainerColor,
        selectedDayContentColor = selectedDayContentColor,
        rangeContainerColor = rangeContainerColor,
        todayBorderColor = todayBorderColor,
        disabledContentColor = disabledContentColor,
        adjacentMonthContentColor = adjacentMonthContentColor,
        weekendContentColor = weekendContentColor,
    )
}
