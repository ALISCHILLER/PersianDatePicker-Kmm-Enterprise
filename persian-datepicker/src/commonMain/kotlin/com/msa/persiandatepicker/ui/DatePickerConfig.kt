package com.msa.persiandatepicker.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import com.msa.persiandatepicker.core.CalendarTextRepository
import com.msa.persiandatepicker.core.DigitMode
import com.msa.persiandatepicker.core.PersianCalendarEngine
import com.msa.persiandatepicker.core.PersianDate
import com.msa.persiandatepicker.core.PersianDateRange
import com.msa.persiandatepicker.core.PersianDateFormatter
import com.msa.persiandatepicker.core.toDigitString
import com.msa.persiandatepicker.core.shift
import kotlinx.datetime.DayOfWeek

@Stable
public data class DatePickerConfig public constructor(
    public val strings: DatePickerStrings = DatePickerStrings.persian(),
    public val digitMode: DigitMode = DigitMode.Persian,
    public val weekConfiguration: WeekConfiguration = WeekConfiguration.persian(),
    public val constraints: DatePickerConstraints = DatePickerConstraints(),
    public val monthFormatter: MonthFormatter = MonthFormatter.Persian,
    public val yearFormatter: YearFormatter = YearFormatter.WithGregorianHint,
    public val dateFormatter: PersianDateFormatter = PersianDateFormatter.numeric(),
    public val yearRange: IntRange = 1300..1500,
    public val showAdjacentMonthDays: Boolean = true,
    public val highlightToday: Boolean = true,
    public val selectionPolicy: DatePickerSelectionPolicy = DatePickerSelectionPolicy(),
    public val showTodayAction: Boolean = true,
    public val quickActions: List<DatePickerQuickAction> = emptyList(),
    public val eventLegend: List<CalendarEventLegendItem> = emptyList(),
    public val eventIndicator: (PersianDate) -> CalendarEvent? = { null },
) {
    init {
        require(!yearRange.isEmpty()) { "yearRange must not be empty." }
        require(yearRange.first >= PersianCalendarEngine.supportedYearRange.first) {
            "yearRange starts before supported Persian year ${PersianCalendarEngine.supportedYearRange.first}."
        }
        require(yearRange.last <= PersianCalendarEngine.supportedYearRange.last) {
            "yearRange ends after supported Persian year ${PersianCalendarEngine.supportedYearRange.last}."
        }
    }
}

@Immutable
public data class DatePickerStrings public constructor(
    public val title: String,
    public val rangeTitle: String,
    public val confirm: String,
    public val cancel: String,
    public val today: String,
    public val clearSelection: String,
    public val selectMonth: String,
    public val selectYear: String,
    public val selectedDate: String,
    public val rangeStartLabel: String,
    public val rangeEndLabel: String,
    public val rangeLimitMessage: String,
    public val previousMonth: String,
    public val nextMonth: String,
    public val unavailableDate: String,
) {
    public companion object {
        public fun persian(): DatePickerStrings = DatePickerStrings(
            title = "انتخاب تاریخ",
            rangeTitle = "انتخاب بازه تاریخ",
            confirm = "تایید",
            cancel = "انصراف",
            today = "امروز",
            clearSelection = "پاک کردن",
            selectMonth = "انتخاب ماه",
            selectYear = "انتخاب سال",
            selectedDate = "تاریخ انتخاب‌شده",
            rangeStartLabel = "شروع بازه",
            rangeEndLabel = "پایان بازه",
            rangeLimitMessage = "حداکثر بازه مجاز %1\$s روز است.",
            previousMonth = "ماه قبل",
            nextMonth = "ماه بعد",
            unavailableDate = "غیرفعال",
        )

        public fun english(): DatePickerStrings = DatePickerStrings(
            title = "Select date",
            rangeTitle = "Select date range",
            confirm = "Confirm",
            cancel = "Cancel",
            today = "Today",
            clearSelection = "Clear",
            selectMonth = "Select month",
            selectYear = "Select year",
            selectedDate = "Selected date",
            rangeStartLabel = "Start date",
            rangeEndLabel = "End date",
            rangeLimitMessage = "Maximum allowed range is %1\$s days.",
            previousMonth = "Previous month",
            nextMonth = "Next month",
            unavailableDate = "Unavailable",
        )
    }
}


