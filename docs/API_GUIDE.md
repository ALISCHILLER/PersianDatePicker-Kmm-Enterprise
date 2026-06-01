# API Guide

## Packages

- `com.msa.persiandatepicker.core`: pure Jalali date logic.
- `com.msa.persiandatepicker.ui`: Compose Multiplatform UI, state, config, and design system.

## Domain model

`PersianDate` is the main value object. It validates year/month/day immediately and supports comparison, arithmetic, conversion to/from Gregorian dates, parsing, and formatting.

```kotlin
val date = PersianDate(1404, 1, 1)
val next = date.plusMonths(1)
val asGregorian = date.toGregorian()
```

`PersianDateRange` is inclusive:

```kotlin
val range = PersianDateRange(PersianDate(1404, 1, 1), PersianDate(1404, 1, 7))
check(range.lengthInDays == 7)
```

## State

For regular Compose state:

```kotlin
val state = rememberPersianDatePickerState()
```

For configuration changes and process recreation:

```kotlin
val state = rememberSaveablePersianDatePickerState()
```

Range state:

```kotlin
val state = rememberSaveablePersianDateRangePickerState()
```

## Config

`DatePickerConfig` is the main entry point for UX behavior:

- strings
- digit mode
- week configuration
- constraints
- formatters
- year range
- adjacent month visibility
- quick actions
- event indicators

## Pro UI

Use `PersianDatePickerProDialog` and `PersianDateRangePickerProDialog` for product-ready screens. Use base `PersianDatePickerDialog` when you need a smaller, simpler dialog.

## Production selection policy

`DatePickerConfig.selectionPolicy` controls how user taps are interpreted before state changes happen.

```kotlin
val config = DatePickerConfig(
    selectionPolicy = DatePickerSelectionPolicy(
        unavailableDateStrategy = UnavailableDateStrategy.SnapToNearestAvailable,
        allowSameDayRange = true,
    ),
)
```

Supported unavailable-date behavior:

- `Ignore`: tapping a blocked/out-of-range date does nothing.
- `SnapToNearestAvailable`: tapping a blocked/out-of-range date selects the nearest selectable date when one is available.

The policy is applied by both base and Pro pickers through the state `applyTap(...)` methods.

## Event legend

Use `eventIndicator` to mark individual dates and `eventLegend` to explain markers in the Pro UI.

```kotlin
DatePickerConfig(
    eventLegend = listOf(
        CalendarEventLegendItem(Color(0xFF059669), "امروز"),
        CalendarEventLegendItem(Color(0xFF7C3AED), "شروع ماه"),
    ),
    eventIndicator = { date ->
        when {
            date == PersianDate.today() -> CalendarEvent(Color(0xFF059669), "امروز")
            date.day == 1 -> CalendarEvent(Color(0xFF7C3AED), "شروع ماه")
            else -> null
        }
    },
)
```

## Enterprise validation APIs

For production integrations, prefer typed validation when you need a reason for rejection:

```kotlin
val result = constraints.validateRange(start, end)
if (!result.isValid) {
    // map the typed result to your design-system error message
}
```

`PersianDateRangePickerState.validateAndSetRange(...)` updates state only when validation succeeds and returns a `DatePickerRangeValidationResult` either way.

## Safe date arithmetic

Use throwing arithmetic when invalid dates should fail fast:

```kotlin
val tomorrow = date.plusDays(1)
```

Use safe arithmetic for boundary-sensitive flows:

```kotlin
val maybeTomorrow = date.tryPlusDays(1)
```

## Pro range layout

`DatePickerLayoutOptions.showDualMonthRangeInExpandedPanel` enables a two-month range view when the picker has enough space. It is useful on desktop, tablets, landscape layouts, and web.


## Enterprise diagnostics

Use `DatePickerConfigValidator.validate(config)` before wiring a configuration into production screens. It returns stable diagnostic codes such as `CONFIG_OK`, `NO_SELECTABLE_DATE`, `MIN_DATE_OUTSIDE_YEAR_RANGE`, and `RANGE_LIMIT_EXCEEDS_VALIDATION_WINDOW`.

`PersianDatePickerEnterprisePresets` provides production-friendly starting points for standard Persian pickers and booking windows.

## Enterprise validation report

For production apps, prefer the report API over a raw diagnostic list:

```kotlin
val report = DatePickerConfigValidator.validateProductionReady(config)

if (report.isReady) {
    // Safe to render or ship this configuration.
} else {
    report.errors.forEach { error ->
        // Log, show in QA panel, or fail a CI smoke check.
    }
}
```

Fail fast when config errors must stop startup or CI:

```kotlin
DatePickerConfigValidator.requireProductionReady(config)
```

The report exposes `diagnostics`, `errors`, `warnings`, `infos`, `isReady`, and `summary`.

## Enterprise presets

```kotlin
val persian = PersianDatePickerEnterprisePresets.standardPersian()
val english = PersianDatePickerEnterprisePresets.standardEnglish()
val booking = PersianDatePickerEnterprisePresets.bookingWindow(
    daysAhead = 90,
    maxRangeLength = 30,
)
```
