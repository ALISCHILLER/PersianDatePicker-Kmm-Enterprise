# Enterprise Ultra Audit - 2026-05-31

This audit documents the final pass that moved the project from **Enterprise Final** to **Enterprise Ultra**.

## Scope

Reviewed and updated:

- Gradle/KMM module structure.
- Public API surface.
- Compose UI layers.
- Form-field integration story.
- Config diagnostics.
- Common test suite.
- README and testing documentation.
- Sample app coverage.
- Version metadata and project naming.

## Key improvements

### Production field API

Added:

- `PersianDatePickerField(...)`
- `PersianDateRangePickerField(...)`

These components provide ready-to-use form integrations that internally open Pro dialogs and keep product screens simpler.

### Stable diagnostic codes

Added:

- `DatePickerDiagnosticCode`
- `DatePickerValidationReport.hasCode(...)`
- `DatePickerValidationReport.hasError(...)`
- `DatePickerValidationReport.hasWarning(...)`
- `DatePickerValidationReport.hasInfo(...)`

This makes diagnostics safer for automated CI checks and production telemetry.

### Sample app upgrade

The sample app now demonstrates three integration levels:

1. Dialog API.
2. Production field API.
3. Inline Pro picker API.

### Test additions

Added tests for:

- Stable diagnostic-code lookup.
- Validator usage of stable codes.
- Field API public contract markers.
- Quick-action de-duplication.
- Range and nearest-valid constraints.

## Known environment limitation

The sandbox cannot download the Gradle distribution from `services.gradle.org`, so full Gradle execution is not possible here. The project was still checked through direct source inspection, static text scans, version consistency checks, and zip integrity verification.

## Recommended release gate

Before public publication, run on a connected local machine:

```bash
./gradlew clean \
  :persian-datepicker:allTests \
  :persian-datepicker:dokkaGenerate \
  :persian-datepicker:publishAllPublicationsToLocalStagingRepository \
  :sampleApp:assembleDebug \
  :sampleApp:run \
  :sampleApp:wasmJsBrowserDistribution \
  :sampleApp:jsBrowserDistribution \
  :sampleApp:composeCompatibilityBrowserDistribution
```

On macOS:

```bash
./gradlew \
  :persian-datepicker:compileKotlinIosSimulatorArm64 \
  :sampleApp:compileKotlinIosSimulatorArm64
```
