package com.zargroup.persiandatepicker.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zargroup.persiandatepicker.core.CalendarTextRepository
import com.zargroup.persiandatepicker.core.DigitMode
import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianDateFormatter
import com.zargroup.persiandatepicker.core.PersianDateRange
import com.zargroup.persiandatepicker.core.PersianYearMonth
import com.zargroup.persiandatepicker.core.toDigitString

private enum class ProPickerMode { Day, Month, Year }

/**
 * Premium, production-oriented single-date dialog.
 *
 * This component is intentionally built from the same public state/config primitives as the base picker,
 * so teams can either use it directly or copy the visual layer while keeping the calendar engine stable.
 */
@Composable
public fun PersianDatePickerProDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (PersianDate) -> Unit,
    modifier: Modifier = Modifier,
    initialDate: PersianDate? = PersianDate.today(),
    config: DatePickerConfig = DatePickerConfig(),
    layoutOptions: DatePickerLayoutOptions = DatePickerLayoutOptions(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
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
        PersianDatePickerPro(
            state = state,
            onCancel = onDismissRequest,
            onConfirm = {
                onDateSelected(it)
                onDismissRequest()
            },
            modifier = modifier,
            config = config,
            layoutOptions = layoutOptions,
            colors = colors,
        )
    }
}

@Composable
public fun PersianDateRangePickerProDialog(
    onDismissRequest: () -> Unit,
    onRangeSelected: (PersianDateRange) -> Unit,
    modifier: Modifier = Modifier,
    initialStartDate: PersianDate? = null,
    initialEndDate: PersianDate? = null,
    config: DatePickerConfig = DatePickerConfig(),
    layoutOptions: DatePickerLayoutOptions = DatePickerLayoutOptions(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
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
        PersianDateRangePickerPro(
            state = state,
            onCancel = onDismissRequest,
            onConfirm = {
                onRangeSelected(it)
                onDismissRequest()
            },
            modifier = modifier,
            config = config,
            layoutOptions = layoutOptions,
            colors = colors,
        )
    }
}

