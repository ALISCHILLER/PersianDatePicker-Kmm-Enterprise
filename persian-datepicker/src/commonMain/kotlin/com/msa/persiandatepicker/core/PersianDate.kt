package com.msa.persiandatepicker.core

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/** Strongly typed date in the Persian/Jalali calendar. */
public data class PersianDate public constructor(
    public val year: Int,
    public val month: Int,
    public val day: Int,
) : Comparable<PersianDate> {

    init {
        require(month in 1..12) { "month must be in 1..12 but was $month" }
        val maxDay = PersianCalendarEngine.monthLength(year, month)
        require(day in 1..maxDay) { "day must be in 1..$maxDay for $year/$month but was $day" }
    }

    override fun compareTo(other: PersianDate): Int = compareValuesBy(
        this,
        other,
        PersianDate::year,
        PersianDate::month,
        PersianDate::day,
    )

    public val yearMonth: PersianYearMonth get() = PersianYearMonth(year, month)

    public fun toGregorian(): LocalDate = PersianCalendarEngine.toGregorian(this)

    public fun dayOfWeek(): DayOfWeek = PersianCalendarEngine.dayOfWeek(this)

    public fun isLeapYear(): Boolean = PersianCalendarEngine.isLeapYear(year)

    public fun plusDays(days: Int): PersianDate {
        if (days == 0) return this
        return PersianCalendarEngine.fromGregorian(LocalDate.fromEpochDays(toGregorian().toEpochDays() + days))
    }

    /** Returns null instead of throwing when the target date leaves the supported Jalali range. */
    public fun tryPlusDays(days: Int): PersianDate? = runCatching { plusDays(days) }.getOrNull()

    public fun minusDays(days: Int): PersianDate = plusDays(-days)

    /** Returns null instead of throwing when the target date leaves the supported Jalali range. */
    public fun tryMinusDays(days: Int): PersianDate? = tryPlusDays(-days)

    public fun plusMonths(months: Int): PersianDate {
        if (months == 0) return this
        val target = yearMonth.plusMonths(months)
        return target.atDay(day.coerceAtMost(target.lengthOfMonth))
    }

    public fun minusMonths(months: Int): PersianDate = plusMonths(-months)

    public fun plusYears(years: Int): PersianDate = plusMonths(years * 12)

    public fun daysUntil(other: PersianDate): Int = (other.toGregorian().toEpochDays() - toGregorian().toEpochDays()).toInt()

    public fun format(
        formatter: PersianDateFormatter = PersianDateFormatter.numeric(),
        digitMode: DigitMode = DigitMode.Persian,
    ): String = formatter.format(this, digitMode)

    public fun toLegacyMap(usePersianDigits: Boolean = true): Map<String, String> {
        val mode = if (usePersianDigits) DigitMode.Persian else DigitMode.Latin
        return mapOf(
            "year" to year.toDigitString(mode),
            "month" to month.toDigitString(mode, minLength = 2),
            "day" to day.toDigitString(mode, minLength = 2),
        )
    }

    public companion object {
        public fun today(): PersianDate = PersianCalendarEngine.today()

        public fun fromGregorian(date: LocalDate): PersianDate = PersianCalendarEngine.fromGregorian(date)

        public fun parseOrNull(value: String): PersianDate? {
            val normalized = value.toEnglishDigits().trim()
            val parts = normalized.split('/', '-', '.', ' ').filter { it.isNotBlank() }
            if (parts.size != 3) return null
            val year = parts[0].toIntOrNull() ?: return null
            val month = parts[1].toIntOrNull() ?: return null
            val day = parts[2].toIntOrNull() ?: return null
            return runCatching { PersianDate(year, month, day) }.getOrNull()
        }
    }
}

public typealias SoleimaniDate = PersianDate
