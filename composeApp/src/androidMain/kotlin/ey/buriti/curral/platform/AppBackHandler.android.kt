package ey.buriti.curral.platform

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}

@Composable
actual fun rememberExitApp(): () -> Unit {
    val activity = LocalContext.current as? Activity
    return remember(activity) { { activity?.finish() } }
}

actual fun getCurrentDate(): PlatformDate {
    val today = LocalDate.now()
    return PlatformDate(
        year = today.year,
        month = today.monthValue,
        day = today.dayOfMonth,
    )
}
