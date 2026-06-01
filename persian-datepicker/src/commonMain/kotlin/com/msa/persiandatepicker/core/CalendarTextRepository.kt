package com.msa.persiandatepicker.core

import kotlinx.datetime.DayOfWeek

public object CalendarTextRepository {
    public val persianMonths: List<String> = listOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند",
    )

    public val persianMonthsLatin: List<String> = listOf(
        "Farvardin", "Ordibehesht", "Khordad", "Tir", "Mordad", "Shahrivar",
        "Mehr", "Aban", "Azar", "Dey", "Bahman", "Esfand",
    )

    public val gregorianMonthsFa: List<String> = listOf(
        "ژانویه", "فوریه", "مارس", "آوریل", "مه", "ژوئن",
        "ژوئیه", "اوت", "سپتامبر", "اکتبر", "نوامبر", "دسامبر",
    )

    public val gregorianMonthsEn: List<String> = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December",
    )

    private val persianWeekdaysShort = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")
    private val latinWeekdaysShort = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")


    public fun persianWeekdayFull(day: DayOfWeek): String = when (day) {
        DayOfWeek.SATURDAY -> "شنبه"
        DayOfWeek.SUNDAY -> "یکشنبه"
        DayOfWeek.MONDAY -> "دوشنبه"
        DayOfWeek.TUESDAY -> "سه‌شنبه"
        DayOfWeek.WEDNESDAY -> "چهارشنبه"
        DayOfWeek.THURSDAY -> "پنجشنبه"
        DayOfWeek.FRIDAY -> "جمعه"
    }

    public fun englishWeekdayFull(day: DayOfWeek): String = when (day) {
        DayOfWeek.MONDAY -> "Monday"
        DayOfWeek.TUESDAY -> "Tuesday"
        DayOfWeek.WEDNESDAY -> "Wednesday"
        DayOfWeek.THURSDAY -> "Thursday"
        DayOfWeek.FRIDAY -> "Friday"
        DayOfWeek.SATURDAY -> "Saturday"
        DayOfWeek.SUNDAY -> "Sunday"
    }

    public fun weekdayShort(day: DayOfWeek, digitMode: DigitMode, weekStartsOn: DayOfWeek): String {
        val normalizedDay = weekStartsOn.shift(day.indexRelativeTo(weekStartsOn))
        return when (digitMode) {
            DigitMode.Persian -> persianWeekdaysShort[normalizedDay.indexRelativeTo(DayOfWeek.SATURDAY)]
            DigitMode.Latin -> latinWeekdaysShort[normalizedDay.indexRelativeTo(DayOfWeek.MONDAY)]
        }
    }
}

public fun DayOfWeek.indexRelativeTo(startDay: DayOfWeek): Int = floorMod(isoNumber - startDay.isoNumber, 7)

public fun DayOfWeek.shift(days: Int): DayOfWeek {
    val nextIso = floorMod((isoNumber - 1) + days, 7) + 1
    return dayOfWeekByIsoNumber(nextIso)
}

private val DayOfWeek.isoNumber: Int
    get() = when (this) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 7
    }

private fun dayOfWeekByIsoNumber(value: Int): DayOfWeek = when (value) {
    1 -> DayOfWeek.MONDAY
    2 -> DayOfWeek.TUESDAY
    3 -> DayOfWeek.WEDNESDAY
    4 -> DayOfWeek.THURSDAY
    5 -> DayOfWeek.FRIDAY
    6 -> DayOfWeek.SATURDAY
    7 -> DayOfWeek.SUNDAY
    else -> error("invalid ISO day number: $value")
}
