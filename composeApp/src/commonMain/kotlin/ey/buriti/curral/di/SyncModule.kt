package ey.buriti.curral.di

import ey.buriti.curral.auth.SessionManager
import ey.buriti.curral.sync.SyncEngine
import ey.buriti.curral.sync.SyncScheduler
import org.koin.dsl.module

val syncModule = module {
    single {
        SyncEngine(
            db = get(),
            api = get(),
            farmIdProvider = { get<SessionManager>().farmId },
        )
    }
    single { SyncScheduler(get()) }
}
