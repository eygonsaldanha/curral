package ey.buriti.curral.di

import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.buildDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val databaseModule: Module = module {
    single<CurralDatabase> { buildDatabase() }
    includes(daoModule())
}
