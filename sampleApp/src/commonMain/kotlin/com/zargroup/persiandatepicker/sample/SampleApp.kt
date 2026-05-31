package com.zargroup.persiandatepicker.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zargroup.persiandatepicker.core.DigitMode
import com.zargroup.persiandatepicker.core.PersianDate
import com.zargroup.persiandatepicker.core.PersianDateFormatter
import com.zargroup.persiandatepicker.core.PersianDateRange
import com.zargroup.persiandatepicker.ui.CalendarEvent
import com.zargroup.persiandatepicker.ui.CalendarEventLegendItem
import com.zargroup.persiandatepicker.ui.DatePickerConfig
import com.zargroup.persiandatepicker.ui.DatePickerConfigValidator
import com.zargroup.persiandatepicker.ui.DatePickerDiagnosticSeverity
import com.zargroup.persiandatepicker.ui.DatePickerConstraints
import com.zargroup.persiandatepicker.ui.DatePickerLayoutOptions
import com.zargroup.persiandatepicker.ui.DatePickerPanelSize
import com.zargroup.persiandatepicker.ui.DatePickerQuickAction
import com.zargroup.persiandatepicker.ui.DatePickerSelectionPolicy
import com.zargroup.persiandatepicker.ui.DatePickerStrings
import com.zargroup.persiandatepicker.ui.DatePickerVisualDensity
import com.zargroup.persiandatepicker.ui.DatePickerValidationReport
import com.zargroup.persiandatepicker.ui.MonthFormatter
import com.zargroup.persiandatepicker.ui.PersianDatePickerColors
import com.zargroup.persiandatepicker.ui.PersianDatePickerPalettes
import com.zargroup.persiandatepicker.ui.PersianDatePickerField
import com.zargroup.persiandatepicker.ui.PersianDatePickerPro
import com.zargroup.persiandatepicker.ui.PersianDatePickerState
import com.zargroup.persiandatepicker.ui.PersianDatePickerProDialog
import com.zargroup.persiandatepicker.ui.PersianDateRangePickerField
import com.zargroup.persiandatepicker.ui.PersianDateRangePickerProDialog
import com.zargroup.persiandatepicker.ui.UnavailableDateStrategy
import com.zargroup.persiandatepicker.ui.WeekConfiguration
import com.zargroup.persiandatepicker.ui.rememberSaveablePersianDatePickerState
import kotlinx.datetime.DayOfWeek

