# Changelog

## 2.4.0 - Enterprise Ultra

### Added
- High-level `PersianDatePickerField(...)` and `PersianDateRangePickerField(...)` APIs for production forms.
- Stable `DatePickerDiagnosticCode` constants for CI assertions, logging, telemetry, and QA dashboards.
- `DatePickerValidationReport.hasCode(...)`, `hasError(...)`, `hasWarning(...)`, and `hasInfo(...)` helpers.
- Field API section in the sample app showing single-date and range-date form integrations.
- Additional common tests for diagnostic-code stability, field API contract markers, quick-action resolution, range contracts, and nearest-valid constraint behavior.
- Complete README rewrite focused on installation, usage, configuration, diagnostics, testing, publishing, and quality release checks.

### Fixed
- Prevented duplicate Today buttons when `showTodayAction = true` and `DatePickerQuickAction.Today` is also provided manually.
- Added a non-blocking diagnostic for deduped Today actions so CI/preflight reports explain the UI behavior.
- Updated project identity and version metadata to `PersianDatePickerKmmEnterpriseUltra` / `2.4.0`.

## 2.2.0 - Enterprise Pro

- Added `DatePickerValidationReport` for grouped production diagnostics.
- Added `DatePickerConfigurationException` and `DatePickerConfigValidator.requireProductionReady(...)` for fail-fast CI or app-startup validation.
- Added `PersianDatePickerEnterprisePresets.standardEnglish(...)` for LTR/Latin-digit integrations.
- Updated the sample app to display validation report summaries, not only raw diagnostic rows.
- Rewrote `README.md` as a full publish-grade guide with installation, usage, configuration, testing, publishing, migration, and quality sections.
- Rewrote `docs/TESTING.md` with a complete test strategy and verification matrix.
- Added tests for validation reports, fail-fast diagnostics, formatter contracts, week configuration, and quick action contracts.
- Renamed project metadata to `PersianDatePickerKmmEnterprisePro`.

## 2.1.0 - Enterprise Release

- Added `DatePickerConfigValidator` for production preflight diagnostics.
- Added `PersianDatePickerEnterprisePresets` for standard Persian and booking-window configurations.
- Made exported data-class constructors and constructor properties explicit for strict `explicitApi()` usage.
- Added an inline Pro Picker section to the sample app, showing that the library works beyond dialogs.
- Added diagnostic tests and an enterprise audit report.

## 2.0.0 - Elite Release

- Switched the library module to strict Kotlin `explicitApi()` mode.
- Added typed range validation result APIs.
- Added safe date arithmetic helpers for supported-calendar boundaries.
- Added Pro range dual-month expanded layout for tablet/desktop/web scenarios.
- Added base range quick actions.
- Added Dokka HTML JAR artifact wiring for Maven publications.
- Added extra tests for range validation and safe arithmetic.

## 1.5.0 - Release Pro

- Added Dokka plugin wiring for publish-quality API documentation generation.
- Added optional in-memory PGP signing configuration guarded by `releaseSigningRequired`.
- Hardened Persian calendar boundaries with `supportedYearRange` and safe `isValidDate(...)` behavior.
- Added `DatePickerConstraints.maxRangeValidationDays` to prevent unbounded range scans in custom validators.
- Added `DatePickerConstraints.isRangeSelectable(PersianDateRange)` overload for cleaner integration code.
- Added `trySelect(...)` and `trySetRange(...)` state APIs that return validation success instead of silently ignoring invalid input.
- Disabled Pro range mode tabs/chips instead of rendering clickable no-op controls.
- Added boundary, state mutation, unsupported year range, and validation-limit tests.

## 1.4.0 - Publish Ready

- Added Maven publication metadata and local staging repository support for the reusable `persian-datepicker` module.
- Aligned Maven POM license metadata with the repository GPL v3 license.
- Made picker state constructors public for custom state holders, tests, and advanced integrations.
- Added API stability, publishing, and license policy documentation.
- Added CI split for Ubuntu Android/Desktop/Web checks and macOS iOS simulator compilation.
- Added `DatePickerConfig` guard for empty `yearRange`.
- Added `DatePickerConstraints.hasSelectableDate(...)` and tests for closed/open constraint sets.
- Wired `quickActions` into the Pro footer so the premium UI honors the same configuration as the base picker.
- Renamed the project to `PersianDatePickerKmmPublishReady`.

## 1.2.0 - All Platforms Reviewed

- Hardened Web entry points with explicit `ComposeViewport(viewportContainerId = "webApp")` wiring.
- Added full-screen JS/Wasm HTML host containers and viewport CSS.
- Centralized constrained initial date/range resolution for base and Pro dialogs.
- Preserved completed one-day initial ranges while keeping start-only ranges pending.
- Added common tests for constrained initial state and same-day range initialization.
- Updated Web, testing, and final audit documentation.

## 1.1.0 - Production

- Hardened project as a production-grade KMM / Compose Multiplatform library.
- Added `DatePickerConfig.selectionPolicy` and wired it into base and Pro picker state transitions.
- Added policy-aware `applyTap(...)` APIs for single and range picker states.
- Added `CalendarEventLegendItem` and `DatePickerConfig.eventLegend` for visible event legends.
- Improved unavailable-date behavior with `Ignore` and `SnapToNearestAvailable` strategies.
- Switched dialog state creation to saveable state where appropriate.
- Fixed Pro UI missing layout import.
- Corrected `LocalDate.fromEpochDays(...)` usage for common `kotlinx-datetime` compatibility.
- Expanded tests for policy-aware state behavior.
- Added final quality report and editor configuration.

## 1.1.0 - All Platforms

- Added Web support through both `js` and `wasmJs` browser targets.
- Added ComposeViewport entry points for Kotlin/JS and Kotlin/Wasm.
- Added browser `index.html` resources for web distributions.
- Updated CI and docs for Android, iOS, Desktop, and Web verification.

## 1.0.0

- Initial KMM / Compose Multiplatform rewrite.
- Added pure commonMain Persian calendar engine.
- Added base and Pro single/range picker UI.
- Added Android, Desktop, iOS, Kotlin/JS browser, and Kotlin/Wasm browser sample entry points.
