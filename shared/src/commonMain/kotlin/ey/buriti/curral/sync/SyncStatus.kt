package ey.buriti.curral.sync

enum class SyncStatus {
    /** Criado/editado localmente, ainda não enviado ao servidor. */
    PENDING,
    /** Em sincronia com o servidor. */
    SYNCED,
    /** Conflito detectado (versão do servidor diverge). */
    CONFLICT,
    /** Marcado para deleção (soft-delete pendente de sync). */
    DELETED,
}
