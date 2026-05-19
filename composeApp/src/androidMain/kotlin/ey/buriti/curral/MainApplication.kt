package ey.buriti.curral

import android.app.Application
import ey.buriti.curral.di.appModule
import ey.buriti.curral.sync.SyncScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class MainApplication : Application(), KoinComponent {

    private val syncScheduler: SyncScheduler by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }

        syncScheduler.schedulePeriodic()
    }
}
