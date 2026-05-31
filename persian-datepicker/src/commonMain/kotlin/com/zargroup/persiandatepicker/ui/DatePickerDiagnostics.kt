package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianCalendarEngine
import com.zargroup.persiandatepicker.core.PersianDate

/** Severity level for non-throwing DatePicker configuration diagnostics. */
public enum class DatePickerDiagnosticSeverity {
    Info,
    Warning,
    Error,
}

/** A stable, machine-readable diagnostic that can be surfaced in CI, logs, or sample apps. */
public data class DatePickerDiagnostic public constructor(
    public val severity: DatePickerDiagnosticSeverity,
    public val code: String,
    public val message: String,
)

/** Aggregated production-readiness result for a [DatePickerConfig]. */
public data class DatePickerValidationReport public constructor(
    public val diagnostics: List<DatePickerDiagnostic>,
) {
    public val errors: List<DatePickerDiagnostic> = diagnostics.filter { it.severity == DatePickerDiagnosticSeverity.Error }

    public val warnings: List<DatePickerDiagnostic> = diagnostics.filter { it.severity == DatePickerDiagnosticSeverity.Warning }

    public val infos: List<DatePickerDiagnostic> = diagnostics.filter { it.severity == DatePickerDiagnosticSeverity.Info }

    public val isReady: Boolean = errors.isEmpty()

    public val summary: String = buildString {
        append("errors=")
        append(errors.size)
        append(", warnings=")
        append(warnings.size)
        append(", infos=")
        append(infos.size)
    }

    public fun hasCode(code: String): Boolean = diagnostics.any { it.code == code }

    public fun hasError(code: String): Boolean = errors.any { it.code == code }

    public fun hasWarning(code: String): Boolean = warnings.any { it.code == code }

    public fun hasInfo(code: String): Boolean = infos.any { it.code == code }

    public fun throwIfBlocking(): Unit {
        if (!isReady) {
            throw DatePickerConfigurationException(errors)
        }
    }
}

/** Exception thrown when an integration explicitly opts into fail-fast production validation. */
public class DatePickerConfigurationException public constructor(
    public val errors: List<DatePickerDiagnostic>,
) : IllegalStateException(errors.joinToString(separator = "\n") { "${it.code}: ${it.message}" })

/**
 * Lightweight preflight validator for production integrations.
 *
 * Constructors still fail fast for impossible values. This validator is designed for softer
 * integration checks such as an empty selectable window, a year range that does not match the
 * configured constraints, or range settings that can surprise users at runtime.
 */
public object DatePickerConfigValidator {
    public fun validate(
        config: DatePickerConfig,
        selectableSearchRadiusDays: Int = 3660,
    ): List<DatePickerDiagnostic> = validateProductionReady(
        config = config,
        selectableSearchRadiusDays = selectableSearchRadiusDays,
    ).diagnostics

    public fun validateProductionReady(
        config: DatePickerConfig,
        selectableSearchRadiusDays: Int = 3660,
    ): DatePickerValidationReport {
        val diagnostics = mutableListOf<DatePickerDiagnostic>()
        val constraints = config.constraints

        if (!constraints.hasSelectableDate(selectableSearchRadiusDays)) {
            diagnostics += DatePickerDiagnostic(
                severity = DatePickerDiagnosticSeverity.Error,
                code = DatePickerDiagnosticCode.NoSelectableDate,
                message = "No selectable Persian date could be found within the configured constraint window.",
            )
        }

        val minYear = constraints.minDate?.year
        val maxYear = constraints.maxDate?.year
        if (minYear != null && minYear !in config.yearRange) {
            diagnostics += DatePickerDiagnostic(
                severity = DatePickerDiagnosticSeverity.Warning,
                code = DatePickerDiagnosticCode.MinDateOutsideYearRange,
                message = "minDate.year=$minYear is outside yearRange=${config.yearRange}.",
            )
        }
        if (maxYear != null && maxYear !in config.yearRange) {
            diagnostics += DatePickerDiagnostic(
                severity = DatePickerDiagnosticSeverity.Warning,
                code = DatePickerDiagnosticCode.MaxDateOutsideYearRange,
                message = "maxDate.year=$maxYear is outside yearRange=${config.yearRange}.",
            )
        }
        if (constraints.maxRangeLength != null && constraints.maxRangeLength > constraints.maxRangeValidationDays) {
            diagnostics += DatePickerDiagnostic(
                severity = DatePickerDiagnosticSeverity.Warning,
                code = DatePickerDiagnosticCode.RangeLimitExceedsValidationWindow,
                message = "maxRangeLength=${constraints.maxRangeLength} is greater than maxRangeValidationDays=${constraints.maxRangeValidationDays}.",
            )
        }
        if (config.yearRange.first < PersianCalendarEngine.supportedYearRange.first ||
            config.yearRange.last > PersianCalendarEngine.supportedYearRange.last
        ) {
            diagnostics += DatePickerDiagnostic(
                severity = DatePickerDiagnosticSeverity.Error,
                code = DatePickerDiagnosticCode.YearRangeOutsideEngineSupport,
                message = "yearRange=${config.yearRange} is outside PersianCalendarEngine.supportedYearRange=${PersianCalendarEngine.supportedYearRange}.",
            )
        }
        if (config.showTodayAction && config.quickActions.any { it is DatePickerQuickAction.Today }) {
            diagnostics += DatePickerDiagnostic(
                severity = DatePickerDiagnosticSeverity.Info,
                code = DatePickerDiagnosticCode.DuplicateTodayActionDeduped,
                message = "DatePickerQuickAction.Today is already enabled by showTodayAction and will be rendered only once.",
            )
        }

        if (config.quickActions.any { it is DatePickerQuickAction.ClearSelection } && !config.showTodayAction && config.quickActions.size == 1) {
            diagnostics += DatePickerDiagnostic(
                severity = DatePickerDiagnosticSeverity.Info,
                code = DatePickerDiagnosticCode.ClearOnlyQuickActions,
                message = "Only ClearSelection is configured as a quick action. This is valid, but most UX flows also provide Today or a JumpToDate action.",
            )
        }

        if (diagnostics.isEmpty()) {
            diagnostics += DatePickerDiagnostic(
                severity = DatePickerDiagnosticSeverity.Info,
                code = DatePickerDiagnosticCode.ConfigOk,
                message = "DatePickerConfig passed production preflight checks.",
            )
        }
        return DatePickerValidationReport(diagnostics)
    }

