package ey.buriti.curral.model

data class ActivityLog(
    val id: String,
    val type: ActivityType,
    val action: ActivityAction,
    val entityId: String?,
    val occurredAt: String,
    val title: String,
    val description: String = "",
    val subjectName: String = "",
)

enum class ActivityType(val label: String) {
    ANIMAL("Animais"),
    PRODUCAO("Produção"),
    ESTOQUE("Estoque"),
    GESTACAO("Gestação"),
    GRUPO("Grupos"),
}

enum class ActivityAction {
    CRIADO,
    ATUALIZADO,
    EXCLUIDO,
    INCREMENTADO,
    REDUZIDO,
    EVENTO,
    IMPORTADO,
}
