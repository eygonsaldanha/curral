package ey.buriti.curral.data

import ey.buriti.curral.model.Gestation
import kotlinx.coroutines.flow.Flow

interface IGestationRepository {
    fun getAllGestations(): Flow<List<Gestation>>
    fun getGestationById(id: String): Flow<Gestation?>
    fun getGestationForAnimal(animalId: String): Flow<Gestation?>
    suspend fun addGestation(gestation: Gestation)
    suspend fun updateGestation(gestation: Gestation)
    suspend fun deleteGestation(id: String)
    suspend fun generateGestationId(): String
}
