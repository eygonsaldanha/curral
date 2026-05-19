package ey.buriti.curral.data

import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.mapper.toDomain
import ey.buriti.curral.db.mapper.toEntity
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.util.nowIso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepositoryImpl(
    private val db: CurralDatabase,
    private val farmId: String,
) : IEventRepository {

    private val eventDao get() = db.animalEventDao()

    override fun getEventsForAnimal(animalId: String): Flow<List<AnimalEvent>> =
        eventDao.getByAnimal(animalId, farmId).map { list -> list.map { it.toDomain() } }

    override fun getEventsForDay(date: String): Flow<List<AnimalEvent>> =
        eventDao.getByDate(date, farmId).map { list -> list.map { it.toDomain() } }

    override fun getAllEvents(): Flow<List<AnimalEvent>> =
        eventDao.getAll(farmId).map { list -> list.map { it.toDomain() } }

    override suspend fun addEvent(event: AnimalEvent) {
        eventDao.upsert(event.toEntity(farmId))
    }

    override suspend fun deleteEvent(id: String) {
        eventDao.softDelete(id, nowIso())
    }

    override suspend fun generateEventId(): String = "e${nowIso().replace(":", "-")}"
}
