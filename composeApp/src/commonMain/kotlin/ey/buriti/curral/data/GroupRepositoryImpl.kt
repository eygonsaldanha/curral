package ey.buriti.curral.data

import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.mapper.toDomain
import ey.buriti.curral.db.mapper.toEntity
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.util.nowIso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class GroupRepositoryImpl(
    private val db: CurralDatabase,
    private val farmId: String,
) : IGroupRepository {

    private val groupDao get() = db.animalGroupDao()
    private val animalDao get() = db.animalDao()

    override fun getGroups(): Flow<List<AnimalGroup>> =
        groupDao.getAll(farmId).map { list -> list.map { it.toDomain() } }

    override fun getGroupById(id: String): Flow<AnimalGroup?> =
        groupDao.getById(id).map { it?.toDomain() }

    override fun getGroupsForAnimal(animalId: String): Flow<List<AnimalGroup>> =
        groupDao.getAll(farmId).map { groups ->
            groups.filter { animalId in Json.decodeFromString<List<String>>(it.animalIdsJson) }
                .map { it.toDomain() }
        }

    override fun getAnimalsInGroup(groupId: String): Flow<List<Animal>> =
        animalDao.getAll(farmId).map { animals ->
            val group = groupDao.getById(groupId).first() ?: return@map emptyList()
            val ids = Json.decodeFromString<List<String>>(group.animalIdsJson).toSet()
            animals.filter { it.id in ids }.map { it.toDomain() }
        }

    override suspend fun addGroup(name: String, description: String): AnimalGroup {
        val group = AnimalGroup(
            id = "g${nowIso().replace(":", "-")}",
            name = name.trim(),
            description = description.trim(),
        )
        groupDao.upsert(group.toEntity(farmId))
        return group
    }

    override suspend fun updateGroup(group: AnimalGroup) {
        groupDao.upsert(group.toEntity(farmId))
    }

    override suspend fun deleteGroup(id: String) {
        groupDao.softDelete(id, nowIso())
    }

    override suspend fun addAnimalToGroup(animalId: String, groupId: String) {
        val groupEntity = groupDao.getById(groupId).first() ?: return
        val animalEntity = animalDao.getById(animalId).first() ?: return
        val groupIds = Json.decodeFromString<List<String>>(groupEntity.animalIdsJson)
        val animalGroupIds = Json.decodeFromString<List<String>>(animalEntity.groupIdsJson)
        if (animalId !in groupIds) {
            groupDao.upsert(groupEntity.copy(animalIdsJson = Json.encodeToString(groupIds + animalId)))
        }
        if (groupId !in animalGroupIds) {
            animalDao.upsert(animalEntity.copy(groupIdsJson = Json.encodeToString(animalGroupIds + groupId)))
        }
    }

    override suspend fun removeAnimalFromGroup(animalId: String, groupId: String) {
        val groupEntity = groupDao.getById(groupId).first() ?: return
        val animalEntity = animalDao.getById(animalId).first() ?: return
        val groupIds = Json.decodeFromString<List<String>>(groupEntity.animalIdsJson)
        val animalGroupIds = Json.decodeFromString<List<String>>(animalEntity.groupIdsJson)
        groupDao.upsert(groupEntity.copy(animalIdsJson = Json.encodeToString(groupIds - animalId)))
        animalDao.upsert(animalEntity.copy(groupIdsJson = Json.encodeToString(animalGroupIds - groupId)))
    }

    override suspend fun generateGroupId(): String = "g${nowIso().replace(":", "-")}"
}
