# Release Pro audit - 2026-05-31

This audit prepares the project for publish-grade use as a Kotlin Multiplatform / Compose Multiplatform library.

## Scope

Reviewed areas:

- Gradle and version catalog configuration
- Kotlin Multiplatform source sets for Android, iOS, Desktop JVM, Kotlin/JS, and Kotlin/Wasm
- Public core API: `PersianDate`, `PersianYearMonth`, `PersianDateRange`, parser, formatter, and calendar engine
- Picker state APIs and saveable state helpers
- Base and Pro Compose UI components
- Selection policies, constraints, range validation, quick actions, and event indicators
- Sample app entry points for Android, iOS, Desktop, JS, and Wasm
- CI, publishing metadata, licensing, and release documentation

## Fixes applied

1. Added Dokka plugin wiring for API documentation generation.
2. Added optional signing configuration using in-memory PGP keys through Gradle properties.
3. Added explicit `PersianCalendarEngine.supportedYearRange` and safe `isValidDate(...)` behavior for unsupported years.
4. Added `DatePickerConstraints.maxRangeValidationDays` to avoid expensive unbounded range scans with custom validators.
5. Added `DatePickerConstraints.isRangeSelectable(PersianDateRange)` overload.
6. Added state mutation APIs that return success/failure: `trySelect(...)` and `trySetRange(...)`.
7. Disabled Pro range mode switching controls instead of showing clickable no-op tabs/chips.
8. Added tests for unsupported calendar years, state mutation, unsupported config ranges, and range scan limits.
9. Updated public API marker to `2.4.0-enterprise-ultra`.
10. Updated project identity to `PersianDatePickerKmmEnterpriseUltra`.

## Release gate

Run this before publishing:

```bash
./gradlew clean \
  :persian-datepicker:allTests \
  :persian-datepicker:dokkaGenerate \
  :sampleApp:assembleDebug \
  :sampleApp:wasmJsBrowserDistribution \
  :sampleApp:jsBrowserDistribution \
  :sampleApp:composeCompatibilityBrowserDistribution
```

On macOS, also run:

```bash
./gradlew \
  :persian-datepicker:compileKotlinIosSimulatorArm64 \
  :sampleApp:compileKotlinIosSimulatorArm64
```

## Known environment limitation

The artifact was statically audited in this environment. Full Gradle execution was blocked because the Gradle Wrapper attempts to download `gradle-8.11.1-all.zip` from `services.gradle.org`, which is not reachable from the sandbox.
