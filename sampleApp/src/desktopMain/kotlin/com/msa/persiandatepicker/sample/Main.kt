package com.msa.persiandatepicker.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Persian DatePicker KMM Pro",
    ) {
        SampleApp()
    }
}
