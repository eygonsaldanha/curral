package ey.buriti.curral.data

import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.mapper.toDomain
import ey.buriti.curral.db.mapper.toEntity
import ey.buriti.curral.model.ProducaoEntry
import ey.buriti.curral.util.nowIso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProducaoRepositoryImpl(
    private val db: CurralDatabase,
    private val farmId: String,
) : IProducaoRepository {

    private val dao get() = db.producaoEntryDao()

    override fun getEntries(): Flow<List<ProducaoEntry>> =
        dao.getAll(farmId).map { list -> list.map { it.toDomain() } }

    override suspend fun addEntry(entry: ProducaoEntry) {
        dao.upsert(entry.toEntity(farmId))
    }

    override suspend fun deleteEntry(id: String) {
        dao.softDelete(id, nowIso())
    }

    override suspend fun generateId(): String = "prod${nowIso().replace(":", "-")}"
}
