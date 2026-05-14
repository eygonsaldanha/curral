package ey.buriti.curral.platform

import androidx.compose.runtime.Composable

data class PlatformDate(
    val year: Int,
    val month: Int,
    val day: Int,
) {
    fun toIsoDateString(): String = "${year.toString().padStart(4, '0')}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    fun toDisplayDateString(): String = "${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/${year.toString().padStart(4, '0')}"
}

@Composable
expect fun AppBackHandler(enabled: Boolean = true, onBack: () -> Unit)

@Composable
expect fun rememberExitApp(): () -> Unit

expect fun getCurrentDate(): PlatformDate