public sealed interface DatePickerRangeValidationResult {
    public data object Valid : DatePickerRangeValidationResult
    public data class ExceedsMaxRangeLength public constructor(public val actualLength: Int, public val maxLength: Int) : DatePickerRangeValidationResult
    public data class ExceedsValidationWindow public constructor(public val actualLength: Int, public val maxValidationDays: Int) : DatePickerRangeValidationResult
    public data class UnselectableDate public constructor(public val date: PersianDate) : DatePickerRangeValidationResult
    public data object CalendarBoundaryExceeded : DatePickerRangeValidationResult
}

public val DatePickerRangeValidationResult.isValid: Boolean
    get() = this is DatePickerRangeValidationResult.Valid

@Stable
public data class DatePickerConstraints public constructor(
    public val minDate: PersianDate? = null,
    public val maxDate: PersianDate? = null,
    public val disabledDates: Set<PersianDate> = emptySet(),
    public val maxRangeLength: Int? = null,
    /** Upper bound for day-by-day range validation when custom validators are used. */
    public val maxRangeValidationDays: Int = 3660,
    public val dateValidator: (PersianDate) -> Boolean = { true },
) {
    init {
        if (minDate != null && maxDate != null) {
            require(minDate <= maxDate) { "minDate must be before or equal to maxDate" }
        }
        if (maxRangeLength != null) {
            require(maxRangeLength > 0) { "maxRangeLength must be greater than zero" }
        }
        require(maxRangeValidationDays > 0) { "maxRangeValidationDays must be greater than zero" }
    }

    public fun isWithinBounds(date: PersianDate): Boolean {
        if (minDate != null && date < minDate) return false
        if (maxDate != null && date > maxDate) return false
        return true
    }

    public fun isDateSelectable(date: PersianDate): Boolean {
        if (!isWithinBounds(date)) return false
        if (date in disabledDates) return false
        return dateValidator(date)
    }

    public fun validateRange(start: PersianDate, end: PersianDate): DatePickerRangeValidationResult {
        val first = minOf(start, end)
        val second = maxOf(start, end)
        val length = first.daysUntil(second) + 1
        val limit = maxRangeLength
        if (limit != null && length > limit) {
            return DatePickerRangeValidationResult.ExceedsMaxRangeLength(length, limit)
        }
        if (length > maxRangeValidationDays) {
            return DatePickerRangeValidationResult.ExceedsValidationWindow(length, maxRangeValidationDays)
        }

        var cursor = first
        repeat(length) { index ->
            if (!isDateSelectable(cursor)) {
                return DatePickerRangeValidationResult.UnselectableDate(cursor)
            }
            if (index < length - 1) {
                cursor = cursor.tryPlusDays(1) ?: return DatePickerRangeValidationResult.CalendarBoundaryExceeded
            }
        }
        return DatePickerRangeValidationResult.Valid
    }

    public fun validateRange(range: PersianDateRange): DatePickerRangeValidationResult =
        validateRange(range.start, range.endInclusive)

    public fun isRangeSelectable(start: PersianDate, end: PersianDate): Boolean = validateRange(start, end).isValid

    public fun isRangeSelectable(range: PersianDateRange): Boolean = validateRange(range).isValid

    public fun clamp(date: PersianDate): PersianDate {
        val minClamped = minDate?.let { if (date < it) it else date } ?: date
        return maxDate?.let { if (minClamped > it) it else minClamped } ?: minClamped
    }

    public fun nearestValidOrNull(anchor: PersianDate, searchRadiusDays: Int = 3660): PersianDate? {
        require(searchRadiusDays >= 0) { "searchRadiusDays must be zero or greater." }

        val clamped = clamp(anchor)
        if (isDateSelectable(clamped)) return clamped

        var forward: PersianDate? = clamped
        var backward: PersianDate? = clamped
        repeat(searchRadiusDays) {
            forward = forward?.tryPlusDays(1)?.takeIf { maxDate == null || it <= maxDate }
            forward?.let { if (isDateSelectable(it)) return it }

            backward = backward?.tryMinusDays(1)?.takeIf { minDate == null || it >= minDate }
            backward?.let { if (isDateSelectable(it)) return it }

            if (forward == null && backward == null) return null
        }
        return null
    }

    public fun hasSelectableDate(searchRadiusDays: Int = 3660): Boolean {
        val seed = minDate ?: maxDate ?: PersianDate.today()
        return nearestValidOrNull(seed, searchRadiusDays) != null
    }
}

