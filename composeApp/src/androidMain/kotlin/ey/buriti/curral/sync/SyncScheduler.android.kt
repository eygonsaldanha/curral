package ey.buriti.curral.sync

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class SyncScheduler actual constructor(
    private val syncEngine: SyncEngine,
) : KoinComponent {
    private val context: Context by inject()

    actual fun schedulePeriodic() {
        SyncWorker.schedule(context)
    }

    actual fun scheduleNow() {
        SyncWorker.scheduleNow(context)
    }

    actual fun cancel() {
        androidx.work.WorkManager.getInstance(context).cancelAllWorkByTag("curral_periodic_sync")
    }
}
