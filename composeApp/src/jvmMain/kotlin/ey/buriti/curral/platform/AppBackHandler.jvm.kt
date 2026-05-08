package ey.buriti.curral.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalDate
import kotlin.system.exitProcess

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit

@Composable
actual fun rememberExitApp(): () -> Unit = remember { { exitProcess(0) } }

actual fun getCurrentDate(): PlatformDate {
    val today = LocalDate.now()
    return PlatformDate(
        year = today.year,
        month = today.monthValue,
        day = today.dayOfMonth,
    )
}
