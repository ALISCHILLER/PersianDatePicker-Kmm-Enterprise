# Contributing

## قواعد توسعه

- منطق تقویم باید در `core` بماند و به Compose یا Android وابسته نشود.
- UI مشترک باید در `commonMain` باقی بماند.
- APIهای عمومی باید strongly typed باشند؛ map/string فقط برای سازگاری legacy قابل قبول است.
- قبل از merge، تست‌های common باید اجرا شوند:

```bash
./gradlew :persian-datepicker:allTests
```

## سبک کدنویسی

- Kotlin official code style
- small composables
- state hoisting
- immutable config objects
- no Android resources inside common library
