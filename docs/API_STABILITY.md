# API stability policy

The public API is organized into two packages:

- `com.zargroup.persiandatepicker.core`: stable, platform-independent date models and Jalali conversion logic.
- `com.zargroup.persiandatepicker.ui`: Compose Multiplatform UI, configuration, state, colors, and selection policy.

## Versioning

This project follows semantic versioning after the `1.0.0` line:

- Patch: bug fixes that do not change source or binary compatibility.
- Minor: additive APIs, new configuration options, new UI components, new target support.
- Major: source-incompatible or behavior-breaking changes.

## Compatibility rules

- `PersianDate`, `PersianYearMonth`, and `PersianDateRange` are stable domain models.
- Calendar conversion behavior must stay deterministic and covered by tests.
- State classes are public and can be instantiated by consumers for tests, custom state holders, and non-dialog integrations.
- Experimental APIs must be annotated with `@ExperimentalPersianDatePickerApi` before publication.
- Publishing metadata must match the repository license.

## Release gate

Before publishing:

```bash
./gradlew clean \
  :persian-datepicker:allTests \
  :persian-datepicker:publishAllPublicationsToLocalStagingRepository \
  :sampleApp:assembleDebug \
  :sampleApp:run \
  :sampleApp:wasmJsBrowserDistribution \
  :sampleApp:jsBrowserDistribution
```
