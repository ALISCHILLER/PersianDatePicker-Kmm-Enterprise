# Enterprise implementation report - 2026-05-31

## Goal

Bring the project closer to a publishable, reusable Compose Multiplatform library rather than a visual demo.

## Applied changes

- Kept the target matrix: Android, iOS, Desktop JVM, Kotlin/JS, Kotlin/Wasm.
- Preserved strict Kotlin `explicitApi()` and made exported data models more explicit by declaring public constructors and public constructor properties.
- Added `DatePickerConfigValidator` for production preflight diagnostics.
- Added `DatePickerDiagnostic` and `DatePickerDiagnosticSeverity` with stable, machine-readable codes.
- Added `PersianDatePickerEnterprisePresets` for common production setups.
- Added tests for diagnostics and enterprise presets.
- Added an inline Pro Picker section to the sample app so the sample demonstrates dialog and embedded usage.
- Updated README, CHANGELOG, and API guide.

## Why this matters

A publishable UI library needs more than beautiful components. Consumers need stable public APIs, safe defaults, validation hooks, diagnostics, test coverage, and examples that show real integration patterns. This version adds those missing integration-grade pieces while keeping the original calendar engine and multi-platform UI structure intact.

## Verification performed in this environment

- Static source audit across Gradle/Kotlin/docs.
- Source set and entry point review for Android/iOS/Desktop/JS/Wasm.
- `TODO`, `FIXME`, `!!`, and raw debug-output scan.
- Project-name/version consistency scan.
- Zip integrity check after packaging.

Full Gradle execution still requires network access to download Gradle/dependencies, which is not available in this sandbox.
