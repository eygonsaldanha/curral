package ey.buriti.curral.dto

import kotlinx.serialization.Serializable

/** Enviado pelo mobile ao servidor (push de pendências). */
@Serializable
data class SyncPushPayload(
    val animals: List<AnimalDto> = emptyList(),
    val groups: List<AnimalGroupDto> = emptyList(),
    val events: List<AnimalEventDto> = emptyList(),
    val gestations: List<GestationDto> = emptyList(),
    val producao: List<ProducaoEntryDto> = emptyList(),
    val stock: List<StockItemDto> = emptyList(),
)

/** Retornado pelo servidor após um push ou pull. */
@Serializable
data class SyncResponse(
    val animals: List<AnimalDto> = emptyList(),
    val groups: List<AnimalGroupDto> = emptyList(),
    val events: List<AnimalEventDto> = emptyList(),
    val gestations: List<GestationDto> = emptyList(),
    val producao: List<ProducaoEntryDto> = emptyList(),
    val stock: List<StockItemDto> = emptyList(),
    /** IDs que chegaram com versão em conflito (server-wins, mobile deve sobrescrever). */
    val conflicts: List<String> = emptyList(),
)
