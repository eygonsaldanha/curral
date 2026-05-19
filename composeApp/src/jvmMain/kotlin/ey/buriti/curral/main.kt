package ey.buriti.curral

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ey.buriti.curral.di.appModule
import org.koin.core.context.startKoin

fun main() {
    startKoin { modules(appModule) }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "curral",
        ) {
            App()
        }
    }
}