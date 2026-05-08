package ey.buriti.curral.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitDay
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSDate

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit

@Composable
actual fun rememberExitApp(): () -> Unit = remember { { } }

actual fun getCurrentDate(): PlatformDate {
    val components = NSCalendar.currentCalendar.components(
        NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
        fromDate = NSDate(),
    )
    return PlatformDate(
        year = components.year.toInt(),
        month = components.month.toInt(),
        day = components.day.toInt(),
    )
}
