package com.zargroup.persiandatepicker.core

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Pure Kotlin Persian/Jalali calendar engine.
 *
 * The implementation is independent from Android, java.time, ICU, and platform resources,
 * so it is safe for Android, iOS, Desktop, and common tests.
 */
public object PersianCalendarEngine {
    private const val UNIX_EPOCH_JULIAN_DAY = 2440588

    private val breaks = intArrayOf(
        -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181,
        1210, 1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178,
    )

    /** Inclusive range of Persian years supported by the conversion algorithm. */
    public val supportedYearRange: IntRange = breaks.first() until breaks.last()

    public fun isYearSupported(year: Int): Boolean = year in supportedYearRange

    public fun toGregorian(date: PersianDate): LocalDate = toGregorian(date.year, date.month, date.day)

    public fun toGregorian(year: Int, month: Int, day: Int): LocalDate {
        val jdn = persianToJulianDay(year, month, day)
        return LocalDate.fromEpochDays(jdn - UNIX_EPOCH_JULIAN_DAY)
    }

    public fun fromGregorian(date: LocalDate): PersianDate {
        val jdn = gregorianToJulianDay(date.year, date.monthNumber, date.dayOfMonth)
        val (year, month, day) = julianDayToPersian(jdn)
        return PersianDate(year, month, day)
    }

    public fun dayOfWeek(date: PersianDate): DayOfWeek = toGregorian(date).dayOfWeek

    public fun dayOfWeek(year: Int, month: Int, day: Int): DayOfWeek = dayOfWeek(PersianDate(year, month, day))

    public fun monthLength(year: Int, month: Int): Int {
        require(isYearSupported(year)) {
            "Persian year $year is outside supported range ${supportedYearRange.first}..${supportedYearRange.last}"
        }
        require(month in 1..12) { "month must be in 1..12 but was $month" }
        return when (month) {
            in 1..6 -> 31
            in 7..11 -> 30
            else -> if (isLeapYear(year)) 30 else 29
        }
    }

    public fun isValidDate(year: Int, month: Int, day: Int): Boolean {
        if (!isYearSupported(year)) return false
        if (month !in 1..12) return false
        return day in 1..monthLength(year, month)
    }

    public fun isLeapYear(year: Int): Boolean {
        require(isYearSupported(year)) {
            "Persian year $year is outside supported range ${supportedYearRange.first}..${supportedYearRange.last}"
        }
        return jalCal(year).leap == 0
    }

    public fun fromGregorianOrNull(date: LocalDate): PersianDate? = runCatching { fromGregorian(date) }.getOrNull()

    public fun today(timeZone: TimeZone = TimeZone.currentSystemDefault()): PersianDate {
        return fromGregorian(Clock.System.now().toLocalDateTime(timeZone).date)
    }

    internal fun persianToJulianDay(year: Int, month: Int, day: Int): Int {
        require(month in 1..12) { "month must be in 1..12 but was $month" }
        require(day in 1..monthLength(year, month)) { "day $day is not valid for $year/$month" }

        val calibration = jalCal(year)
        return gregorianToJulianDay(calibration.gregorianYear, 3, calibration.marchDay) +
            (month - 1) * 31 - truncDiv(month, 7) * (month - 7) + day - 1
    }

    private fun julianDayToPersian(jdn: Int): Triple<Int, Int, Int> {
        val (gy, _, _) = julianDayToGregorianParts(jdn)
        var jy = gy - 621
        var calibration = jalCal(jy)
        val farvardinFirstJdn = gregorianToJulianDay(gy, 3, calibration.marchDay)
        var dayOffset = jdn - farvardinFirstJdn

        if (dayOffset >= 0) {
            if (dayOffset <= 185) {
                val month = 1 + truncDiv(dayOffset, 31)
                val day = jsMod(dayOffset, 31) + 1
                return Triple(jy, month, day)
            }
            dayOffset -= 186
        } else {
            jy -= 1
            calibration = jalCal(jy)
            dayOffset += 179
            if (calibration.leap == 1) dayOffset += 1
        }

        val month = 7 + truncDiv(dayOffset, 30)
        val day = jsMod(dayOffset, 30) + 1
        return Triple(jy, month, day)
    }

    private fun jalCal(year: Int): JalaliCalibration {
        require(year >= breaks.first() && year < breaks.last()) {
            "Persian year $year is outside supported range ${breaks.first()}..${breaks.last() - 1}"
        }

        val gregorianYear = year + 621
        var leapJ = -14
        var previousBreak = breaks.first()
        var jump = 0

        for (index in 1 until breaks.size) {
            val currentBreak = breaks[index]
            jump = currentBreak - previousBreak
            if (year < currentBreak) break
            leapJ += truncDiv(jump, 33) * 8 + truncDiv(jsMod(jump, 33), 4)
            previousBreak = currentBreak
        }

        var yearsSinceBreak = year - previousBreak
        leapJ += truncDiv(yearsSinceBreak, 33) * 8 + truncDiv(jsMod(yearsSinceBreak, 33) + 3, 4)

        if (jsMod(jump, 33) == 4 && jump - yearsSinceBreak == 4) {
            leapJ += 1
        }

        val leapG = truncDiv(gregorianYear, 4) -
            truncDiv((truncDiv(gregorianYear, 100) + 1) * 3, 4) -
            150
        val marchDay = 20 + leapJ - leapG

        if (jump - yearsSinceBreak < 6) {
            yearsSinceBreak = yearsSinceBreak - jump + truncDiv(jump + 4, 33) * 33
        }

        var leap = jsMod(jsMod(yearsSinceBreak + 1, 33) - 1, 4)
        if (leap == -1) leap = 4

        return JalaliCalibration(
            leap = leap,
            gregorianYear = gregorianYear,
            marchDay = marchDay,
        )
    }

    private fun gregorianToJulianDay(year: Int, month: Int, day: Int): Int {
        return truncDiv((year + truncDiv(month - 8, 6) + 100100) * 1461, 4) +
            truncDiv(153 * jsMod(month + 9, 12) + 2, 5) +
            day - 34840408 -
            truncDiv(truncDiv(year + 100100 + truncDiv(month - 8, 6), 100) * 3, 4) +
            752
    }

    private fun julianDayToGregorianParts(jdn: Int): Triple<Int, Int, Int> {
        var j = 4 * jdn + 139361631
        j += truncDiv(truncDiv(4 * jdn + 183187720, 146097) * 3, 4) * 4 - 3908
        val i = truncDiv(jsMod(j, 1461), 4) * 5 + 308
        val day = truncDiv(jsMod(i, 153), 5) + 1
        val month = jsMod(truncDiv(i, 153), 12) + 1
        val year = truncDiv(j, 1461) - 100100 + truncDiv(8 - month, 6)
        return Triple(year, month, day)
    }

    private data class JalaliCalibration(
        val leap: Int,
        val gregorianYear: Int,
        val marchDay: Int,
    )
}

internal fun floorMod(value: Int, mod: Int): Int {
    val result = value % mod
    return if (result >= 0) result else result + mod
}

internal fun floorDiv(value: Int, divisor: Int): Int {
    var result = value / divisor
    if ((value xor divisor) < 0 && result * divisor != value) result--
    return result
}

private fun truncDiv(value: Int, divisor: Int): Int = value / divisor

private fun jsMod(value: Int, mod: Int): Int = value - truncDiv(value, mod) * mod
