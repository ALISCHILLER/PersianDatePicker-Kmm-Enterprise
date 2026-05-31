package com.zargroup.persiandatepicker.ui

import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianYearMonth
import com.zargroup.persiandatepicker.core.indexRelativeTo
import com.zargroup.persiandatepicker.core.shift
import kotlinx.datetime.DayOfWeek

public enum class MonthCellPosition {
    Previous,
    Current,
    Next,
    Empty,
}

public data class CalendarMonthCell public constructor(
    public val date: PersianDate?,
    public val position: MonthCellPosition,
    public val dayOfWeek: DayOfWeek,
    public val weekdayIndex: Int,
) {
    public val isCurrentMonth: Boolean get() = position == MonthCellPosition.Current
}

public fun buildCalendarMonthCells(
    yearMonth: PersianYearMonth,
    weekConfiguration: WeekConfiguration,
    showAdjacentMonthDays: Boolean,
): List<CalendarMonthCell> {
    val startDay = weekConfiguration.startDay
    val firstDayOfMonth = yearMonth.firstDay().dayOfWeek()
    val leadingOffset = firstDayOfMonth.indexRelativeTo(startDay)
    val previousMonth = yearMonth.minusMonths(1)
    val nextMonth = yearMonth.plusMonths(1)
    val cells = mutableListOf<CalendarMonthCell>()

    repeat(leadingOffset) { index ->
        val dayOfWeek = startDay.shift(index)
        val date = if (showAdjacentMonthDays) {
            previousMonth.atDay(previousMonth.lengthOfMonth - leadingOffset + index + 1)
        } else {
            null
        }
        cells += CalendarMonthCell(
            date = date,
            position = if (date == null) MonthCellPosition.Empty else MonthCellPosition.Previous,
            dayOfWeek = dayOfWeek,
            weekdayIndex = dayOfWeek.indexRelativeTo(startDay),
        )
    }

    repeat(yearMonth.lengthOfMonth) { index ->
        val date = yearMonth.atDay(index + 1)
        val dayOfWeek = date.dayOfWeek()
        cells += CalendarMonthCell(
            date = date,
            position = MonthCellPosition.Current,
            dayOfWeek = dayOfWeek,
            weekdayIndex = dayOfWeek.indexRelativeTo(startDay),
        )
    }

    val targetSize = if (cells.size <= 35) 35 else 42
    while (cells.size < targetSize) {
        val index = cells.size
        val dayOfWeek = startDay.shift(index)
        val nextDay = index - leadingOffset - yearMonth.lengthOfMonth + 1
        val date = if (showAdjacentMonthDays) nextMonth.atDay(nextDay) else null
        cells += CalendarMonthCell(
            date = date,
            position = if (date == null) MonthCellPosition.Empty else MonthCellPosition.Next,
            dayOfWeek = dayOfWeek,
            weekdayIndex = dayOfWeek.indexRelativeTo(startDay),
        )
    }

    return cells
}
