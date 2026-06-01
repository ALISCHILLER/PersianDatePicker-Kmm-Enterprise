# Persian DatePicker KMM Enterprise Ultra

A production-grade **Persian/Jalali DatePicker** library for **Kotlin Multiplatform** and **Compose Multiplatform**.

The project is designed as a reusable publish-ready library, not only a sample app. It contains a pure `commonMain` Jalali calendar engine, strongly typed date models, base and Pro UI components, high-level field components, single-date and range-date flows, saveable state holders, diagnostics, tests, publishing metadata, CI, and a full multi-platform showcase for:

- Android
- iOS
- Desktop JVM
- Web JS
- Web Wasm

---

## Highlights

- 100% common calendar/date logic.
- Compose Multiplatform UI for Android, iOS, Desktop, JS, and Wasm.
- Base picker and polished Pro picker.
- Ready-to-use production form fields.
- Single date and range selection.
- Persian RTL and international LTR layouts.
- Persian and Latin digit rendering.
- Event markers and event legends.
- Quick actions with duplicate Today de-duplication.
- Saveable state APIs for process recreation and rotation.
- Typed constraints and range validation results.
- Diagnostics API for production preflight checks.
- Maven publishing setup with Dokka artifact and optional signing.
- Common test suite for core calendar logic, state, constraints, diagnostics, and contracts.

---

## Version matrix

| Item | Version |
|---|---:|
| Library version | `2.4.0` |
| API marker | `2.4.0-enterprise-ultra` |
| Kotlin | `2.2.20` |
| Compose Multiplatform | `1.11.0` |
| Android Gradle Plugin | `8.10.1` |
| kotlinx-datetime | `0.8.0` |
| Dokka | `2.2.0` |
| Java target | JVM 17 |
| Library min SDK | 23 |
| Sample min SDK | 26 |
| Android compile SDK | 36 |

---

## Project structure

```text
PersianDatePickerKmmEnterpriseUltra/
├── persian-datepicker/              # Publishable KMM/CMP library
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/kotlin/com/msa/persiandatepicker/
│       │   ├── PersianDatePickerApi.kt
│       │   ├── core/                # Pure Jalali calendar engine and models
│       │   └── ui/                  # Compose UI, state, config, diagnostics
│       └── commonTest/kotlin/       # Common tests for core and UI-independent contracts
├── sampleApp/                       # Android, iOS, Desktop, JS, Wasm showcase
├── iosApp/                          # SwiftUI host for iOS
├── docs/                            # API, testing, publishing, release, web, quality docs
├── .github/workflows/ci.yml
├── README.md
├── CHANGELOG.md
├── ARCHITECTURE.md
├── SECURITY.md
└── LICENSE
```

---

## Installation

### Expected Maven coordinate

```kotlin
dependencies {
    implementation("com.msa.persiandatepicker:persian-datepicker:2.4.0")
}
```

### Local staging publish

```bash
./gradlew :persian-datepicker:publishAllPublicationsToLocalStagingRepository
```

The local Maven repository is generated at:

```text
persian-datepicker/build/staging-deploy
```

---

## Quick start: production field API

For most app forms, start with the high-level field components. They manage the open/close state and show the Pro dialog internally.

```kotlin
@Composable
fun BookingForm() {
    var date by remember { mutableStateOf<PersianDate?>(null) }
    var range by remember { mutableStateOf<PersianDateRange?>(null) }

    val today = remember { PersianDate.today() }
    val config = remember(today) {
        PersianDatePickerEnterprisePresets.bookingWindow(
            today = today,
            daysAhead = 60,
            maxRangeLength = 14,
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PersianDatePickerField(
            selectedDate = date,
            onDateSelected = { date = it },
            label = "Booking date",
            config = config,
            onClear = { date = null },
        )

        PersianDateRangePickerField(
            selectedRange = range,
            onRangeSelected = { range = it },
            label = "Booking range",
            config = config,
            onClear = { range = null },
        )
    }
}
```

---

## Pro dialog API

