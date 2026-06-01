package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IEventRepository
import ey.buriti.curral.data.IGestationRepository
import ey.buriti.curral.data.IGroupRepository
import ey.buriti.curral.sync.SyncEngine
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.model.AnimalType
import ey.buriti.curral.model.EventType
import ey.buriti.curral.model.Gestation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimaisViewModel(
    private val animalRepo: IAnimalRepository,
    private val groupRepo: IGroupRepository,
    private val eventRepo: IEventRepository,
    private val gestationRepo: IGestationRepository,
    private val syncEngine: SyncEngine,
) : ViewModel() {

    val animals: StateFlow<List<Animal>> = animalRepo.getAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val groups: StateFlow<List<AnimalGroup>> = groupRepo.getGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addAnimal(
        name: String, type: AnimalType, breed: String, status: AnimalStatus,
        sex: AnimalSex, tagNumber: String, birthDate: String, weightKg: Double,
    ) = viewModelScope.launch {
        val id = animalRepo.generateAnimalId()
        animalRepo.addAnimal(
            Animal(
                id = id, name = name, type = type, breed = breed,
                status = status, sex = sex, tagNumber = tagNumber,
                birthDate = birthDate, weightKg = weightKg,
            )
        )
        runCatching { syncEngine.sync() }
    }

    fun addAnimalFromForm(
        name: String,
        type: AnimalType,
        breed: String,
        status: AnimalStatus,
        sex: AnimalSex,
        tagNumber: String,
        birthDate: String,
        weightKg: Double,
        motherId: String? = null,
        fatherId: String? = null,
        onCreated: (String) -> Unit,
    ) = viewModelScope.launch {
        val id = animalRepo.generateAnimalId()
        animalRepo.addAnimal(
            Animal(
                id = id,
                name = name,
                type = type,
                breed = breed,
                status = status,
                sex = sex,
                tagNumber = tagNumber,
                birthDate = birthDate,
                weightKg = weightKg,
                motherId = motherId,
                fatherId = fatherId,
            )
        )
        onCreated(id)
        runCatching { syncEngine.sync() }
    }

    fun updateAnimal(animal: Animal) = viewModelScope.launch {
        animalRepo.updateAnimal(animal)
        runCatching { syncEngine.sync() }
    }

    fun deleteAnimal(id: String) = viewModelScope.launch {
        animalRepo.deleteAnimal(id)
        runCatching { syncEngine.sync() }
    }

    fun addGroup(name: String, description: String) = viewModelScope.launch {
        groupRepo.addGroup(name, description)
        runCatching { syncEngine.sync() }
    }

    fun updateGroup(group: AnimalGroup) = viewModelScope.launch {
        groupRepo.updateGroup(group)
        runCatching { syncEngine.sync() }
    }

    fun addAnimalToGroup(animalId: String, groupId: String) = viewModelScope.launch {
        groupRepo.addAnimalToGroup(animalId, groupId)
        runCatching { syncEngine.sync() }
    }

    fun removeAnimalFromGroup(animalId: String, groupId: String) = viewModelScope.launch {
        groupRepo.removeAnimalFromGroup(animalId, groupId)
        runCatching { syncEngine.sync() }
    }

    fun addEvent(
        type: EventType,
        date: String,
        time: String,
        notes: String,
        animalId: String,
        groupId: String? = null,
    ) = viewModelScope.launch {
        val id = eventRepo.generateEventId()
        eventRepo.addEvent(
            AnimalEvent(
                id = id,
                animalId = animalId,
                type = type,
                date = date,
                time = time,
                notes = notes,
                groupId = groupId,
            )
        )
        runCatching { syncEngine.sync() }
    }
}
