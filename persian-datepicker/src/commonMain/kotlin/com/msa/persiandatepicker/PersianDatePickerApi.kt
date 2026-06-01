@file:Suppress("unused")

package com.msa.persiandatepicker

/**
 * Public package marker for Persian DatePicker KMM.
 *
 * Primary APIs live under:
 * - com.msa.persiandatepicker.core for pure Jalali calendar/date logic
 * - com.msa.persiandatepicker.ui for Compose Multiplatform UI components
 */
public object PersianDatePickerKmm {
    public const val libraryName: String = "Persian DatePicker KMM"
    public const val apiVersion: String = "2.4.0-enterprise-ultra"
}

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This API is intended for advanced integrations and may evolve across minor versions.",
)
public annotation class ExperimentalPersianDatePickerApi
