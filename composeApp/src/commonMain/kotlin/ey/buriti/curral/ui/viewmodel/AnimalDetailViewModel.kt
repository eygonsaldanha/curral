package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IEventRepository
import ey.buriti.curral.data.IGestationRepository
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.Gestation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimalDetailViewModel(
    private val animalId: String,
    private val animalRepo: IAnimalRepository,
    private val eventRepo: IEventRepository,
    private val gestationRepo: IGestationRepository,
) : ViewModel() {

    val animal: StateFlow<Animal?> = animalRepo.getAnimalById(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val events: StateFlow<List<AnimalEvent>> = eventRepo.getEventsForAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val gestation: StateFlow<Gestation?> = gestationRepo.getGestationForAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun updateAnimal(animal: Animal) = viewModelScope.launch {
        animalRepo.updateAnimal(animal)
    }

    fun deleteAnimal() = viewModelScope.launch {
        animalRepo.deleteAnimal(animalId)
    }

    fun addEvent(event: AnimalEvent) = viewModelScope.launch {
        eventRepo.addEvent(event)
    }

    fun deleteEvent(eventId: String) = viewModelScope.launch {
        eventRepo.deleteEvent(eventId)
    }

    fun addGestation(gestation: Gestation) = viewModelScope.launch {
        gestationRepo.addGestation(gestation)
    }

    fun deleteGestation(gestationId: String) = viewModelScope.launch {
        gestationRepo.deleteGestation(gestationId)
    }
}
