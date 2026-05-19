package ey.buriti.curral.di

import org.koin.core.module.Module
import org.koin.dsl.module

val appModule: List<Module> = listOf(
    databaseModule,
    authModule,
    repositoryModule,
    apiModule,
    syncModule,
    viewModelModule,
)
