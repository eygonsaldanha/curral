package ey.buriti.curral.di

import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.buildDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<CurralDatabase> { buildDatabase(get()) }
    single { get<CurralDatabase>().animalDao() }
    single { get<CurralDatabase>().animalGroupDao() }
    single { get<CurralDatabase>().animalEventDao() }
    single { get<CurralDatabase>().gestationDao() }
    single { get<CurralDatabase>().producaoEntryDao() }
    single { get<CurralDatabase>().stockItemDao() }
}
