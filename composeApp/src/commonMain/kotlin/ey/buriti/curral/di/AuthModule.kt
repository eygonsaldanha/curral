package ey.buriti.curral.di

import ey.buriti.curral.auth.IAuthRepository
import ey.buriti.curral.auth.SessionManager
import ey.buriti.curral.auth.SupabaseAuthRepository
import org.koin.dsl.module

val authModule = module {
    single<IAuthRepository> { SupabaseAuthRepository() }
    single { SessionManager(get()) }
}