@Composable
public fun PersianDatePickerPro(
    state: PersianDatePickerState,
    onConfirm: (PersianDate) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    config: DatePickerConfig = DatePickerConfig(),
    layoutOptions: DatePickerLayoutOptions = DatePickerLayoutOptions(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
): Unit {
    var mode by remember { mutableStateOf(ProPickerMode.Day) }
    val today = remember { PersianDate.today() }

    CompositionLocalProvider(LocalLayoutDirection provides config.weekConfiguration.layoutDirection) {
        val selectedSubtitle = state.selectedDate?.format(
            formatter = if (layoutOptions.showGregorianHint) {
                PersianDateFormatter.withGregorianHint(config.dateFormatter)
            } else {
                config.dateFormatter
            },
            digitMode = config.digitMode,
        ) ?: config.strings.selectedDate

        ProPickerFrame(
            title = config.strings.title,
            subtitle = selectedSubtitle,
            visibleMonth = state.visibleMonth,
            mode = mode,
            onModeChange = { mode = it },
            onPreviousMonth = { state.previousMonth() },
            onNextMonth = { state.nextMonth() },
            modifier = modifier,
            config = config,
            layoutOptions = layoutOptions,
            colors = colors,
            modeSwitchingEnabled = true,
        ) {
            AnimatedContent(targetState = mode, label = "single-picker-mode") { targetMode ->
                when (targetMode) {
                    ProPickerMode.Day -> ProCalendarGrid(
                        visibleMonth = state.visibleMonth,
                        selectedStart = state.selectedDate,
                        selectedEnd = state.selectedDate,
                        highlightedDate = today.takeIf { config.highlightToday },
                        config = config,
                        colors = colors,
                        layoutOptions = layoutOptions,
                        onDateClick = { date ->
                            state.applyTap(date, config.constraints, config.selectionPolicy)
                            if (config.selectionPolicy.autoCloseAfterSingleSelection) {
                                state.selectedDate?.let(onConfirm)
                            }
                        },
                    )

                    ProPickerMode.Month -> ProMonthSelector(
                        selectedMonth = state.visibleMonth.month,
                        config = config,
                        colors = colors,
                        onMonthSelected = { month ->
                            state.showMonth(PersianYearMonth(state.visibleMonth.year, month))
                            mode = ProPickerMode.Day
                        },
                    )

                    ProPickerMode.Year -> ProYearSelector(
                        selectedYear = state.visibleMonth.year,
                        config = config,
                        colors = colors,
                        onYearSelected = { year ->
                            state.showMonth(PersianYearMonth(year, state.visibleMonth.month))
                            mode = ProPickerMode.Day
                        },
                    )
                }
            }

            ProFooter(
                confirmEnabled = state.selectedDate != null,
                selectedSummary = state.selectedDate?.format(config.dateFormatter, config.digitMode),
                config = config,
                layoutOptions = layoutOptions,
                onCancel = onCancel,
                onClear = { state.clearSelection() },
                onToday = {
                    config.constraints.nearestValidOrNull(today)?.let {
                        state.applyTap(it, config.constraints, config.selectionPolicy)
                        mode = ProPickerMode.Day
                    }
                },
                onQuickAction = { action ->
                    when (action) {
                        DatePickerQuickAction.Today -> {
                            config.constraints.nearestValidOrNull(today)?.let {
                                state.applyTap(it, config.constraints, config.selectionPolicy)
                                mode = ProPickerMode.Day
                            }
                        }
                        is DatePickerQuickAction.ClearSelection -> state.clearSelection()
                        is DatePickerQuickAction.JumpToDate -> {
                            val target = action.targetDateProvider()
                            if (target != null) {
                                config.constraints.nearestValidOrNull(target)?.let {
                                    state.applyTap(it, config.constraints, config.selectionPolicy)
                                    mode = ProPickerMode.Day
                                }
                            }
                        }
                    }
                },
                onConfirm = { state.selectedDate?.let(onConfirm) },
            )
        }
    }
}

@Composable
public fun PersianDateRangePickerPro(
    state: PersianDateRangePickerState,
    onConfirm: (PersianDateRange) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    config: DatePickerConfig = DatePickerConfig(),
    layoutOptions: DatePickerLayoutOptions = DatePickerLayoutOptions(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
): Unit {
    val today = remember { PersianDate.today() }

    CompositionLocalProvider(LocalLayoutDirection provides config.weekConfiguration.layoutDirection) {
        ProPickerFrame(
            title = config.strings.rangeTitle,
            subtitle = state.selectedRange?.let {
                "${it.start.format(config.dateFormatter, config.digitMode)}  —  ${it.endInclusive.format(config.dateFormatter, config.digitMode)}"
            } ?: "${config.strings.rangeStartLabel} / ${config.strings.rangeEndLabel}",
            visibleMonth = state.visibleMonth,
            mode = ProPickerMode.Day,
            onModeChange = {},
            onPreviousMonth = { state.previousMonth() },
            onNextMonth = { state.nextMonth() },
            modifier = modifier,
            config = config,
            layoutOptions = layoutOptions.copy(showGregorianHint = false),
            colors = colors,
            modeSwitchingEnabled = false,
        ) {
            ProResponsiveRangeCalendar(
                visibleMonth = state.visibleMonth,
                selectedStart = state.startDate,
                selectedEnd = state.endDate,
                highlightedDate = today.takeIf { config.highlightToday },
                config = config,
                colors = colors,
                layoutOptions = layoutOptions,
                onDateClick = { date -> state.applyTap(date, config.constraints, config.selectionPolicy) },
            )

            ProRangeProgress(
                start = state.startDate,
                end = state.endDate,
                config = config,
            )

            ProFooter(
                confirmEnabled = state.selectedRange != null,
                selectedSummary = state.selectedRange?.let {
                    "${it.lengthInDays.toDigitString(config.digitMode)} ${if (config.digitMode == DigitMode.Persian) "روز" else "days"}"
                },
                config = config,
                layoutOptions = layoutOptions,
                onCancel = onCancel,
                onClear = { state.clearSelection() },
                onToday = {
                    config.constraints.nearestValidOrNull(today)?.let { state.applyTap(it, config.constraints, config.selectionPolicy) }
                },
                onQuickAction = { action ->
                    when (action) {
                        DatePickerQuickAction.Today -> {
                            config.constraints.nearestValidOrNull(today)?.let {
                                state.applyTap(it, config.constraints, config.selectionPolicy)
                            }
                        }
                        is DatePickerQuickAction.ClearSelection -> state.clearSelection()
                        is DatePickerQuickAction.JumpToDate -> {
                            val target = action.targetDateProvider()
                            if (target != null) {
                                config.constraints.nearestValidOrNull(target)?.let {
                                    state.applyTap(it, config.constraints, config.selectionPolicy)
                                }
                            }
                        }
                    }
                },
                onConfirm = { state.selectedRange?.let(onConfirm) },
            )
        }
    }
}

@Composable
private fun ProPickerFrame(
    title: String,
    subtitle: String,
    visibleMonth: PersianYearMonth,
    mode: ProPickerMode,
    onModeChange: (ProPickerMode) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier,
    config: DatePickerConfig,
    layoutOptions: DatePickerLayoutOptions,
    colors: PersianDatePickerColors,
    modeSwitchingEnabled: Boolean,
    content: @Composable Column.() -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val expanded = layoutOptions.panelSize == DatePickerPanelSize.Expanded ||
            (layoutOptions.panelSize == DatePickerPanelSize.Adaptive && maxWidth >= 760.dp)
        val panelWidth = when (layoutOptions.panelSize) {
            DatePickerPanelSize.Expanded -> 760.dp
            DatePickerPanelSize.Compact -> 360.dp
            DatePickerPanelSize.Adaptive -> if (expanded) 760.dp else 420.dp
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = panelWidth)
                .padding(12.dp),
            shape = RoundedCornerShape(if (expanded) 34.dp else 30.dp),
            color = colors.containerColor,
            tonalElevation = 6.dp,
            shadowElevation = 18.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f)),
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.24f),
                                colors.containerColor,
                                colors.containerColor,
                            ),
                        ),
                    )
                    .padding(if (expanded) 24.dp else 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ProHeroHeader(
                    title = title,
                    subtitle = subtitle,
                    visibleMonth = visibleMonth,
                    mode = mode,
                    onModeChange = onModeChange,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    config = config,
                    layoutOptions = layoutOptions,
                    colors = colors,
                    modeSwitchingEnabled = modeSwitchingEnabled,
                )
                content()
            }
        }
    }
}

