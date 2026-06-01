package com.msa.persiandatepicker.core

/** Result returned by strict Persian date parsing. */
public sealed interface PersianDateParseResult {
    public data class Success public constructor(public val date: PersianDate) : PersianDateParseResult
    public data object Blank : PersianDateParseResult
    public data class InvalidFormat public constructor(public val input: String) : PersianDateParseResult
    public data class InvalidDate public constructor(public val input: String, public val reason: String) : PersianDateParseResult
}

/** Utilities for parsing user-entered Jalali dates in a platform-independent way. */
public object PersianDateParser {
    private val separators = charArrayOf('/', '-', '.', ' ', '\\')

    public fun parse(value: String): PersianDateParseResult {
        val normalized = value.toEnglishDigits().trim()
        if (normalized.isBlank()) return PersianDateParseResult.Blank

        val parts = normalized
            .split(*separators)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (parts.size != 3) return PersianDateParseResult.InvalidFormat(value)

        val year = parts[0].toIntOrNull() ?: return PersianDateParseResult.InvalidFormat(value)
        val month = parts[1].toIntOrNull() ?: return PersianDateParseResult.InvalidFormat(value)
        val day = parts[2].toIntOrNull() ?: return PersianDateParseResult.InvalidFormat(value)

        return runCatching { PersianDate(year, month, day) }
            .fold(
                onSuccess = { PersianDateParseResult.Success(it) },
                onFailure = { PersianDateParseResult.InvalidDate(value, it.message.orEmpty()) },
            )
    }

    public fun parseOrNull(value: String): PersianDate? = when (val result = parse(value)) {
        is PersianDateParseResult.Success -> result.date
        PersianDateParseResult.Blank,
        is PersianDateParseResult.InvalidDate,
        is PersianDateParseResult.InvalidFormat -> null
    }
}

/** Stable key suitable for rememberSaveable, logs, deep links, and snapshot tests. */
public fun PersianDate.toStableKey(separator: String = "-"): String = buildString {
    append(year)
    append(separator)
    append(month.toString().padStart(2, '0'))
    append(separator)
    append(day.toString().padStart(2, '0'))
}

public fun PersianDateRange.toStableKey(separator: String = ".."): String =
    start.toStableKey() + separator + endInclusive.toStableKey()
