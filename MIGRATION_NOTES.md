# Migration Notes

## From original Android library

Original package examples:

```kotlin
com.msa.calendar.utils.SoleimaniDate
com.msa.calendar.CalendarScreen
com.msa.calendar.RangeCalendarScreen
```

New package examples:

```kotlin
com.msa.persiandatepicker.core.PersianDate
com.msa.persiandatepicker.ui.PersianDatePickerProDialog
com.msa.persiandatepicker.ui.PersianDateRangePickerProDialog
```

## Date model

Old:

```kotlin
SoleimaniDate(year, month, day)
```

New:

```kotlin
PersianDate(year, month, day)
```

Compatibility:

```kotlin
typealias SoleimaniDate = PersianDate
```

## Date result callback

Prefer typed callbacks:

```kotlin
onDateSelected = { date: PersianDate -> }
```

Legacy map callbacks are still supported in the base picker for migration.

## Resources

Android XML strings/arrays were replaced with common Kotlin configuration:

```kotlin
DatePickerStrings.persian()
DatePickerStrings.english()
MonthFormatter.PersianWithLatinTransliteration
```

## Constraints

Move business rules into `DatePickerConstraints`:

```kotlin
DatePickerConstraints(
    minDate = PersianDate(1404, 1, 1),
    maxDate = PersianDate(1404, 12, 29),
    dateValidator = { it.dayOfWeek() != DayOfWeek.FRIDAY },
)
```
