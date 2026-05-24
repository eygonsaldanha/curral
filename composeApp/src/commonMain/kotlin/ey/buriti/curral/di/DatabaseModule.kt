package ey.buriti.curral.di

import ey.buriti.curral.db.CurralDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun daoModule() = module {
    single { get<CurralDatabase>().animalDao() }
    single { get<CurralDatabase>().animalGroupDao() }
    single { get<CurralDatabase>().animalEventDao() }
    single { get<CurralDatabase>().gestationDao() }
    single { get<CurralDatabase>().producaoEntryDao() }
    single { get<CurralDatabase>().stockItemDao() }
}

expect val databaseModule: Module
