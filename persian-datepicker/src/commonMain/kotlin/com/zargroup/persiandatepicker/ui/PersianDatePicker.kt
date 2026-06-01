package com.zargroup.persiandatepicker.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zargroup.persiandatepicker.core.CalendarTextRepository
import com.zargroup.persiandatepicker.core.DigitMode
import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianDateRange
import com.zargroup.persiandatepicker.core.PersianYearMonth
import com.zargroup.persiandatepicker.core.toDigitString

private enum class PickerMode {
    Day,
    Month,
    Year,
}

@Composable
public fun PersianDatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (PersianDate) -> Unit,
    modifier: Modifier = Modifier,
    initialDate: PersianDate? = PersianDate.today(),
    config: DatePickerConfig = DatePickerConfig(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    onFormattedDateSelected: (String) -> Unit = {},
    onLegacyDateSelected: (Map<String, String>) -> Unit = {},
): Unit {
    val resolvedInitial = remember(initialDate, config.constraints) {
        config.constraints.resolveInitialDateOrNull(initialDate)
    }
    val visibleMonth = remember(resolvedInitial, config.constraints) {
        config.constraints.resolveVisibleMonth(resolvedInitial)
    }
    val state = rememberSaveablePersianDatePickerState(
        initialSelectedDate = resolvedInitial,
        initialVisibleMonth = visibleMonth,
    )

    Dialog(onDismissRequest = onDismissRequest) {
        PersianDatePicker(
            state = state,
            config = config,
            colors = colors,
            modifier = modifier,
            onCancel = onDismissRequest,
            onConfirm = { selected ->
                onLegacyDateSelected(selected.toLegacyMap(usePersianDigits = config.digitMode == DigitMode.Persian))
                onFormattedDateSelected(config.dateFormatter.format(selected, config.digitMode))
                onDateSelected(selected)
                onDismissRequest()
            },
        )
    }
}

