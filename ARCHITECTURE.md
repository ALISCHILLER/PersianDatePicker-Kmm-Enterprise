# Architecture

## Goals

The rewritten library is designed as a professional Kotlin Multiplatform / Compose Multiplatform date picker rather than an Android-only sample.

Primary goals:

1. Keep Jalali calendar logic pure and testable.
2. Keep UI fully shared across Android, iOS, Desktop, and Web.
3. Expose a stable, typed API for application teams.
4. Support product-level customization without forking the library.
5. Preserve a migration path from the original `SoleimaniDate` API.
6. Keep platform entry points thin and move behavior into `commonMain`.

## Modules

### `persian-datepicker`

Reusable KMM/CMP library.

Targets:

- Android
- iOS: `iosX64`, `iosArm64`, `iosSimulatorArm64`
- Desktop JVM
- Web JS: `js` browser
- Web Wasm: `wasmJs` browser

Internal layers:

- `core`: domain model, conversion engine, parsing, formatting, date arithmetic.
- `ui`: state holders, configuration, constraints, base picker, Pro picker, colors, design tokens.

### `sampleApp`

Showcase app for Android, iOS, Desktop, Kotlin/JS browser, and Kotlin/Wasm browser.

It demonstrates:

- single date selection
- range selection
- Persian and Latin digits
- Persian and international week layouts
- validation constraints
- event indicators and event legend
- compact/expanded visual density
- dark theme
- shared UI running from dedicated platform entry points

## Platform entry points

```text
sampleApp/src/androidMain/.../MainActivity.kt       -> Android Activity
sampleApp/src/iosMain/.../MainViewController.kt    -> ComposeUIViewController
sampleApp/src/desktopMain/.../Main.kt              -> Desktop Window
sampleApp/src/jsMain/.../Main.kt                   -> ComposeViewport for Kotlin/JS
sampleApp/src/wasmJsMain/.../Main.kt               -> ComposeViewport for Kotlin/Wasm
```

All five entry points call the same `SampleApp()` composable from `commonMain`.

## Layering

```text
sampleApp platform entry points
   ↓
sampleApp.commonMain.SampleApp
   ↓
persian-datepicker.ui
   ↓
persian-datepicker.core
   ↓
kotlinx.datetime
```

Rules:

- `core` must not depend on Compose, Android, resources, or platform APIs.
- `ui` may depend on Compose Multiplatform and `core`.
- `sampleApp` may depend on the library but the library must never depend on the sample.
- Platform source sets should only launch shared UI or connect native platform services.

## State model

The picker uses explicit state holders:

- `PersianDatePickerState`
- `PersianDateRangePickerState`

Saveable variants are available:

- `rememberSaveablePersianDatePickerState`
- `rememberSaveablePersianDateRangePickerState`

This makes the picker predictable, testable, and easy to integrate with MVVM/MVI screens.

## Validation model

`DatePickerConstraints` is the primary validation boundary:

- min date
- max date
- disabled dates
- max range length
- custom validator

The UI never decides business rules directly; it asks constraints/policies whether a date can be selected.

## UI model

The library exposes two visual layers:

### Base picker

Smaller, minimal, easier to embed.

### Pro picker

Product-ready UX:

- premium header
- adaptive panel width
- animated selection states
- day/month/year modes
- range progress summary
- configurable density
- event markers and event legend
- stronger accessibility semantics
- web-capable shared Compose UI

## Migration compatibility

`typealias SoleimaniDate = PersianDate` keeps the original naming path available while letting new code use a clearer domain model.

## Final hardening pass

The final production pass tightened the architecture around a single rule: UI components should render state, while selection rules live in policy/state code.

- `DatePickerConfig.selectionPolicy` declares business behavior.
- `DatePickerSelectionPolicy` resolves requested taps into accepted dates/ranges.
- `PersianDatePickerState.applyTap(...)` and `PersianDateRangePickerState.applyTap(...)` are the canonical mutation points for interactive selection.
- Base and Pro UI components call `applyTap(...)` instead of duplicating selection logic.
- Pro UI exposes `eventLegend` as a first-class visual system instead of leaving marker meanings implicit.
- Web JS/Wasm entry points use `ComposeViewport` and reuse the exact same `SampleApp()` UI.

This keeps the date engine, validation layer, state mutation layer, and visual layer independently testable.