@Composable
private fun ProHeroHeader(
    title: String,
    subtitle: String,
    visibleMonth: PersianYearMonth,
    mode: ProPickerMode,
    onModeChange: (ProPickerMode) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    config: DatePickerConfig,
    layoutOptions: DatePickerLayoutOptions,
    colors: PersianDatePickerColors,
    modeSwitchingEnabled: Boolean,
) {
    val month = config.monthFormatter.format(visibleMonth.month, config.digitMode)
    val year = config.yearFormatter.format(visibleMonth.year, config.digitMode)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.headerContentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                AnimatedVisibility(
                    visible = layoutOptions.showSelectedSummary,
                    enter = fadeIn(tween(180)),
                    exit = fadeOut(tween(120)),
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            AssistChip(
                onClick = { onModeChange(ProPickerMode.Day) },
                enabled = modeSwitchingEnabled,
                label = { Text(if (config.digitMode == DigitMode.Persian) "تقویم" else "Calendar") },
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)),
            shape = RoundedCornerShape(24.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onPreviousMonth) { Text("‹", style = MaterialTheme.typography.headlineSmall) }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElevatedAssistChip(
                        onClick = { onModeChange(ProPickerMode.Month) },
                        enabled = modeSwitchingEnabled,
                        label = { Text(month, fontWeight = FontWeight.SemiBold) },
                    )
                    Spacer(Modifier.width(8.dp))
                    ElevatedAssistChip(
                        onClick = { onModeChange(ProPickerMode.Year) },
                        enabled = modeSwitchingEnabled,
                        label = { Text(year, fontWeight = FontWeight.SemiBold) },
                    )
                }

                TextButton(onClick = onNextMonth) { Text("›", style = MaterialTheme.typography.headlineSmall) }
            }
        }

        if (modeSwitchingEnabled) {
            ProModeTabs(mode = mode, onModeChange = onModeChange, config = config)
        }
    }
}