@Composable
public fun PersianDatePicker(
    state: PersianDatePickerState,
    onConfirm: (PersianDate) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    config: DatePickerConfig = DatePickerConfig(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
): Unit {
    var pickerMode by remember { mutableStateOf(PickerMode.Day) }
    val today = remember { PersianDate.today() }

    CompositionLocalProvider(LocalLayoutDirection provides config.weekConfiguration.layoutDirection) {
        PickerSurface(modifier = modifier, colors = colors) {
            PickerHeader(
                title = config.strings.title,
                visibleMonth = state.visibleMonth,
                pickerMode = pickerMode,
                config = config,
                colors = colors,
                modeSwitchingEnabled = true,
                onPreviousMonth = { state.previousMonth() },
                onNextMonth = { state.nextMonth() },
                onMonthClick = { pickerMode = PickerMode.Month },
                onYearClick = { pickerMode = PickerMode.Year },
                onDayClick = { pickerMode = PickerMode.Day },
            )

            Spacer(Modifier.height(14.dp))

            when (pickerMode) {
                PickerMode.Day -> CalendarMonthGrid(
                    visibleMonth = state.visibleMonth,
                    selectedStart = state.selectedDate,
                    selectedEnd = null,
                    highlightedDate = today.takeIf { config.highlightToday },
                    config = config,
                    colors = colors,
                    onDateClick = { date ->
                        state.applyTap(date, config.constraints, config.selectionPolicy)
                        if (config.selectionPolicy.autoCloseAfterSingleSelection) {
                            state.selectedDate?.let(onConfirm)
                        }
                    },
                )

                PickerMode.Month -> MonthGrid(
                    selectedMonth = state.visibleMonth.month,
                    config = config,
                    onMonthSelected = { month ->
                        state.showMonth(PersianYearMonth(state.visibleMonth.year, month))
                        pickerMode = PickerMode.Day
                    },
                )

                PickerMode.Year -> YearGrid(
                    selectedYear = state.visibleMonth.year,
                    config = config,
                    onYearSelected = { year ->
                        state.showMonth(PersianYearMonth(year, state.visibleMonth.month))
                        pickerMode = PickerMode.Day
                    },
                )
            }

            Spacer(Modifier.height(16.dp))

            PickerFooter(
                selectedDate = state.selectedDate,
                config = config,
                onCancel = onCancel,
                onClear = { state.clearSelection() },
                onConfirm = { state.selectedDate?.let(onConfirm) },
                onQuickAction = { action ->
                    when (action) {
                        DatePickerQuickAction.Today -> {
                            val target = config.constraints.nearestValidOrNull(today) ?: return@PickerFooter
                            state.select(target, config.constraints)
                            pickerMode = PickerMode.Day
                        }

                        is DatePickerQuickAction.ClearSelection -> state.clearSelection()

                        is DatePickerQuickAction.JumpToDate -> {
                            val target = action.targetDateProvider() ?: return@PickerFooter
                            val resolved = config.constraints.nearestValidOrNull(target) ?: return@PickerFooter
                            state.select(resolved, config.constraints)
                            pickerMode = PickerMode.Day
                        }
                    }
                },
            )
        }
    }
}

@Composable
public fun PersianDateRangePickerDialog(
    onDismissRequest: () -> Unit,
    onRangeSelected: (PersianDateRange) -> Unit,
    modifier: Modifier = Modifier,
    initialStartDate: PersianDate? = null,
    initialEndDate: PersianDate? = null,
    config: DatePickerConfig = DatePickerConfig(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    onLegacyRangeSelected: (start: Map<String, String>, end: Map<String, String>) -> Unit = { _, _ -> },
): Unit {
    val resolvedRange = remember(initialStartDate, initialEndDate, config.constraints) {
        config.constraints.resolveInitialRangeSelection(initialStartDate, initialEndDate)
    }
    val visibleMonth = remember(resolvedRange, config.constraints) {
        config.constraints.resolveVisibleMonth(resolvedRange.visibleSeed)
    }
    val state = rememberSaveablePersianDateRangePickerState(
        initialStartDate = resolvedRange.startDate,
        initialEndDate = resolvedRange.endDate,
        initialVisibleMonth = visibleMonth,
    )

    Dialog(onDismissRequest = onDismissRequest) {
        PersianDateRangePicker(
            state = state,
            config = config,
            colors = colors,
            modifier = modifier,
            onCancel = onDismissRequest,
            onConfirm = { range ->
                onLegacyRangeSelected(
                    range.start.toLegacyMap(usePersianDigits = config.digitMode == DigitMode.Persian),
                    range.endInclusive.toLegacyMap(usePersianDigits = config.digitMode == DigitMode.Persian),
                )
                onRangeSelected(range)
                onDismissRequest()
            },
        )
    }
}

@Composable
public fun PersianDateRangePicker(
    state: PersianDateRangePickerState,
    onConfirm: (PersianDateRange) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    config: DatePickerConfig = DatePickerConfig(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
): Unit {
    val today = remember { PersianDate.today() }

    CompositionLocalProvider(LocalLayoutDirection provides config.weekConfiguration.layoutDirection) {
        PickerSurface(modifier = modifier, colors = colors) {
            PickerHeader(
                title = config.strings.rangeTitle,
                visibleMonth = state.visibleMonth,
                pickerMode = PickerMode.Day,
                config = config,
                colors = colors,
                modeSwitchingEnabled = false,
                onPreviousMonth = { state.previousMonth() },
                onNextMonth = { state.nextMonth() },
                onMonthClick = {},
                onYearClick = {},
                onDayClick = {},
            )

            Spacer(Modifier.height(14.dp))

            CalendarMonthGrid(
                visibleMonth = state.visibleMonth,
                selectedStart = state.startDate,
                selectedEnd = state.endDate,
                highlightedDate = today.takeIf { config.highlightToday },
                config = config,
                colors = colors,
                onDateClick = { date -> state.applyTap(date, config.constraints, config.selectionPolicy) },
            )

            Spacer(Modifier.height(16.dp))

            RangeSummary(state = state, config = config)

            Spacer(Modifier.height(12.dp))

            RangeQuickActions(
                config = config,
                onQuickAction = { action ->
                    when (action) {
                        DatePickerQuickAction.Today -> {
                            val target = config.constraints.nearestValidOrNull(today) ?: return@RangeQuickActions
                            state.applyTap(target, config.constraints, config.selectionPolicy)
                        }

                        is DatePickerQuickAction.ClearSelection -> state.clearSelection()

                        is DatePickerQuickAction.JumpToDate -> {
                            val target = action.targetDateProvider() ?: return@RangeQuickActions
                            val resolved = config.constraints.nearestValidOrNull(target) ?: return@RangeQuickActions
                            state.applyTap(resolved, config.constraints, config.selectionPolicy)
                        }
                    }
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = { state.clearSelection() },
                    enabled = state.startDate != null || state.endDate != null,
                ) {
                    Text(config.strings.clearSelection)
                }

                Spacer(Modifier.weight(1f))

                TextButton(onClick = onCancel) { Text(config.strings.cancel) }

                Spacer(Modifier.width(8.dp))

                Button(
                    enabled = state.selectedRange != null,
                    onClick = { state.selectedRange?.let(onConfirm) },
                ) {
                    Text(config.strings.confirm)
                }
            }
        }
    }
}

@Composable
private fun PickerSurface(
    modifier: Modifier,
    colors: PersianDatePickerColors,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = colors.containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content,
        )
    }
}

@Composable
private fun PickerHeader(
    title: String,
    visibleMonth: PersianYearMonth,
    pickerMode: PickerMode,
    config: DatePickerConfig,
    colors: PersianDatePickerColors,
    modeSwitchingEnabled: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthClick: () -> Unit,
    onYearClick: () -> Unit,
    onDayClick: () -> Unit,
) {
    val monthLabel = config.monthFormatter.format(visibleMonth.month, config.digitMode)
    val yearLabel = config.yearFormatter.format(visibleMonth.year, config.digitMode)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.headerContentColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onPreviousMonth,
                modifier = Modifier.semantics { contentDescription = config.strings.previousMonth },
            ) {
                Text("‹", style = MaterialTheme.typography.headlineSmall)
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ElevatedAssistChip(
                    onClick = onMonthClick,
                    enabled = modeSwitchingEnabled,
                    label = { Text(monthLabel) },
                )

                Spacer(Modifier.width(8.dp))

                ElevatedAssistChip(
                    onClick = onYearClick,
                    enabled = modeSwitchingEnabled,
                    label = { Text(yearLabel) },
                )
            }

            TextButton(
                onClick = onNextMonth,
                modifier = Modifier.semantics { contentDescription = config.strings.nextMonth },
            ) {
                Text("›", style = MaterialTheme.typography.headlineSmall)
            }
        }

        if (pickerMode != PickerMode.Day) {
            TextButton(onClick = onDayClick) {
                Text(config.strings.title)
            }
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    visibleMonth: PersianYearMonth,
    selectedStart: PersianDate?,
    selectedEnd: PersianDate?,
    highlightedDate: PersianDate?,
    config: DatePickerConfig,
    colors: PersianDatePickerColors,
    onDateClick: (PersianDate) -> Unit,
) {
    val cells = remember(visibleMonth, config.weekConfiguration, config.showAdjacentMonthDays) {
        buildCalendarMonthCells(
            yearMonth = visibleMonth,
            weekConfiguration = config.weekConfiguration,
            showAdjacentMonthDays = config.showAdjacentMonthDays,
        )
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            config.weekConfiguration.orderedDays.forEach { day ->
                val label = CalendarTextRepository.weekdayShort(
                    day = day,
                    digitMode = config.digitMode,
                    weekStartsOn = config.weekConfiguration.startDay,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (config.weekConfiguration.isWeekend(day)) {
                        colors.weekendContentColor
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 292.dp, max = 356.dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(2.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            itemsIndexed(
                items = cells,
                key = { index, cell ->
                    cell.date?.let { "${it.year}-${it.month}-${it.day}-${cell.position}" } ?: "empty-$index"
                },
            ) { _, cell ->
                val date = cell.date
                if (date == null) {
                    Spacer(Modifier.aspectRatio(1f))
                } else {
                    val range = if (selectedStart != null && selectedEnd != null) {
                        PersianDateRange.ordered(selectedStart, selectedEnd)
                    } else {
                        null
                    }

                    DayCell(
                        date = date,
                        isCurrentMonth = cell.isCurrentMonth,
                        isSelected = date == selectedStart || date == selectedEnd,
                        isInRange = range?.contains(date) == true && date != selectedStart && date != selectedEnd,
                        isToday = date == highlightedDate,
                        isWeekend = config.weekConfiguration.isWeekend(date.dayOfWeek()),
                        isSelectable = config.constraints.isDateSelectable(date),
                        isTapEnabled = config.selectionPolicy.isTapEnabled(date, config.constraints),
                        event = config.eventIndicator(date),
                        digitMode = config.digitMode,
                        colors = colors,
                        config = config,
                        onClick = { onDateClick(date) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: PersianDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isInRange: Boolean,
    isToday: Boolean,
    isWeekend: Boolean,
    isSelectable: Boolean,
    isTapEnabled: Boolean,
    event: CalendarEvent?,
    digitMode: DigitMode,
    colors: PersianDatePickerColors,
    config: DatePickerConfig,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(15.dp)
    val containerColor = when {
        isSelected -> colors.selectedDayContainerColor
        isInRange -> colors.rangeContainerColor
        isToday -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
        else -> colors.dayContainerColor
    }
    val contentColor = when {
        !isSelectable -> colors.disabledContentColor
        isSelected -> colors.selectedDayContentColor
        !isCurrentMonth -> colors.adjacentMonthContentColor
        isWeekend -> colors.weekendContentColor
        else -> colors.dayContentColor
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(containerColor.copy(alpha = if (isCurrentMonth) 1f else 0.55f))
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(BorderStroke(1.2.dp, colors.todayBorderColor), shape)
                } else {
                    Modifier
                },
            )
            .clickable(enabled = isTapEnabled, onClick = onClick)
            .semantics {
                contentDescription = buildString {
                    append(config.dateFormatter.format(date, digitMode))
                    if (!isSelectable) append(" ${config.strings.unavailableDate}")
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = date.day.toDigitString(digitMode),
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
        )

        if (event != null && isCurrentMonth) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
                    .size(width = 18.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(event.color),
            )
        }
    }
}

@Composable
private fun MonthGrid(
    selectedMonth: Int,
    config: DatePickerConfig,
    onMonthSelected: (Int) -> Unit,
) {
    val labels = config.monthFormatter.labels(config.digitMode)

    Column {
        Text(
            text = config.strings.selectMonth,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(282.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(labels.size) { index ->
                val month = index + 1
                SelectablePill(
                    text = labels[index],
                    selected = month == selectedMonth,
                    onClick = { onMonthSelected(month) },
                )
            }
        }
    }
}

@Composable
private fun YearGrid(
    selectedYear: Int,
    config: DatePickerConfig,
    onYearSelected: (Int) -> Unit,
) {
    val years = remember(config.yearRange) { config.yearRange.toList() }

    Column {
        Text(
            text = config.strings.selectYear,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(318.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(years, key = { it }) { year ->
                SelectablePill(
                    text = config.yearFormatter.format(year, config.digitMode),
                    selected = year == selectedYear,
                    onClick = { onYearSelected(year) },
                )
            }
        }
    }
}

@Composable
private fun SelectablePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 9.dp),
        ) {
            Text(text = text, maxLines = 1)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 9.dp),
        ) {
            Text(text = text, maxLines = 1)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PickerFooter(
    selectedDate: PersianDate?,
    config: DatePickerConfig,
    onCancel: () -> Unit,
    onClear: () -> Unit,
    onConfirm: () -> Unit,
    onQuickAction: (DatePickerQuickAction) -> Unit,
) {
    val quickActions = remember(config.quickActions, config.showTodayAction) {
        config.resolvedQuickActions()
    }

    Column {
        selectedDate?.let {
            Text(
                text = "${config.strings.selectedDate}: ${config.dateFormatter.format(it, config.digitMode)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
        }

        if (quickActions.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                quickActions.forEach { action ->
                    AssistChip(
                        onClick = { onQuickAction(action) },
                        label = { Text(action.label(config.strings)) },
                    )
                    Spacer(Modifier.width(6.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onClear, enabled = selectedDate != null) {
                Text(config.strings.clearSelection)
            }

            Spacer(Modifier.weight(1f))

            TextButton(onClick = onCancel) {
                Text(config.strings.cancel)
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = onConfirm, enabled = selectedDate != null) {
                Text(config.strings.confirm)
            }
        }
    }
}

@Composable
private fun RangeSummary(
    state: PersianDateRangePickerState,
    config: DatePickerConfig,
) {
    val start = state.startDate?.let { config.dateFormatter.format(it, config.digitMode) } ?: "—"
    val end = state.endDate?.let { config.dateFormatter.format(it, config.digitMode) } ?: "—"
    val rangeLimit = config.constraints.maxRangeLength

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "${config.strings.rangeStartLabel}: $start",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "${config.strings.rangeEndLabel}: $end",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (rangeLimit != null) {
            Text(
                text = config.strings.rangeLimitMessage.replace("%1\$s", rangeLimit.toDigitString(config.digitMode)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RangeQuickActions(
    config: DatePickerConfig,
    onQuickAction: (DatePickerQuickAction) -> Unit,
) {
    val quickActions = remember(config.quickActions, config.showTodayAction) {
        config.resolvedQuickActions()
    }

    if (quickActions.isEmpty()) return

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        quickActions.forEach { action ->
            AssistChip(
                onClick = { onQuickAction(action) },
                label = { Text(action.label(config.strings)) },
            )
            Spacer(Modifier.width(6.dp))
        }
    }

    Spacer(Modifier.height(10.dp))
}
