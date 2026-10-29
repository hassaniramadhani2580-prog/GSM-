package com.example.gsm.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LogLevel {
    INFO, SUCCESS, WARNING, ERROR, COMMAND, RAW_HEX
}

data class GsmLogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel = LogLevel.INFO,
    val tag: String = "CORE",
    val message: String
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}

data class TestPointItem(
    val id: String,
    val brand: String,
    val model: String,
    val chipset: String,
    val mode: String, // EDL 9008 au BROM
    val pinoutInstruction: String,
    val resistorBypass: String,
    val caution: String
)

data class KnoxPackageInfo(
    val packageName: String,
    val name: String,
    val category: String, // Loan Lock, Knox Agent, MDM
    val adbDisableCommand: String,
    val description: String
)

data class AtCommandPreset(
    val command: String,
    val title: String,
    val description: String,
    val expectedResponse: String
)

data class FastbootCmdPreset(
    val command: String,
    val label: String,
    val description: String,
    val isDangerous: Boolean = false
)
