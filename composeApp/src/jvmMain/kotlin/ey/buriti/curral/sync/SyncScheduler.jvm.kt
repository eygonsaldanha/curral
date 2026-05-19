package ey.buriti.curral.sync

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

actual class SyncScheduler actual constructor(
    private val syncEngine: SyncEngine,
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var periodicJob: Job? = null

    actual fun schedulePeriodic() {
        periodicJob?.cancel()
        periodicJob = scope.launch {
            while (true) {
                runCatching { syncEngine.sync() }
                delay(15 * 60 * 1000L)
            }
        }
    }

    actual fun scheduleNow() {
        scope.launch { runCatching { syncEngine.sync() } }
    }

    actual fun cancel() {
        periodicJob?.cancel()
        periodicJob = null
    }
}