@Immutable
public data class CalendarEvent public constructor(
    public val color: Color,
    public val label: String? = null,
)

@Immutable
public data class CalendarEventLegendItem public constructor(
    public val color: Color,
    public val label: String,
)

@Immutable
public data class WeekConfiguration public constructor(
    public val startDay: DayOfWeek = DayOfWeek.SATURDAY,
    public val weekendDays: Set<DayOfWeek> = setOf(DayOfWeek.FRIDAY),
    public val layoutDirection: LayoutDirection = LayoutDirection.Rtl,
) {
    public val orderedDays: List<DayOfWeek> = List(7) { startDay.shift(it) }

    public fun isWeekend(day: DayOfWeek): Boolean = day in weekendDays

    public companion object {
        public fun persian(): WeekConfiguration = WeekConfiguration()

        public fun international(): WeekConfiguration = WeekConfiguration(
            startDay = DayOfWeek.MONDAY,
            weekendDays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
            layoutDirection = LayoutDirection.Ltr,
        )
    }
}

@Immutable
public class MonthFormatter internal constructor(
    private val provider: (DigitMode) -> List<String>,
) {
    public fun format(month: Int, digitMode: DigitMode): String {
        require(month in 1..12) { "month must be in 1..12" }
        return provider(digitMode)[month - 1]
    }

    public fun labels(digitMode: DigitMode): List<String> = provider(digitMode)

    public companion object {
        public val Persian: MonthFormatter = MonthFormatter { CalendarTextRepository.persianMonths }

        public val PersianWithLatinTransliteration: MonthFormatter = MonthFormatter { mode ->
            when (mode) {
                DigitMode.Persian -> CalendarTextRepository.persianMonths
                DigitMode.Latin -> CalendarTextRepository.persianMonthsLatin
            }
        }

        public fun custom(provider: (DigitMode) -> List<String>): MonthFormatter = MonthFormatter { mode ->
            provider(mode).also { labels ->
                require(labels.size == 12) { "MonthFormatter.custom provider must return exactly 12 labels." }
            }
        }
    }
}

@Immutable
public class YearFormatter internal constructor(
    private val formatter: (Int, DigitMode) -> String,
) {
    public fun format(year: Int, digitMode: DigitMode): String = formatter(year, digitMode)

    public companion object {
        public val Default: YearFormatter = YearFormatter { year, mode -> year.toDigitString(mode) }

        public val WithGregorianHint: YearFormatter = YearFormatter { year, mode ->
            val primary = year.toDigitString(mode)
            val gregorian = PersianDate(year, 1, 1).toGregorian().year.toDigitString(mode)
            "$primary ($gregorian)"
        }

        public fun custom(formatter: (year: Int, digitMode: DigitMode) -> String): YearFormatter = YearFormatter(formatter)
    }
}

public sealed interface DatePickerQuickAction {
    public fun label(strings: DatePickerStrings): String

    public data object Today : DatePickerQuickAction {
        override fun label(strings: DatePickerStrings): String = strings.today
    }

    public data class ClearSelection public constructor(public val customLabel: String? = null) : DatePickerQuickAction {
        override fun label(strings: DatePickerStrings): String = customLabel ?: strings.clearSelection
    }

    public data class JumpToDate public constructor(
        public val customLabel: String,
        public val targetDateProvider: () -> PersianDate?,
    ) : DatePickerQuickAction {
        override fun label(strings: DatePickerStrings): String = customLabel
    }
}

/**
 * Returns the effective quick actions shown by picker footers.
 *
 * The built-in Today action is deduplicated so `showTodayAction = true` and an explicit
 * [DatePickerQuickAction.Today] inside [DatePickerConfig.quickActions] do not render two
 * identical buttons in the UI. Custom actions keep their original order.
 */
public fun DatePickerConfig.resolvedQuickActions(): List<DatePickerQuickAction> = buildList {
    if (showTodayAction) add(DatePickerQuickAction.Today)
    quickActions.forEach { action ->
        if (action is DatePickerQuickAction.Today && showTodayAction) return@forEach
        add(action)
    }
}

