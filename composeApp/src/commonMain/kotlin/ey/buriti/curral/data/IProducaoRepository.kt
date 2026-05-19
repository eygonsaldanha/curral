package ey.buriti.curral.data

import ey.buriti.curral.model.ProducaoEntry
import kotlinx.coroutines.flow.Flow

interface IProducaoRepository {
    fun getEntries(): Flow<List<ProducaoEntry>>
    suspend fun addEntry(entry: ProducaoEntry)
    suspend fun deleteEntry(id: String)
    suspend fun generateId(): String
}
