package com.msa.persiandatepicker.core

/** Inclusive Persian date range. */
public data class PersianDateRange public constructor(
    public val start: PersianDate,
    public val endInclusive: PersianDate,
) {
    init {
        require(start <= endInclusive) { "start must be before or equal to endInclusive" }
    }

    public val lengthInDays: Int get() = start.daysUntil(endInclusive) + 1

    public operator fun contains(date: PersianDate): Boolean = date >= start && date <= endInclusive

    public fun overlaps(other: PersianDateRange): Boolean = start <= other.endInclusive && other.start <= endInclusive

    public companion object {
        public fun ordered(a: PersianDate, b: PersianDate): PersianDateRange = if (a <= b) {
            PersianDateRange(a, b)
        } else {
            PersianDateRange(b, a)
        }
    }
}
