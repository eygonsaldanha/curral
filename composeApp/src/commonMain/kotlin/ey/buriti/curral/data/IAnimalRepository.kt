package ey.buriti.curral.data

import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.Gestation
import kotlinx.coroutines.flow.Flow

interface IAnimalRepository {
    fun getAnimals(): Flow<List<Animal>>
    fun getAnimalById(id: String): Flow<Animal?>
    suspend fun addAnimal(animal: Animal)
    suspend fun updateAnimal(animal: Animal)
    suspend fun deleteAnimal(id: String)
    fun getGroupsForAnimal(animalId: String): Flow<List<AnimalGroup>>
    fun getEventsForAnimal(animalId: String): Flow<List<AnimalEvent>>
    fun getGestationForAnimal(animalId: String): Flow<Gestation?>
    suspend fun generateAnimalId(): String
}