@Composable
private fun ProModeTabs(
    mode: ProPickerMode,
    onModeChange: (ProPickerMode) -> Unit,
    config: DatePickerConfig,
) {
    val dayLabel = if (config.digitMode == DigitMode.Persian) "روز" else "Day"
    val monthLabel = if (config.digitMode == DigitMode.Persian) "ماه" else "Month"
    val yearLabel = if (config.digitMode == DigitMode.Persian) "سال" else "Year"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.54f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ProTab(label = dayLabel, selected = mode == ProPickerMode.Day, onClick = { onModeChange(ProPickerMode.Day) }, modifier = Modifier.weight(1f))
        ProTab(label = monthLabel, selected = mode == ProPickerMode.Month, onClick = { onModeChange(ProPickerMode.Month) }, modifier = Modifier.weight(1f))
        ProTab(label = yearLabel, selected = mode == ProPickerMode.Year, onClick = { onModeChange(ProPickerMode.Year) }, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ProTab(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val background by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(160),
        label = "tab-bg",
    )
    val content by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(160),
        label = "tab-fg",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = content, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
    }
}

@Composable
private fun ProResponsiveRangeCalendar(
    visibleMonth: PersianYearMonth,
    selectedStart: PersianDate?,
    selectedEnd: PersianDate?,
    highlightedDate: PersianDate?,
    config: DatePickerConfig,
    colors: PersianDatePickerColors,
    layoutOptions: DatePickerLayoutOptions,
    onDateClick: (PersianDate) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val showDualMonth = layoutOptions.showDualMonthRangeInExpandedPanel &&
            (layoutOptions.panelSize == DatePickerPanelSize.Expanded || maxWidth >= 700.dp)

        if (showDualMonth) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ProCalendarGrid(
                    visibleMonth = visibleMonth,
                    selectedStart = selectedStart,
                    selectedEnd = selectedEnd,
                    highlightedDate = highlightedDate,
                    config = config,
                    colors = colors,
                    layoutOptions = layoutOptions,
                    modifier = Modifier.weight(1f),
                    onDateClick = onDateClick,
                )
                ProCalendarGrid(
                    visibleMonth = visibleMonth.plusMonths(1),
                    selectedStart = selectedStart,
                    selectedEnd = selectedEnd,
                    highlightedDate = highlightedDate,
                    config = config,
                    colors = colors,
                    layoutOptions = layoutOptions,
                    modifier = Modifier.weight(1f),
                    onDateClick = onDateClick,
                )
            }
        } else {
            ProCalendarGrid(
                visibleMonth = visibleMonth,
                selectedStart = selectedStart,
                selectedEnd = selectedEnd,
                highlightedDate = highlightedDate,
                config = config,
                colors = colors,
                layoutOptions = layoutOptions,
                modifier = Modifier.fillMaxWidth(),
                onDateClick = onDateClick,
            )
        }
    }
}

