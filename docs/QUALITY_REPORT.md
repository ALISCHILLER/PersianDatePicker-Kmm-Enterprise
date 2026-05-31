# Quality Report

## Current package

- Project name: `PersianDatePickerKmmEnterpriseUltra`
- Version: `2.4.0`
- API marker: `2.4.0-enterprise-ultra`
- Library module: `persian-datepicker`
- Sample module: `sampleApp`

## Static inventory

- Kotlin files in library/sample: 43
- Common test files: 17
- Kotlin lines in library/sample: 4702
- Markdown documentation files: 23
- Markdown documentation lines: 2459

## Quality checks performed in this pass

- Reviewed Gradle settings and module structure.
- Reviewed library target configuration for Android, iOS, Desktop, JS, and Wasm.
- Reviewed sample app target configuration.
- Reviewed public API marker and version metadata.
- Reviewed core calendar model and date conversion surface.
- Reviewed config, constraints, diagnostics, state holders, and selection policy.
- Reviewed base and Pro UI files for obvious named-argument and wiring errors.
- Recompiled core and non-Compose UI logic against local stubs to catch syntax-level issues in source areas that do not require actual Compose runtime.
- Rewrote README and testing documentation.
- Added extra tests around diagnostics, formatting, week configuration, and quick actions.

## Scan results

- Non-null assertions in source: none detected.
- Raw debug `println(...)` usage in source: none detected.
- Old project-name references in active source/build files: none detected.
- Deprecated sample-only project name updated to `PersianDatePickerKmmEnterpriseUltra`.

## Required external verification

A full Gradle build must be run in a normal development environment or CI because this sandbox cannot download the Gradle distribution or dependencies.

Recommended command:

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
