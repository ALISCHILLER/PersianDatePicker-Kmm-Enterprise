# Security Policy

This library does not process network data, credentials, payment data, or personal data by default. Security work mainly focuses on safe API behavior, dependency hygiene, and preventing incorrect date validation in consuming products.

## Supported versions

| Version | Supported |
| --- | --- |
| 1.0.x | Yes |

## Reporting issues

Please open a private security report in the hosting repository or contact the maintainers directly.

## Dependency policy

- Keep Kotlin, Compose Multiplatform, Android Gradle Plugin, and kotlinx-datetime updated.
- Prefer official JetBrains/Google artifacts.
- Avoid platform-only APIs inside `commonMain`.
- Run tests for all enabled targets before publishing.
