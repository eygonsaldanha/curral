package ey.buriti.curral.platform

import androidx.compose.runtime.Composable

@Composable
expect fun AppBackHandler(enabled: Boolean = true, onBack: () -> Unit)

@Composable
expect fun rememberExitApp(): () -> Unit
