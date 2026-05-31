package com.zargroup.persiandatepicker.core

/** Controls how numbers are rendered in picker labels and formatted dates. */
public enum class DigitMode {
    Persian,
    Latin,
}

private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

/** Converts ASCII digits inside a string to Persian digits. */
public fun String.toPersianDigits(): String = buildString(length) {
    for (char in this@toPersianDigits) {
        append(if (char in '0'..'9') persianDigits[char - '0'] else char)
    }
}

/** Converts Persian and Arabic-Indic digits inside a string to ASCII digits. */
public fun String.toEnglishDigits(): String = buildString(length) {
    for (char in this@toEnglishDigits) {
        append(
            when (char) {
                '۰', '٠' -> '0'
                '۱', '١' -> '1'
                '۲', '٢' -> '2'
                '۳', '٣' -> '3'
                '۴', '٤' -> '4'
                '۵', '٥' -> '5'
                '۶', '٦' -> '6'
                '۷', '٧' -> '7'
                '۸', '٨' -> '8'
                '۹', '٩' -> '9'
                else -> char
            },
        )
    }
}

public fun Int.toDigitString(
    mode: DigitMode,
    minLength: Int = 1,
): String {
    val raw = toString().padStart(minLength, '0')
    return when (mode) {
        DigitMode.Persian -> raw.toPersianDigits()
        DigitMode.Latin -> raw
    }
}
