# Release Checklist

## Before release

- [ ] Run all common tests.
- [ ] Run Android sample on API 23 and latest API.
- [ ] Run Desktop sample.
- [ ] Build iOS framework and open the SwiftUI sample.
- [ ] Run Kotlin/Wasm browser sample.
- [ ] Run Kotlin/JS browser sample.
- [ ] Build browser compatibility distribution.
- [ ] Review public API names and KDoc.
- [ ] Update `CHANGELOG.md`.
- [ ] Update screenshots/gifs for Android, iOS, Desktop, and Web.
- [ ] Verify ProGuard consumer rules.
- [ ] Verify Maven coordinates and signing config.

## Suggested Maven coordinates

```kotlin
group = "com.msa"
artifact = "persian-datepicker-compose-multiplatform"
version = "1.0.0"
```

## Semantic versioning

- Patch: bug fixes, docs, non-breaking visual fixes.
- Minor: new APIs, new configuration options.
- Major: breaking API changes.
