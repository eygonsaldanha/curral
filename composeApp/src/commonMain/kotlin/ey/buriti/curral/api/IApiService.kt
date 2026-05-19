package ey.buriti.curral.api

import ey.buriti.curral.dto.AnimalDto
import ey.buriti.curral.dto.AnimalEventDto
import ey.buriti.curral.dto.AnimalGroupDto
import ey.buriti.curral.dto.GestationDto
import ey.buriti.curral.dto.ProducaoEntryDto
import ey.buriti.curral.dto.StockItemDto
import ey.buriti.curral.dto.SyncPushPayload
import ey.buriti.curral.dto.SyncResponse

interface IApiService {
    suspend fun pushSync(payload: SyncPushPayload): SyncResponse
    suspend fun pullSync(since: String): SyncResponse

    suspend fun getAnimals(since: String? = null): List<AnimalDto>
    suspend fun upsertAnimal(dto: AnimalDto): AnimalDto
    suspend fun deleteAnimal(id: String)

    suspend fun getGroups(since: String? = null): List<AnimalGroupDto>
    suspend fun upsertGroup(dto: AnimalGroupDto): AnimalGroupDto
    suspend fun deleteGroup(id: String)

    suspend fun getEvents(since: String? = null): List<AnimalEventDto>
    suspend fun upsertEvent(dto: AnimalEventDto): AnimalEventDto
    suspend fun deleteEvent(id: String)

    suspend fun getGestations(since: String? = null): List<GestationDto>
    suspend fun upsertGestation(dto: GestationDto): GestationDto
    suspend fun deleteGestation(id: String)

    suspend fun getProducao(since: String? = null): List<ProducaoEntryDto>
    suspend fun upsertProducaoEntry(dto: ProducaoEntryDto): ProducaoEntryDto
    suspend fun deleteProducaoEntry(id: String)

    suspend fun getStock(since: String? = null): List<StockItemDto>
    suspend fun upsertStockItem(dto: StockItemDto): StockItemDto
    suspend fun deleteStockItem(id: String)
}
