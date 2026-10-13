package ey.buriti.curral.di

import ey.buriti.curral.platform.CodeScanResult
import ey.buriti.curral.platform.CodeScanner
import ey.buriti.curral.platform.ApiConfig
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { ApiConfig(baseUrl = "http://127.0.0.1:8080") }
    single<CodeScanner> { object : CodeScanner {
        override suspend fun scan() = CodeScanResult.Unavailable("Use a entrada manual no Desktop.")
    } }
}
