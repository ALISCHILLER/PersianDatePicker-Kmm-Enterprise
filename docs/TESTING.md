# Testing Strategy

This document defines the verification strategy for **Persian DatePicker KMM Enterprise Ultra**.

The project is intended to become a reusable multi-platform library, so tests focus on deterministic `commonMain` behavior first, then platform buildability and manual UX smoke checks.

---

## 1. Pure core tests

Location:

```text
persian-datepicker/src/commonTest/kotlin/com/zargroup/persiandatepicker/core
```

Coverage:

- Jalali to Gregorian conversion.
- Gregorian to Jalali conversion.
- Known Nowruz boundaries.
- Leap-year behavior.
- Month length for Farvardin through Esfand.
- Safe arithmetic near supported calendar boundaries.
- Parser behavior for Persian, Arabic-Indic, and Latin digits.
- Formatter contracts.
- Stable key contracts.
- `PersianDateRange` ordering, overlap, and inclusive length.

Important files:

```text
PersianCalendarEngineTest.kt
PersianCalendarEngineBoundaryTest.kt
PersianDateParserTest.kt
PersianDateFormatterContractTest.kt
PersianDateSafeArithmeticTest.kt
PersianDateRangeContractTest.kt
```

---

## 2. UI-independent picker logic tests

Location:

```text
persian-datepicker/src/commonTest/kotlin/com/zargroup/persiandatepicker/ui
```

Coverage:

- Calendar month cell generation.
- Persian RTL week layout.
- International LTR week layout.
- Date constraints.
- Disabled dates.
- Custom validators.
- Nearest selectable date lookup.
- Typed range validation results.
- Initial single-date and range-date resolution.
- State mutation safety.
- Selection policy behavior.
- Quick action resolution and duplicate Today de-duplication.
- Diagnostics code stability.
- Fail-fast production validation.
- Field API public contract markers.

Important files:

```text
CalendarMonthCellsTest.kt
DatePickerConfigContractTest.kt
DatePickerConstraintsNearestValidTest.kt
DatePickerConstraintsTest.kt
DatePickerDiagnosticCodeContractTest.kt
DatePickerDiagnosticsTest.kt
DatePickerEnterpriseUltraContractTest.kt
DatePickerFieldApiContractTest.kt
DatePickerInitialSelectionTest.kt
DatePickerQuickActionContractTest.kt
DatePickerQuickActionResolutionTest.kt
DatePickerRangeValidationResultTest.kt
DatePickerSelectionPolicyTest.kt
DatePickerStateMutationTest.kt
DatePickerStatePolicyTest.kt
DatePickerValidationReportTest.kt
WeekConfigurationContractTest.kt
```

---

## 3. Full local verification

```bash
./gradlew clean \
  :persian-datepicker:allTests \
  :persian-datepicker:dokkaGenerate \
  :sampleApp:assembleDebug \
  :sampleApp:run \
  :sampleApp:wasmJsBrowserDistribution \
  :sampleApp:jsBrowserDistribution \
  :sampleApp:composeCompatibilityBrowserDistribution
```

---

## 4. iOS verification on macOS

```bash
./gradlew \
  :persian-datepicker:compileKotlinIosSimulatorArm64 \
  :sampleApp:compileKotlinIosSimulatorArm64
```

---

## 5. Publishing verification

```bash
./gradlew :persian-datepicker:publishAllPublicationsToLocalStagingRepository
```

Inspect:

```text
persian-datepicker/build/staging-deploy
```

Verify:

- KMP metadata publication exists.
- Android release publication exists.
- JS/Wasm/Desktop/iOS target artifacts are generated where supported by the host.
- Sources artifacts exist.
- Dokka/Javadoc artifact exists.
- POM metadata contains correct group, artifact, version, license, developer, and SCM fields.

---

## 6. CI matrix

The GitHub Actions workflow should keep two jobs:

### Ubuntu

- `:persian-datepicker:allTests`
- `:persian-datepicker:dokkaGenerate`
- `:sampleApp:assembleDebug`
- `:sampleApp:jsBrowserDistribution`
- `:sampleApp:wasmJsBrowserDistribution`
- `:sampleApp:composeCompatibilityBrowserDistribution`

### macOS

- `:persian-datepicker:compileKotlinIosSimulatorArm64`
- `:sampleApp:compileKotlinIosSimulatorArm64`

---

## 7. Manual UX smoke checklist

### Field API

- Single field opens the Pro dialog.
- Range field opens the Pro range dialog.
- Clear buttons clear selected values.
- Field values update after confirm.
- Disabled field does not open dialogs.
- Long formatted values ellipsize cleanly.

### Single picker

- Initial selected date is visible.
- Today action works.
- JumpToDate action works.
- Clear action works.
- Disabled dates are ignored with `Ignore` policy.
- Disabled dates snap with `SnapToNearestAvailable` policy.
- Month selector works.
- Year selector works.
- Confirm button is disabled with no selection.

### Range picker

- Pending start-only state is visible.
- Same-day range works when allowed.
- Same-day range stays pending when disabled.
- Max range length is enforced.
- Range with disabled dates is rejected.
- Dual-month range panel appears on expanded width.
- Confirm button is disabled until a full range exists.

### Web

- JS target renders into `#webApp`.
- Wasm target renders into `#webApp`.
- Browser viewport fills the page.
- No platform-specific APIs are used from common code.

---

## 8. Recommended future upgrades

- Android/Desktop screenshot tests.
- Browser Playwright smoke tests for JS and Wasm artifacts.
- Accessibility checks for semantics labels and disabled states.
- Binary compatibility validation after the API is frozen.
- Performance micro-benchmarks for large custom validator windows.
- Golden tests for event markers and dual-month range layout.
