package ey.buriti.curral.data

import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.mapper.toDomain
import ey.buriti.curral.db.mapper.toEntity
import ey.buriti.curral.model.Gestation
import ey.buriti.curral.util.nowIso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GestationRepositoryImpl(
    private val db: CurralDatabase,
    private val farmIdProvider: () -> String,
) : IGestationRepository {

    private val gestationDao get() = db.gestationDao()

    private fun farmId(): String = farmIdProvider()

    override fun getAllGestations(): Flow<List<Gestation>> =
        gestationDao.getAll(farmId()).map { list -> list.map { it.toDomain() } }

    override fun getGestationById(id: String): Flow<Gestation?> =
        gestationDao.getById(id).map { it?.toDomain() }

    override fun getGestationForAnimal(animalId: String): Flow<Gestation?> =
        gestationDao.getByAnimal(animalId).map { it?.toDomain() }

    override suspend fun addGestation(gestation: Gestation) {
        gestationDao.upsert(gestation.toEntity(farmId()))
    }

    override suspend fun updateGestation(gestation: Gestation) {
        gestationDao.upsert(gestation.toEntity(farmId()))
    }

    override suspend fun deleteGestation(id: String) {
        gestationDao.softDelete(id, nowIso())
    }

    override suspend fun generateGestationId(): String = "gest${nowIso().replace(":", "-")}"
}
