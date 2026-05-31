# UI System

The Pro UI is built around a few reusable ideas:

## Visual hierarchy

1. Hero header: title, selected summary, month/year navigation.
2. Mode tabs: day/month/year.
3. Calendar grid: animated selected/range/today/event states.
4. Footer: today, clear, cancel, confirm.

## RTL and LTR

`WeekConfiguration.persian()` uses Saturday as the first day of week and RTL layout. `WeekConfiguration.international()` uses Monday and LTR layout.

## Density

`DatePickerLayoutOptions` supports:

- `Compact`
- `Comfortable`
- `Spacious`

## Color palettes

`PersianDatePickerPalettes` includes:

- `zarEmerald()`
- `royalIndigo()`
- `fromColorScheme()`

You can also pass a custom `PersianDatePickerColors` instance.

## Event indicators

Use `eventIndicator` to decorate days:

```kotlin
DatePickerConfig(
    eventIndicator = { date ->
        when {
            date.day == 1 -> CalendarEvent(Color.Magenta, "Month start")
            else -> null
        }
    }
)
```

## Final Pro UI behavior

The Pro picker now separates visual availability from tap behavior:

- A date can still look unavailable when it violates constraints.
- If `UnavailableDateStrategy.SnapToNearestAvailable` is enabled, the date remains tappable and resolves through policy.
- If `UnavailableDateStrategy.Ignore` is enabled, unavailable dates are disabled semantically and visually.

This keeps the UI honest while still enabling advanced business flows such as snapping a user from a holiday or blocked date to the next valid business day.

## Enterprise range layout

The Pro range picker can render one or two months depending on available width:

```kotlin
DatePickerLayoutOptions(
    panelSize = DatePickerPanelSize.Expanded,
    showDualMonthRangeInExpandedPanel = true,
)
```

For phones or compact dialogs, keep adaptive sizing enabled. For dashboard, desktop, tablet, and web surfaces, expanded sizing gives the range picker a more professional booking-calendar feel.
