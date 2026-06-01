package com.msa.persiandatepicker.core

/** A Persian calendar month without a day component. */
public data class PersianYearMonth public constructor(
    public val year: Int,
    public val month: Int,
) : Comparable<PersianYearMonth> {

    init {
        require(month in 1..12) { "month must be in 1..12 but was $month" }
    }

    public val lengthOfMonth: Int get() = PersianCalendarEngine.monthLength(year, month)

    public fun atDay(day: Int): PersianDate = PersianDate(year, month, day)

    public fun firstDay(): PersianDate = atDay(1)

    public fun lastDay(): PersianDate = atDay(lengthOfMonth)

    public fun plusMonths(months: Int): PersianYearMonth {
        if (months == 0) return this
        val absolute = year * 12 + (month - 1) + months
        val newYear = floorDiv(absolute, 12)
        val newMonth = floorMod(absolute, 12) + 1
        return PersianYearMonth(newYear, newMonth)
    }

    public fun minusMonths(months: Int): PersianYearMonth = plusMonths(-months)

    override fun compareTo(other: PersianYearMonth): Int = compareValuesBy(
        this,
        other,
        PersianYearMonth::year,
        PersianYearMonth::month,
    )

    public companion object {
        public fun from(date: PersianDate): PersianYearMonth = PersianYearMonth(date.year, date.month)
    }
}
