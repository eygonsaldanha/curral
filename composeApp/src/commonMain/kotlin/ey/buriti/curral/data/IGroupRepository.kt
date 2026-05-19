package ey.buriti.curral.data

import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalGroup
import kotlinx.coroutines.flow.Flow

interface IGroupRepository {
    fun getGroups(): Flow<List<AnimalGroup>>
    fun getGroupById(id: String): Flow<AnimalGroup?>
    fun getGroupsForAnimal(animalId: String): Flow<List<AnimalGroup>>
    fun getAnimalsInGroup(groupId: String): Flow<List<Animal>>
    suspend fun addGroup(name: String, description: String): AnimalGroup
    suspend fun updateGroup(group: AnimalGroup)
    suspend fun deleteGroup(id: String)
    suspend fun addAnimalToGroup(animalId: String, groupId: String)
    suspend fun removeAnimalFromGroup(animalId: String, groupId: String)
    suspend fun generateGroupId(): String
}
