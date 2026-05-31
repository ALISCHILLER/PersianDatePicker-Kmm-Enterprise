# Enterprise release audit - 2026-05-31

## Scope

This audit reviewed the library as a publish-grade Compose Multiplatform package:

- Gradle/KMP source sets and target matrix
- Android, iOS, Desktop, JS, and Wasm entry points
- public API stability and Kotlin explicit API mode
- Jalali calendar core and date arithmetic safety
- single-date and range picker state machines
- base picker and Pro picker UI behavior
- saveable state
- range validation, constraints, and unavailable-date policies
- docs, CI, publishing metadata, signing, and Dokka output

## Fixes and improvements applied

### API and publishing

- Changed the reusable library module from `explicitApiWarning()` to `explicitApi()`.
- Added explicit `Unit` return types to public composable APIs.
- Added explicit public constructors to state holder classes.
- Added a Dokka HTML JAR artifact to Maven publications.
- Upgraded the project identity to `PersianDatePickerKmmEnterpriseUltra` and version `2.4.0`.

### Core safety

- Replaced experimental `kotlin.time.Clock` usage with `kotlinx.datetime.Clock`.
- Added `PersianCalendarEngine.fromGregorianOrNull(...)`.
- Added safe arithmetic helpers:
  - `PersianDate.tryPlusDays(...)`
  - `PersianDate.tryMinusDays(...)`
- Hardened nearest selectable-date search to avoid crashing near supported calendar boundaries.

### Validation

- Added typed range validation:
  - `DatePickerRangeValidationResult.Valid`
  - `ExceedsMaxRangeLength`
  - `ExceedsValidationWindow`
  - `UnselectableDate`
  - `CalendarBoundaryExceeded`
- Added `DatePickerConstraints.validateRange(...)` overloads.
- Added `PersianDateRangePickerState.validateAndSetRange(...)` so consumers can inspect rejection reasons.

### UI/UX

- Added quick actions to the base range picker flow.
- Added `DatePickerLayoutOptions.showDualMonthRangeInExpandedPanel`.
- Added responsive dual-month rendering for the Pro range picker on expanded layouts.
- Expanded Pro panel sizing for desktop/tablet/web range workflows.
- Updated the sample app to demonstrate the dual range panel option.

### Tests

- Added tests for typed range validation.
- Added tests for state-preserving rejected range updates.
- Added tests for safe date arithmetic at calendar boundaries.

## Remaining build note

The project structure and source code were statically audited in this environment. A full Gradle build requires downloading the configured Gradle distribution and dependencies from external repositories, which was not available inside the sandbox. The CI workflow and local commands in the README are the intended final verification path on a machine with network access.
