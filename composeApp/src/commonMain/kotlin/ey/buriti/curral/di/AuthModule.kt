package ey.buriti.curral.di

import ey.buriti.curral.auth.IAuthRepository
import ey.buriti.curral.auth.SessionManager
import ey.buriti.curral.auth.SupabaseAuthRepository
import ey.buriti.curral.SUPABASE_URL
import ey.buriti.curral.SUPABASE_ANON_KEY
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import org.koin.dsl.module

val authModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = Constants.SUPABASE_URL,
            supabaseKey = Constants.SUPABASE_ANON_KEY,
        ) {
            install(Auth)
        }
    }
    single<IAuthRepository> { SupabaseAuthRepository(get()) }
    single { SessionManager(get()) }
}
