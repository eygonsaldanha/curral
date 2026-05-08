package ey.buriti.curral.ui.screens

internal fun formatDate(date: String): String {
    return try {
        val parts = date.split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } catch (_: Exception) {
        date
    }
}

internal fun String.toIsoDate(): String {
    val parts = trim().split("/")
    return if (parts.size == 3) {
        "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
    } else {
        this
    }
}
