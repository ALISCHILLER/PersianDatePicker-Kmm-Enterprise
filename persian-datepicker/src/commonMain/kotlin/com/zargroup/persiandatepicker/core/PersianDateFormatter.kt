package com.zargroup.persiandatepicker.core

/** Platform-independent formatter for Jalali dates. */
public fun interface PersianDateFormatter {
    public fun format(date: PersianDate, digitMode: DigitMode): String

    public companion object {
        public fun numeric(
            separator: String = " / ",
            padMonthAndDay: Boolean = true,
        ): PersianDateFormatter = PersianDateFormatter { date, mode ->
            val monthLength = if (padMonthAndDay) 2 else 1
            listOf(
                date.year.toDigitString(mode),
                date.month.toDigitString(mode, minLength = monthLength),
                date.day.toDigitString(mode, minLength = monthLength),
            ).joinToString(separator)
        }

        public fun compact(): PersianDateFormatter = numeric(separator = "/", padMonthAndDay = true)

        public fun longPersian(
            includeWeekday: Boolean = false,
        ): PersianDateFormatter = PersianDateFormatter { date, mode ->
            val day = date.day.toDigitString(mode)
            val year = date.year.toDigitString(mode)
            val main = "$day ${CalendarTextRepository.persianMonths[date.month - 1]} $year"
            if (!includeWeekday) main else {
                val weekday = CalendarTextRepository.persianWeekdayFull(date.dayOfWeek())
                "$weekday، $main"
            }
        }

        public fun withGregorianHint(
            baseFormatter: PersianDateFormatter = longPersian(),
        ): PersianDateFormatter = PersianDateFormatter { date, mode ->
            val base = baseFormatter.format(date, mode)
            val gregorian = date.toGregorian().toString()
            val renderedGregorian = if (mode == DigitMode.Persian) gregorian.toPersianDigits() else gregorian
            "$base  •  $renderedGregorian"
        }
    }
}