@Composable
fun SampleApp() {
    var darkTheme by remember { mutableStateOf(false) }
    val colors = if (darkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colors) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            ShowcaseScreen(
                darkTheme = darkTheme,
                onDarkThemeChanged = { darkTheme = it },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShowcaseScreen(
    darkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
) {
    val today = remember { PersianDate.today() }

    var showSinglePicker by remember { mutableStateOf(false) }
    var showRangePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<PersianDate?>(today) }
    var selectedRange by remember { mutableStateOf<PersianDateRange?>(null) }

    var latinDigits by remember { mutableStateOf(false) }
    var nextThirtyDaysOnly by remember { mutableStateOf(false) }
    var blockFridays by remember { mutableStateOf(false) }
    var blockMonthEnds by remember { mutableStateOf(false) }
    var compactMode by remember { mutableStateOf(false) }
    var expandedPanel by remember { mutableStateOf(false) }
    var internationalWeek by remember { mutableStateOf(false) }
    var gregorianHint by remember { mutableStateOf(true) }
    var dualMonthRange by remember { mutableStateOf(true) }

    val digitMode = if (latinDigits) DigitMode.Latin else DigitMode.Persian
    val constraints = remember(nextThirtyDaysOnly, blockFridays, blockMonthEnds, today) {
        DatePickerConstraints(
            minDate = if (nextThirtyDaysOnly) today else PersianDate(1300, 1, 1),
            maxDate = if (nextThirtyDaysOnly) today.plusDays(30) else PersianDate(1500, 12, 29),
            maxRangeLength = if (nextThirtyDaysOnly) 14 else null,
            dateValidator = { date ->
                val fridayAllowed = !blockFridays || date.dayOfWeek() != DayOfWeek.FRIDAY
                val monthEndAllowed = !blockMonthEnds || date.day < date.yearMonth.lengthOfMonth
                fridayAllowed && monthEndAllowed
            },
        )
    }

    val config = remember(digitMode, constraints, internationalWeek, today) {
        DatePickerConfig(
            strings = if (digitMode == DigitMode.Persian) DatePickerStrings.persian() else DatePickerStrings.english(),
            digitMode = digitMode,
            weekConfiguration = if (internationalWeek) WeekConfiguration.international() else WeekConfiguration.persian(),
            constraints = constraints,
            monthFormatter = MonthFormatter.PersianWithLatinTransliteration,
            dateFormatter = PersianDateFormatter.longPersian(includeWeekday = digitMode == DigitMode.Persian),
            selectionPolicy = DatePickerSelectionPolicy(
                unavailableDateStrategy = UnavailableDateStrategy.SnapToNearestAvailable,
                allowSameDayRange = true,
            ),
            showAdjacentMonthDays = true,
            eventLegend = listOf(
                CalendarEventLegendItem(Color(0xFF059669), if (digitMode == DigitMode.Persian) "امروز" else "Today"),
                CalendarEventLegendItem(Color(0xFF7C3AED), if (digitMode == DigitMode.Persian) "شروع ماه" else "Month start"),
                CalendarEventLegendItem(Color(0xFFDC2626), if (digitMode == DigitMode.Persian) "جمعه" else "Friday"),
            ),
            quickActions = listOf(
                DatePickerQuickAction.JumpToDate(
                    customLabel = if (digitMode == DigitMode.Persian) "نوروز" else "Nowruz",
                    targetDateProvider = { PersianDate(today.year, 1, 1) },
                ),
            ),
            eventIndicator = { date ->
                when {
                    date == today -> CalendarEvent(Color(0xFF059669), "Today")
                    date.day == 1 -> CalendarEvent(Color(0xFF7C3AED), "Month start")
                    date.dayOfWeek() == DayOfWeek.FRIDAY -> CalendarEvent(Color(0xFFDC2626), "Friday")
                    else -> null
                }
            },
        )
    }

    val layoutOptions = remember(compactMode, expandedPanel, gregorianHint, dualMonthRange) {
        DatePickerLayoutOptions(
            panelSize = if (expandedPanel) DatePickerPanelSize.Expanded else DatePickerPanelSize.Adaptive,
            density = if (compactMode) DatePickerVisualDensity.Compact else DatePickerVisualDensity.Comfortable,
            showSelectedSummary = true,
            showGregorianHint = gregorianHint,
            showConstraintHint = true,
            showEventLegend = true,
            showDualMonthRangeInExpandedPanel = dualMonthRange,
        )
    }

    val pickerColors = PersianDatePickerPalettes.royalIndigo()
    val validationReport = remember(config) { DatePickerConfigValidator.validateProductionReady(config) }
    val inlinePickerState = rememberSaveablePersianDatePickerState(
        initialSelectedDate = selectedDate ?: today,
        initialVisibleMonth = (selectedDate ?: today).yearMonth,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.22f),
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HeroCard()

            ResultCard(
                selectedDate = selectedDate,
                selectedRange = selectedRange,
                digitMode = digitMode,
                onSingleClick = { showSinglePicker = true },
                onRangeClick = { showRangePicker = true },
            )

            FieldApiCard(
                selectedDate = selectedDate,
                selectedRange = selectedRange,
                config = config,
                layoutOptions = layoutOptions,
                colors = pickerColors,
                onDateSelected = { selectedDate = it },
                onDateCleared = { selectedDate = null },
                onRangeSelected = { selectedRange = it },
                onRangeCleared = { selectedRange = null },
            )

            QualityCard(report = validationReport)

            InlinePickerCard(
                state = inlinePickerState,
                config = config,
                layoutOptions = layoutOptions,
                colors = pickerColors,
                onDateConfirmed = { selectedDate = it },
            )

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text("Runtime Lab", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                    Text(
                        "تمام گزینه‌ها بدون platform-specific code روی Android، iOS، Desktop و Web کار می‌کنند.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ToggleChip("Latin digits", latinDigits) { latinDigits = !latinDigits }
                        ToggleChip("Next 30 days", nextThirtyDaysOnly) { nextThirtyDaysOnly = !nextThirtyDaysOnly }
                        ToggleChip("Block Fridays", blockFridays) { blockFridays = !blockFridays }
                        ToggleChip("Block month ends", blockMonthEnds) { blockMonthEnds = !blockMonthEnds }
                        ToggleChip("Compact", compactMode) { compactMode = !compactMode }
                        ToggleChip("Expanded panel", expandedPanel) { expandedPanel = !expandedPanel }
                        ToggleChip("International week", internationalWeek) { internationalWeek = !internationalWeek }
                        ToggleChip("Gregorian hint", gregorianHint) { gregorianHint = !gregorianHint }
                        ToggleChip("Dual range panel", dualMonthRange) { dualMonthRange = !dualMonthRange }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Dark theme", fontWeight = FontWeight.SemiBold)
                        Switch(checked = darkTheme, onCheckedChange = onDarkThemeChanged)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showSinglePicker) {
        PersianDatePickerProDialog(
            onDismissRequest = { showSinglePicker = false },
            onDateSelected = { selectedDate = it },
            initialDate = selectedDate,
            config = config,
            layoutOptions = layoutOptions,
            colors = pickerColors,
        )
    }

    if (showRangePicker) {
        PersianDateRangePickerProDialog(
            onDismissRequest = { showRangePicker = false },
            onRangeSelected = { selectedRange = it },
            initialStartDate = selectedRange?.start,
            initialEndDate = selectedRange?.endInclusive,
            config = config,
            layoutOptions = layoutOptions,
            colors = pickerColors,
        )
    }
}

@Composable
private fun FieldApiCard(
    selectedDate: PersianDate?,
    selectedRange: PersianDateRange?,
    config: DatePickerConfig,
    layoutOptions: DatePickerLayoutOptions,
    colors: PersianDatePickerColors,
    onDateSelected: (PersianDate) -> Unit,
    onDateCleared: () -> Unit,
    onRangeSelected: (PersianDateRange) -> Unit,
    onRangeCleared: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Production Field API", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(
                "این بخش API سطح بالای field را نشان می‌دهد؛ مناسب فرم‌های واقعی، پروفایل، رزرو، فیلتر گزارش و screenهای enterprise.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PersianDatePickerField(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                label = if (config.digitMode == DigitMode.Persian) "تاریخ فرم" else "Form date",
                config = config,
                layoutOptions = layoutOptions,
                colors = colors,
                onClear = onDateCleared,
            )
            PersianDateRangePickerField(
                selectedRange = selectedRange,
                onRangeSelected = onRangeSelected,
                label = if (config.digitMode == DigitMode.Persian) "بازه فرم" else "Form range",
                config = config,
                layoutOptions = layoutOptions,
                colors = colors,
                onClear = onRangeCleared,
            )
        }
    }
}

