package com.msa.persiandatepicker.ui

/**
 * Stable diagnostic codes emitted by [DatePickerConfigValidator].
 *
 * Keep these constants stable across minor versions so applications can assert diagnostics
 * in CI, telemetry, and production preflight checks without depending on localized messages.
 */
public object DatePickerDiagnosticCode {
    public const val NoSelectableDate: String = "NO_SELECTABLE_DATE"
    public const val MinDateOutsideYearRange: String = "MIN_DATE_OUTSIDE_YEAR_RANGE"
    public const val MaxDateOutsideYearRange: String = "MAX_DATE_OUTSIDE_YEAR_RANGE"
    public const val RangeLimitExceedsValidationWindow: String = "RANGE_LIMIT_EXCEEDS_VALIDATION_WINDOW"
    public const val YearRangeOutsideEngineSupport: String = "YEAR_RANGE_OUTSIDE_ENGINE_SUPPORT"
    public const val DuplicateTodayActionDeduped: String = "DUPLICATE_TODAY_ACTION_DEDUPED"
    public const val ClearOnlyQuickActions: String = "CLEAR_ONLY_QUICK_ACTIONS"
    public const val ConfigOk: String = "CONFIG_OK"
}
