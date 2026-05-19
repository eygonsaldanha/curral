package ey.buriti.curral.data

import ey.buriti.curral.model.AnimalEvent
import kotlinx.coroutines.flow.Flow

interface IEventRepository {
    fun getEventsForAnimal(animalId: String): Flow<List<AnimalEvent>>
    fun getEventsForDay(date: String): Flow<List<AnimalEvent>>
    fun getAllEvents(): Flow<List<AnimalEvent>>
    suspend fun addEvent(event: AnimalEvent)
    suspend fun deleteEvent(id: String)
    suspend fun generateEventId(): String
}
