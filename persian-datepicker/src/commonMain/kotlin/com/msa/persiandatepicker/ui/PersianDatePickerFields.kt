package com.msa.persiandatepicker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.msa.persiandatepicker.core.PersianDate
import com.msa.persiandatepicker.core.PersianDateRange

/**
 * Ready-to-use form field that opens [PersianDatePickerProDialog].
 *
 * This is the recommended high-level API for product forms and settings screens. Lower-level
 * picker composables remain available for custom bottom sheets, dashboards, and fully bespoke UI.
 */
@Composable
public fun PersianDatePickerField(
    selectedDate: PersianDate?,
    onDateSelected: (PersianDate) -> Unit,
    modifier: Modifier = Modifier,
    label: String = DatePickerStrings.persian().title,
    placeholder: String = "—",
    enabled: Boolean = true,
    config: DatePickerConfig = DatePickerConfig(),
    layoutOptions: DatePickerLayoutOptions = DatePickerLayoutOptions(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    onClear: (() -> Unit)? = null,
): Unit {
    var open by rememberSaveable { mutableStateOf(false) }
    val value = selectedDate?.format(config.dateFormatter, config.digitMode) ?: placeholder

    DatePickerFieldSurface(
        modifier = modifier,
        label = label,
        value = value,
        enabled = enabled,
        clearEnabled = selectedDate != null && onClear != null,
        clearLabel = config.strings.clearSelection,
        openLabel = config.strings.title,
        onOpen = { open = true },
        onClear = { onClear?.invoke() },
    )

    if (open) {
        PersianDatePickerProDialog(
            onDismissRequest = { open = false },
            onDateSelected = { date ->
                onDateSelected(date)
                open = false
            },
            initialDate = selectedDate,
            config = config,
            layoutOptions = layoutOptions,
            colors = colors,
        )
    }
}

/** Ready-to-use form field that opens [PersianDateRangePickerProDialog]. */
@Composable
public fun PersianDateRangePickerField(
    selectedRange: PersianDateRange?,
    onRangeSelected: (PersianDateRange) -> Unit,
    modifier: Modifier = Modifier,
    label: String = DatePickerStrings.persian().rangeTitle,
    placeholder: String = "—",
    enabled: Boolean = true,
    config: DatePickerConfig = DatePickerConfig(),
    layoutOptions: DatePickerLayoutOptions = DatePickerLayoutOptions(),
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    onClear: (() -> Unit)? = null,
): Unit {
    var open by rememberSaveable { mutableStateOf(false) }
    val value = selectedRange?.let { range ->
        val start = range.start.format(config.dateFormatter, config.digitMode)
        val end = range.endInclusive.format(config.dateFormatter, config.digitMode)
        "$start  —  $end"
    } ?: placeholder

    DatePickerFieldSurface(
        modifier = modifier,
        label = label,
        value = value,
        enabled = enabled,
        clearEnabled = selectedRange != null && onClear != null,
        clearLabel = config.strings.clearSelection,
        openLabel = config.strings.rangeTitle,
        onOpen = { open = true },
        onClear = { onClear?.invoke() },
    )

    if (open) {
        PersianDateRangePickerProDialog(
            onDismissRequest = { open = false },
            onRangeSelected = { range ->
                onRangeSelected(range)
                open = false
            },
            initialStartDate = selectedRange?.start,
            initialEndDate = selectedRange?.endInclusive,
            config = config,
            layoutOptions = layoutOptions,
            colors = colors,
        )
    }
}

@Composable
private fun DatePickerFieldSurface(
    label: String,
    value: String,
    enabled: Boolean,
    clearEnabled: Boolean,
    clearLabel: String,
    openLabel: String,
    onOpen: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
): Unit {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.width(12.dp))

                TextButton(
                    onClick = onClear,
                    enabled = enabled && clearEnabled,
                ) {
                    Text(clearLabel)
                }

                OutlinedButton(
                    onClick = onOpen,
                    enabled = enabled,
                ) {
                    Text(openLabel)
                }
            }
        }
    }
}
