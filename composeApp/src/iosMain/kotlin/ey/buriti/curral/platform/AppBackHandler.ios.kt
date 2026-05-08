package ey.buriti.curral.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) = Unit

@Composable
actual fun rememberExitApp(): () -> Unit = remember { { } }
