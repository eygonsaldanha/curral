package ey.buriti.curral.ui.screens

import ey.buriti.curral.platform.PlatformDate

internal fun formatDate(date: String): String {
    val parts = date.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else date
}

internal fun String.toIsoDateOrNull(): String? {
    val parts = trim().split("/")
    return if (parts.size == 3) {
        val day = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val year = parts[2].toIntOrNull() ?: return null
        if (day !in 1..31 || month !in 1..12 || year < 1900) {
            null
        } else {
            PlatformDate(year = year, month = month, day = day).toIsoDateString()
        }
    } else {
        null
    }
}