```kotlin
@Composable
fun SingleDateDialogExample() {
    var open by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<PersianDate?>(PersianDate.today()) }

    Button(onClick = { open = true }) {
        Text("Open picker")
    }

    if (open) {
        PersianDatePickerProDialog(
            onDismissRequest = { open = false },
            onDateSelected = { selected = it },
            initialDate = selected,
            config = PersianDatePickerEnterprisePresets.standardPersian(),
            layoutOptions = DatePickerLayoutOptions(
                panelSize = DatePickerPanelSize.Adaptive,
                density = DatePickerVisualDensity.Comfortable,
                showGregorianHint = true,
            ),
            colors = PersianDatePickerPalettes.royalIndigo(),
        )
    }
}
```

---

## Range picker API

```kotlin
@Composable
fun RangeDialogExample() {
    var open by remember { mutableStateOf(false) }
    var selectedRange by remember { mutableStateOf<PersianDateRange?>(null) }

    val config = DatePickerConfig(
        constraints = DatePickerConstraints(
            minDate = PersianDate(1404, 1, 1),
            maxDate = PersianDate(1404, 12, 29),
            maxRangeLength = 30,
        ),
        selectionPolicy = DatePickerSelectionPolicy(
            unavailableDateStrategy = UnavailableDateStrategy.SnapToNearestAvailable,
            allowSameDayRange = true,
        ),
    )

    if (open) {
        PersianDateRangePickerProDialog(
            onDismissRequest = { open = false },
            onRangeSelected = { selectedRange = it },
            initialStartDate = selectedRange?.start,
            initialEndDate = selectedRange?.endInclusive,
            config = config,
            layoutOptions = DatePickerLayoutOptions(
                panelSize = DatePickerPanelSize.Expanded,
                showDualMonthRangeInExpandedPanel = true,
            ),
        )
    }
}
```

---

## Inline picker API

Use the state holder directly when the picker should live inside a screen, card, bottom sheet, dashboard, or report filter.

```kotlin
@Composable
fun InlinePickerExample() {
    val state = rememberSaveablePersianDatePickerState(
        initialSelectedDate = PersianDate.today(),
    )

    PersianDatePickerPro(
        state = state,
        onConfirm = { confirmed -> /* persist confirmed */ },
        onCancel = { state.clearSelection() },
        config = PersianDatePickerEnterprisePresets.standardPersian(),
    )
}
```

---

## Core domain models

```kotlin
val date = PersianDate(1404, 1, 1)
val gregorian = date.toGregorian()
val nextWeek = date.plusDays(7)
val safeNext = date.tryPlusDays(7)
val yearMonth = date.yearMonth
val range = PersianDateRange.ordered(date, date.plusDays(3))
```

Main types:

- `PersianDate`
- `PersianYearMonth`
- `PersianDateRange`
- `PersianCalendarEngine`
- `PersianDateFormatter`
- `PersianDateParser`

---

## Configuration

`DatePickerConfig` centralizes UI strings, digit mode, week layout, constraints, formatters, event rendering, quick actions, and selection behavior.

```kotlin
val config = DatePickerConfig(
    strings = DatePickerStrings.persian(),
    digitMode = DigitMode.Persian,
    weekConfiguration = WeekConfiguration.persian(),
    constraints = DatePickerConstraints(
        minDate = PersianDate(1404, 1, 1),
        maxDate = PersianDate(1404, 12, 29),
        disabledDates = setOf(PersianDate(1404, 1, 13)),
        maxRangeLength = 30,
        dateValidator = { it.dayOfWeek() != DayOfWeek.FRIDAY },
    ),
    selectionPolicy = DatePickerSelectionPolicy(
        unavailableDateStrategy = UnavailableDateStrategy.SnapToNearestAvailable,
    ),
    quickActions = listOf(
        DatePickerQuickAction.JumpToDate("Nowruz") { PersianDate(1404, 1, 1) },
    ),
)
```

---

## Constraints and typed validation

```kotlin
val constraints = DatePickerConstraints(maxRangeLength = 14)
val result = constraints.validateRange(PersianDate(1404, 1, 1), PersianDate(1404, 1, 20))

when (result) {
    DatePickerRangeValidationResult.Valid -> Unit
    is DatePickerRangeValidationResult.ExceedsMaxRangeLength -> Unit
    is DatePickerRangeValidationResult.ExceedsValidationWindow -> Unit
    is DatePickerRangeValidationResult.UnselectableDate -> Unit
    DatePickerRangeValidationResult.CalendarBoundaryExceeded -> Unit
}
```

---

## Diagnostics API

Use diagnostics in app startup, CI, feature flags, or internal QA screens.

