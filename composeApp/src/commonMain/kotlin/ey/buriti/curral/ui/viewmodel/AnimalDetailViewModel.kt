package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IEventRepository
import ey.buriti.curral.data.IGestationRepository
import ey.buriti.curral.sync.SyncEngine
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.EventType
import ey.buriti.curral.model.Gestation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimalDetailViewModel(
    private val animalId: String,
    private val animalRepo: IAnimalRepository,
    private val eventRepo: IEventRepository,
    private val gestationRepo: IGestationRepository,
    private val syncEngine: SyncEngine,
) : ViewModel() {

    val animal: StateFlow<Animal?> = animalRepo.getAnimalById(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val events: StateFlow<List<AnimalEvent>> = eventRepo.getEventsForAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val gestation: StateFlow<Gestation?> = gestationRepo.getGestationForAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val groups: StateFlow<List<AnimalGroup>> = animalRepo.getGroupsForAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val weightHistory: StateFlow<List<AnimalEvent>> = eventRepo.getEventsForAnimal(animalId)
        .map { evts -> evts.filter { it.type == EventType.CONTROLE_PESO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allAnimals: StateFlow<List<Animal>> = animalRepo.getAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateAnimal(animal: Animal) = viewModelScope.launch {
        animalRepo.updateAnimal(animal)
        runCatching { syncEngine.sync() }
    }

    fun deleteAnimal() = viewModelScope.launch {
        animalRepo.deleteAnimal(animalId)
        runCatching { syncEngine.sync() }
    }

    fun addEvent(event: AnimalEvent) = viewModelScope.launch {
        eventRepo.addEvent(event)
        runCatching { syncEngine.sync() }
    }

    fun saveWeightRecord(weightKg: Double, date: String, notes: String) = viewModelScope.launch {
        val currentAnimal = animal.value ?: return@launch
        animalRepo.updateAnimal(currentAnimal.copy(weightKg = weightKg))
        eventRepo.addEvent(
            AnimalEvent(
                id = eventRepo.generateEventId(),
                animalId = animalId,
                type = EventType.CONTROLE_PESO,
                date = date,
                notes = notes.ifBlank { "Peso registrado." },
                weightKg = weightKg,
            )
        )
        runCatching { syncEngine.sync() }
    }

    fun deleteEvent(eventId: String) = viewModelScope.launch {
        eventRepo.deleteEvent(eventId)
        runCatching { syncEngine.sync() }
    }

    fun addGestation(gestation: Gestation) = viewModelScope.launch {
        gestationRepo.addGestation(gestation)
        runCatching { syncEngine.sync() }
    }

    fun deleteGestation(gestationId: String) = viewModelScope.launch {
        gestationRepo.deleteGestation(gestationId)
        runCatching { syncEngine.sync() }
    }
}
