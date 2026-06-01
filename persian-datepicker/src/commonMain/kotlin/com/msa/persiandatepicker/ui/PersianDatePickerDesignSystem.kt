package com.msa.persiandatepicker.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
public data class PersianDatePickerShapeTokens public constructor(
    public val dialog: Dp = 30.dp,
    public val tile: Dp = 16.dp,
    public val chip: Dp = 14.dp,
    public val control: Dp = 18.dp,
)

@Immutable
public data class PersianDatePickerSpacingTokens public constructor(
    public val xSmall: Dp = 4.dp,
    public val small: Dp = 8.dp,
    public val medium: Dp = 12.dp,
    public val large: Dp = 16.dp,
    public val xLarge: Dp = 24.dp,
)

@Immutable
public data class PersianDatePickerMotionTokens public constructor(
    public val fast: Int = 140,
    public val normal: Int = 220,
    public val slow: Int = 320,
)

@Immutable
public data class PersianDatePickerDesignTokens public constructor(
    public val shapes: PersianDatePickerShapeTokens = PersianDatePickerShapeTokens(),
    public val spacing: PersianDatePickerSpacingTokens = PersianDatePickerSpacingTokens(),
    public val motion: PersianDatePickerMotionTokens = PersianDatePickerMotionTokens(),
)

public val LocalPersianDatePickerDesignTokens: ProvidableCompositionLocal<PersianDatePickerDesignTokens> =
    staticCompositionLocalOf { PersianDatePickerDesignTokens() }

public object PersianDatePickerPalettes {
    @Composable
    public fun zarEmerald(): PersianDatePickerColors = PersianDatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        headerContentColor = Color(0xFF047857),
        selectedDayContainerColor = Color(0xFF047857),
        selectedDayContentColor = Color.White,
        rangeContainerColor = Color(0xFFD1FAE5),
        todayBorderColor = Color(0xFF059669),
        weekendContentColor = Color(0xFFDC2626),
    )

    @Composable
    public fun royalIndigo(): PersianDatePickerColors = PersianDatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        headerContentColor = Color(0xFF4F46E5),
        selectedDayContainerColor = Color(0xFF4F46E5),
        selectedDayContentColor = Color.White,
        rangeContainerColor = Color(0xFFE0E7FF),
        todayBorderColor = Color(0xFF6366F1),
        weekendContentColor = Color(0xFFBE123C),
    )

    @Composable
    public fun fromColorScheme(colorScheme: ColorScheme): PersianDatePickerColors = PersianDatePickerDefaults.colors(
        containerColor = colorScheme.surface,
        headerContentColor = colorScheme.primary,
        selectedDayContainerColor = colorScheme.primary,
        selectedDayContentColor = colorScheme.onPrimary,
        rangeContainerColor = colorScheme.primaryContainer.copy(alpha = 0.64f),
        todayBorderColor = colorScheme.primary,
        weekendContentColor = colorScheme.error,
    )
}