```kotlin
val report = DatePickerConfigValidator.validateProductionReady(config)

if (!report.isReady) {
    report.errors.forEach { error ->
        // log error.code and error.message
    }
}

DatePickerConfigValidator.requireProductionReady(config)
```

Stable diagnostic constants are exposed through `DatePickerDiagnosticCode`:

```kotlin
if (report.hasError(DatePickerDiagnosticCode.NoSelectableDate)) {
    // fail startup or show a configuration error
}
```

---

## Quick actions

`showTodayAction` and explicit `DatePickerQuickAction.Today` are automatically de-duplicated by:

```kotlin
config.resolvedQuickActions()
```

Supported actions:

- `DatePickerQuickAction.Today`
- `DatePickerQuickAction.ClearSelection`
- `DatePickerQuickAction.JumpToDate`

---

## Events and legend

```kotlin
val config = DatePickerConfig(
    eventLegend = listOf(
        CalendarEventLegendItem(Color(0xFF059669), "Holiday"),
    ),
    eventIndicator = { date ->
        if (date.day == 1) CalendarEvent(Color(0xFF059669), "Month start") else null
    },
)
```

---

## Theming

Available helpers:

- `PersianDatePickerDefaults.colors(...)`
- `PersianDatePickerPalettes.royalIndigo()`
- `PersianDatePickerPalettes.zarEmerald()`
- `PersianDatePickerPalettes.fromColorScheme(...)`
- `DatePickerLayoutOptions`
- `DatePickerVisualDensity`
- `DatePickerPanelSize`

---

## Platform commands

```bash
# Library tests and docs
./gradlew :persian-datepicker:allTests
./gradlew :persian-datepicker:dokkaGenerate

# Android
./gradlew :sampleApp:assembleDebug
./gradlew :sampleApp:installDebug

# Desktop
./gradlew :sampleApp:run

# Web
./gradlew :sampleApp:jsBrowserDevelopmentRun
./gradlew :sampleApp:jsBrowserDistribution
./gradlew :sampleApp:wasmJsBrowserDevelopmentRun
./gradlew :sampleApp:wasmJsBrowserDistribution
./gradlew :sampleApp:composeCompatibilityBrowserDistribution

# iOS on macOS
./gradlew :persian-datepicker:compileKotlinIosSimulatorArm64
./gradlew :sampleApp:compileKotlinIosSimulatorArm64

# Publishing smoke test
./gradlew :persian-datepicker:publishAllPublicationsToLocalStagingRepository
```

---

## Testing

Current test suite covers:

- Jalali/Gregorian conversion.
- Leap-year and month length behavior.
- Safe date arithmetic.
- Parsing Persian, Arabic-Indic, and Latin digits.
- Formatter contracts.
- Stable keys.
- Calendar month grid generation.
- Week configuration.
- Constraints and nearest selectable date lookup.
- Typed range validation.
- Initial selection resolution.
- State mutation safety.
- Selection policy behavior.
- Quick action resolution.
- Diagnostics and fail-fast config validation.
- Public field API contract markers.

Run:

```bash
./gradlew :persian-datepicker:allTests
```

See [`docs/TESTING.md`](docs/TESTING.md) for the full strategy and manual smoke checklist.

---

## Publishing

The library module includes:

- Kotlin Multiplatform publications.
- Android release variant publication.
- Sources artifact.
- Dokka HTML JAR artifact.
- POM metadata.
- Optional in-memory PGP signing.
- Local staging repository.

```bash
./gradlew :persian-datepicker:publishAllPublicationsToLocalStagingRepository
```

Release signing can be enabled with:

```properties
releaseSigningRequired=true
signingInMemoryKey=...
signingInMemoryKeyPassword=...
```

---

## Quality checklist

Before public release:

- [ ] Run all common tests.
- [ ] Build Android sample.
- [ ] Build Desktop sample.
- [ ] Build Web JS distribution.
- [ ] Build Web Wasm distribution.
- [ ] Compile iOS simulator targets on macOS.
- [ ] Generate Dokka docs.
- [ ] Publish to local staging.
- [ ] Verify POM metadata and license.
- [ ] Run manual UX smoke tests.
- [ ] Review binary compatibility before freezing a stable public API.

---

## License note

The repository contains a GPL v3 license file. Keep Maven POM metadata, README, and release documentation aligned with the final license decision before public or commercial distribution.