@Composable
private fun ProCalendarGrid(
    visibleMonth: PersianYearMonth,
    selectedStart: PersianDate?,
    selectedEnd: PersianDate?,
    highlightedDate: PersianDate?,
    config: DatePickerConfig,
    colors: PersianDatePickerColors,
    layoutOptions: DatePickerLayoutOptions,
    modifier: Modifier = Modifier,
    onDateClick: (PersianDate) -> Unit,
) {
    val cells = remember(visibleMonth, config.weekConfiguration, config.showAdjacentMonthDays) {
        buildCalendarMonthCells(visibleMonth, config.weekConfiguration, config.showAdjacentMonthDays)
    }
    val cellGap = when (layoutOptions.density) {
        DatePickerVisualDensity.Compact -> 5.dp
        DatePickerVisualDensity.Comfortable -> 7.dp
        DatePickerVisualDensity.Spacious -> 10.dp
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(cellGap)) {
        Row(horizontalArrangement = Arrangement.spacedBy(cellGap), modifier = Modifier.fillMaxWidth()) {
            config.weekConfiguration.orderedDays.forEach { day ->
                Text(
                    modifier = Modifier.weight(1f),
                    text = CalendarTextRepository.weekdayShort(day, config.digitMode, config.weekConfiguration.startDay),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (config.weekConfiguration.isWeekend(day)) colors.weekendContentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        cells.chunked(7).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(cellGap), modifier = Modifier.fillMaxWidth()) {
                row.forEach { cell ->
                    ProDayCell(
                        cell = cell,
                        visibleMonth = visibleMonth,
                        selectedStart = selectedStart,
                        selectedEnd = selectedEnd,
                        highlightedDate = highlightedDate,
                        config = config,
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        onDateClick = onDateClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProDayCell(
    cell: CalendarMonthCell,
    visibleMonth: PersianYearMonth,
    selectedStart: PersianDate?,
    selectedEnd: PersianDate?,
    highlightedDate: PersianDate?,
    config: DatePickerConfig,
    colors: PersianDatePickerColors,
    modifier: Modifier = Modifier,
    onDateClick: (PersianDate) -> Unit,
) {
    val date = cell.date
    val selectedRange = when {
        selectedStart != null && selectedEnd != null -> PersianDateRange.ordered(selectedStart, selectedEnd)
        selectedStart != null -> PersianDateRange(selectedStart, selectedStart)
        else -> null
    }
    val isSelected = date != null && selectedRange?.contains(date) == true
    val isEdge = date != null && (date == selectedRange?.start || date == selectedRange?.endInclusive)
    val isToday = date != null && date == highlightedDate
    val isSelectable = date != null && config.constraints.isDateSelectable(date)
    val isTapEnabled = date != null && config.selectionPolicy.isTapEnabled(date, config.constraints)
    val isAdjacent = date != null && date.yearMonth != visibleMonth
    val event = date?.let(config.eventIndicator)

    val targetBg = when {
        isEdge -> colors.selectedDayContainerColor
        isSelected -> colors.rangeContainerColor
        else -> colors.dayContainerColor.copy(alpha = if (isAdjacent) 0.20f else 0.55f)
    }
    val targetText = when {
        !isSelectable -> colors.disabledContentColor
        isEdge -> colors.selectedDayContentColor
        isAdjacent -> colors.adjacentMonthContentColor
        config.weekConfiguration.isWeekend(cell.dayOfWeek) -> colors.weekendContentColor
        else -> colors.dayContentColor
    }
    val bg by animateColorAsState(targetBg, tween(140), label = "day-bg")
    val fg by animateColorAsState(targetText, tween(140), label = "day-fg")
    val radius by animateDpAsState(if (isEdge) 18.dp else 15.dp, tween(140), label = "day-radius")
    val shape = RoundedCornerShape(radius)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(bg)
            .then(if (isToday && !isEdge) Modifier.border(1.5.dp, colors.todayBorderColor, shape) else Modifier)
            .clickable(enabled = isTapEnabled, role = Role.Button) { if (date != null) onDateClick(date) }
            .semantics {
                if (date != null) {
                    contentDescription = buildString {
                        append(date.format(config.dateFormatter, config.digitMode))
                        if (!isSelectable) append(" ${config.strings.unavailableDate}")
                    }
                    selected = isSelected
                    if (!isTapEnabled) disabled()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (date != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(
                    text = date.day.toDigitString(config.digitMode),
                    color = fg,
                    fontWeight = if (isSelected || isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (event != null) {
                    Spacer(Modifier.height(3.dp))
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 4.dp else 5.dp)
                            .clip(CircleShape)
                            .background(event.color),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProMonthSelector(
    selectedMonth: Int,
    config: DatePickerConfig,
    colors: PersianDatePickerColors,
    onMonthSelected: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        config.monthFormatter.labels(config.digitMode).chunked(3).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { columnIndex, label ->
                    val month = rowIndex * 3 + columnIndex + 1
                    ProSelectorTile(
                        label = label,
                        selected = month == selectedMonth,
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        onClick = { onMonthSelected(month) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProYearSelector(
    selectedYear: Int,
    config: DatePickerConfig,
    colors: PersianDatePickerColors,
    onYearSelected: (Int) -> Unit,
) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .height(308.dp)
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        config.yearRange.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { year ->
                    ProSelectorTile(
                        label = config.yearFormatter.format(year, config.digitMode),
                        selected = year == selectedYear,
                        colors = colors,
                        modifier = Modifier.weight(1f),
                        onClick = { onYearSelected(year) },
                    )
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun ProSelectorTile(
    label: String,
    selected: Boolean,
    colors: PersianDatePickerColors,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg by animateColorAsState(
        if (selected) colors.selectedDayContainerColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.56f),
        tween(140),
        label = "selector-bg",
    )
    val fg by animateColorAsState(
        if (selected) colors.selectedDayContentColor else MaterialTheme.colorScheme.onSurface,
        tween(140),
        label = "selector-fg",
    )
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = fg, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ProRangeProgress(start: PersianDate?, end: PersianDate?, config: DatePickerConfig) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f)),
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RangePart(config.strings.rangeStartLabel, start?.format(config.dateFormatter, config.digitMode) ?: "—")
            VerticalDivider(modifier = Modifier.height(34.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            RangePart(config.strings.rangeEndLabel, end?.format(config.dateFormatter, config.digitMode) ?: "—")
        }
    }
}

@Composable
private fun RowScope.RangePart(label: String, value: String) {
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProFooter(
    confirmEnabled: Boolean,
    selectedSummary: String?,
    config: DatePickerConfig,
    layoutOptions: DatePickerLayoutOptions,
    onCancel: () -> Unit,
    onClear: () -> Unit,
    onToday: () -> Unit,
    onQuickAction: (DatePickerQuickAction) -> Unit,
    onConfirm: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AnimatedVisibility(visible = layoutOptions.showConstraintHint && selectedSummary != null) {
            Text(
                text = selectedSummary.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        if (layoutOptions.showEventLegend && config.eventLegend.isNotEmpty()) {
            ProEventLegend(config = config)
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            config.resolvedQuickActions().forEach { action ->
                OutlinedButton(
                    onClick = {
                        when (action) {
                            DatePickerQuickAction.Today -> onToday()
                            else -> onQuickAction(action)
                        }
                    },
                ) {
                    Text(action.label(config.strings))
                }
            }
            TextButton(onClick = onClear) { Text(config.strings.clearSelection) }
            TextButton(onClick = onCancel) { Text(config.strings.cancel) }
            Button(
                onClick = onConfirm,
                enabled = confirmEnabled,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
            ) { Text(config.strings.confirm) }
        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProEventLegend(config: DatePickerConfig) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        config.eventLegend.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.56f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(item.color),
                )
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
