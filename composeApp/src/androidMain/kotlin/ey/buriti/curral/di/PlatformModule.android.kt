package ey.buriti.curral.di

import ey.buriti.curral.platform.CodeScanner
import ey.buriti.curral.platform.GoogleCodeScanner
import ey.buriti.curral.platform.ApiConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { ApiConfig(baseUrl = "http://10.0.2.2:8080") }
    single<CodeScanner> { GoogleCodeScanner(androidContext()) }
}