@Composable
private fun InlinePickerCard(
    state: PersianDatePickerState,
    config: DatePickerConfig,
    layoutOptions: DatePickerLayoutOptions,
    colors: PersianDatePickerColors,
    onDateConfirmed: (PersianDate) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Inline Pro Picker", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(
                "نمونه‌ی inline نشان می‌دهد library فقط dialog نیست؛ همین state و config را می‌توان داخل screen، bottom sheet یا dashboard هم استفاده کرد.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PersianDatePickerPro(
                state = state,
                onConfirm = onDateConfirmed,
                onCancel = { state.clearSelection() },
                config = config,
                layoutOptions = layoutOptions.copy(
                    panelSize = DatePickerPanelSize.Compact,
                    showEventLegend = false,
                    showConstraintHint = false,
                ),
                colors = colors,
            )
        }
    }
}

@Composable
private fun QualityCard(report: DatePickerValidationReport) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Production Preflight", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text(
                "این بخش همان Diagnostic API کتابخانه را روی تنظیمات فعلی اجرا می‌کند تا قبل از انتشار یا integration، خطای پنهان باقی نماند.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Report: ${report.summary}",
                color = if (report.isReady) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
            report.diagnostics.forEach { diagnostic ->
                val prefix = when (diagnostic.severity) {
                    DatePickerDiagnosticSeverity.Info -> "OK"
                    DatePickerDiagnosticSeverity.Warning -> "WARN"
                    DatePickerDiagnosticSeverity.Error -> "ERROR"
                }
                Text(
                    text = "$prefix • ${diagnostic.code}: ${diagnostic.message}",
                    color = when (diagnostic.severity) {
                        DatePickerDiagnosticSeverity.Info -> MaterialTheme.colorScheme.primary
                        DatePickerDiagnosticSeverity.Warning -> MaterialTheme.colorScheme.tertiary
                        DatePickerDiagnosticSeverity.Error -> MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun HeroCard() {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        ),
                    ),
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                "Persian DatePicker KMM Enterprise Ultra",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Text(
                "یک DatePicker جلالی کاملاً commonMain با UI حرفه‌ای، state قابل save/restore، constraints، range picker و sample چندپلتفرمی.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.86f),
            )
        }
    }
}

@Composable
private fun ResultCard(
    selectedDate: PersianDate?,
    selectedRange: PersianDateRange?,
    digitMode: DigitMode,
    onSingleClick: () -> Unit,
    onRangeClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Selected Values", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            SummaryRow("Single date", selectedDate?.format(PersianDateFormatter.withGregorianHint(), digitMode) ?: "—")
            SummaryRow(
                "Range",
                selectedRange?.let {
                    "${it.start.format(PersianDateFormatter.compact(), digitMode)}  →  ${it.endInclusive.format(PersianDateFormatter.compact(), digitMode)}"
                } ?: "—",
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                androidx.compose.material3.Button(onClick = onSingleClick) { Text("Open single picker") }
                androidx.compose.material3.OutlinedButton(onClick = onRangeClick) { Text("Open range picker") }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            modifier = Modifier.padding(start = 12.dp),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ToggleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
    )
}
