package ey.buriti.curral.data

import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.mapper.toDomain
import ey.buriti.curral.db.mapper.toEntity
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.Gestation
import ey.buriti.curral.util.nowIso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class AnimalRepositoryImpl(
    private val db: CurralDatabase,
    private val farmIdProvider: () -> String,
) : IAnimalRepository {

    private val animalDao get() = db.animalDao()
    private val groupDao get() = db.animalGroupDao()
    private val eventDao get() = db.animalEventDao()
    private val gestationDao get() = db.gestationDao()

    private fun farmId(): String = farmIdProvider()

    override fun getAnimals(): Flow<List<Animal>> =
        animalDao.getAll(farmId()).map { list -> list.map { it.toDomain() } }

    override fun getAnimalById(id: String): Flow<Animal?> =
        animalDao.getById(id).map { it?.toDomain() }

    override suspend fun addAnimal(animal: Animal) {
        animalDao.upsert(animal.toEntity(farmId()))
    }

    override suspend fun updateAnimal(animal: Animal) {
        animalDao.upsert(animal.toEntity(farmId()))
    }

    override suspend fun deleteAnimal(id: String) {
        animalDao.softDelete(id, nowIso())
    }

    override fun getGroupsForAnimal(animalId: String): Flow<List<AnimalGroup>> =
        groupDao.getAll(farmId()).map { groups ->
            groups.filter { group ->
                val ids = Json.decodeFromString<List<String>>(group.animalIdsJson)
                animalId in ids
            }.map { it.toDomain() }
        }

    override fun getEventsForAnimal(animalId: String): Flow<List<AnimalEvent>> =
        eventDao.getByAnimal(animalId, farmId()).map { list -> list.map { it.toDomain() } }

    override fun getGestationForAnimal(animalId: String): Flow<Gestation?> =
        gestationDao.getByAnimal(animalId).map { it?.toDomain() }

    override suspend fun generateAnimalId(): String = "a${nowIso().replace(":", "-")}"
}
