# Publish-ready audit – 2026-05-31

This audit focused on making the project suitable as a real reusable Compose Multiplatform library rather than only a showcase.

## Scope reviewed

- Root Gradle configuration and version catalog.
- `persian-datepicker` library Gradle target setup.
- Android, iOS, Desktop JVM, Kotlin/JS, and Kotlin/Wasm source sets.
- Core date engine and Jalali domain models.
- Base picker and Pro picker UI configuration.
- Selection policy, constraints, saveable state, and initial state resolution.
- Tests, docs, CI, release checklist, and license/publishing metadata.

## Important fixes in this pass

1. Added `maven-publish` to the library module and configured local staging publication.
2. Aligned publication POM license metadata with the repository GPL v3 license file.
3. Made `PersianDatePickerState` and `PersianDateRangePickerState` constructors public for advanced integrations and testability.
4. Added `DatePickerConfig` validation so empty `yearRange` fails early.
5. Added `DatePickerConstraints.hasSelectableDate(...)` for consumer-side preflight checks.
6. Added tests for config contract and selectable-date availability.
7. Wired `DatePickerConfig.quickActions` into the Pro footer.
8. Added docs for publishing, API stability, and license policy.
9. Reworked CI into Linux and macOS jobs so Web/Android checks and iOS simulator compilation are separated properly.
10. Renamed project identity to `PersianDatePickerKmmEnterpriseUltra` and updated public API version to `2.4.0-enterprise-ultra`.

## Static checks completed

- Kotlin/KTS files: 37
- Markdown docs: 18
- Kotlin/KTS lines: 4084
- Markdown lines: 1171
- Non-null assertions (`!!`): 0
- TODO/FIXME markers: 0
- Old project-name references: 0
- Raw sample `println(...)` usage: 0
- Lightweight delimiter balance check: OK
- Zip integrity check: OK

## Build note

A real Gradle build could not be executed in this environment because the Gradle Wrapper needs to download `gradle-8.11.1-all.zip` from `services.gradle.org`, and DNS/network access is blocked here. The exact failure was `UnknownHostException: services.gradle.org`.

Run this locally before publishing:

```bash
./gradlew clean \
  :persian-datepicker:allTests \
  :persian-datepicker:publishAllPublicationsToLocalStagingRepository \
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
