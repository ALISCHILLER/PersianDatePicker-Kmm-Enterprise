# Enterprise Final Audit — 2026-05-31

This pass prepares `PersianDatePickerKmmEnterpriseUltra` as a publish-oriented Compose Multiplatform library package.

## Scope reviewed

- Gradle multi-platform configuration.
- `persian-datepicker` public API surface.
- Common Jalali calendar engine.
- Date/range models and formatter/parser contracts.
- Base and Pro Compose UI components.
- Single and range picker state holders.
- Saveable state helpers.
- Constraints, selection policies, diagnostics, and presets.
- Android, iOS, Desktop, JS, and Wasm sample entry points.
- README, testing guide, publishing docs, changelog, and release checklist.

## Corrections made in this pass

1. Upgraded project identity to `PersianDatePickerKmmEnterpriseUltra`.
2. Updated library version to `2.4.0` and API marker to `2.4.0-enterprise-ultra`.
3. Added `DatePickerConfig.resolvedQuickActions()` as the shared footer action resolver.
4. Wired Base single picker, Base range picker, and Pro picker footer to the shared quick-action resolver.
5. Prevented duplicate Today actions when `showTodayAction = true` and `DatePickerQuickAction.Today` is also supplied manually.
6. Added non-blocking diagnostic `DUPLICATE_TODAY_ACTION_DEDUPED` so QA/CI reports explain the deduplication behavior.
7. Added tests for quick-action resolution and diagnostics.
8. Added tests for `PersianDateRange` ordering, containment length, overlap, and reversed-range rejection.
9. Added tests for nearest-valid constraint behavior.
10. Expanded README notes around quick-action resolution and final quality checks.

## Final static checks

- No `!!` non-null assertions in Kotlin sources.
- No TODO/FIXME markers in Kotlin sources.
- No duplicate built-in Today action rendering path remains in picker footers.
- Zip integrity verified after packaging.

## Remaining external verification

A full Gradle build must be run in a network-enabled environment because the current sandbox cannot download the Gradle distribution or dependency graph.

Recommended verification command:

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
