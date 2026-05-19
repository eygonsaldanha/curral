package ey.buriti.curral.sync

/** Agenda e dispara o ciclo de sincronização em cada plataforma. */
expect class SyncScheduler(syncEngine: SyncEngine) {
    /** Agenda sync periódico (ex: WorkManager a cada 15 min). */
    fun schedulePeriodic()
    /** Dispara sync imediato (ex: ao detectar conectividade). */
    fun scheduleNow()
    /** Cancela o agendamento periódico. */
    fun cancel()
}