    public fun hasErrors(config: DatePickerConfig): Boolean = validateProductionReady(config).errors.isNotEmpty()

    public fun requireProductionReady(config: DatePickerConfig): Unit = validateProductionReady(config).throwIfBlocking()
}

/** Opinionated production presets for common app scenarios. */
public object PersianDatePickerEnterprisePresets {
    public fun standardPersian(
        minDate: PersianDate = PersianDate(1300, 1, 1),
        maxDate: PersianDate = PersianDate(1500, 12, 29),
        quickActions: List<DatePickerQuickAction> = listOf(DatePickerQuickAction.Today),
    ): DatePickerConfig = DatePickerConfig(
        strings = DatePickerStrings.persian(),
        constraints = DatePickerConstraints(minDate = minDate, maxDate = maxDate),
        yearRange = minDate.year..maxDate.year,
        monthFormatter = MonthFormatter.Persian,
        yearFormatter = YearFormatter.WithGregorianHint,
        quickActions = quickActions,
    )

    public fun standardEnglish(
        minDate: PersianDate = PersianDate(1300, 1, 1),
        maxDate: PersianDate = PersianDate(1500, 12, 29),
        quickActions: List<DatePickerQuickAction> = listOf(DatePickerQuickAction.Today),
    ): DatePickerConfig = DatePickerConfig(
        strings = DatePickerStrings.english(),
        digitMode = com.zargroup.persiandatepicker.core.DigitMode.Latin,
        weekConfiguration = WeekConfiguration.international(),
        constraints = DatePickerConstraints(minDate = minDate, maxDate = maxDate),
        yearRange = minDate.year..maxDate.year,
        monthFormatter = MonthFormatter.PersianWithLatinTransliteration,
        yearFormatter = YearFormatter.WithGregorianHint,
        quickActions = quickActions,
    )

    public fun bookingWindow(
        today: PersianDate = PersianDate.today(),
        daysAhead: Int = 90,
        maxRangeLength: Int = 30,
        blockPastDates: Boolean = true,
        dateValidator: (PersianDate) -> Boolean = { true },
    ): DatePickerConfig {
        require(daysAhead > 0) { "daysAhead must be greater than zero." }
        require(maxRangeLength > 0) { "maxRangeLength must be greater than zero." }
        val minDate = if (blockPastDates) today else today.tryMinusDays(daysAhead) ?: today
        val maxDate = today.tryPlusDays(daysAhead) ?: today
        return DatePickerConfig(
            constraints = DatePickerConstraints(
                minDate = minDate,
                maxDate = maxDate,
                maxRangeLength = maxRangeLength,
                dateValidator = dateValidator,
            ),
            yearRange = minDate.year..maxDate.year,
            selectionPolicy = DatePickerSelectionPolicy(
                unavailableDateStrategy = UnavailableDateStrategy.SnapToNearestAvailable,
                allowSameDayRange = true,
            ),
            quickActions = listOf(DatePickerQuickAction.Today),
        )
    }
}
