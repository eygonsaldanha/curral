package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IGroupRepository
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnimalGroupDetailViewModel(
    private val groupId: String,
    private val groupRepo: IGroupRepository,
    private val animalRepo: IAnimalRepository,
) : ViewModel() {

    val group: StateFlow<AnimalGroup?> = groupRepo.getGroupById(groupId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val animals: StateFlow<List<Animal>> = groupRepo.getAnimalsInGroup(groupId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allAnimals: StateFlow<List<Animal>> = animalRepo.getAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addAnimalToGroup(animalId: String) = viewModelScope.launch {
        groupRepo.addAnimalToGroup(animalId, groupId)
    }

    fun removeAnimalFromGroup(animalId: String) = viewModelScope.launch {
        groupRepo.removeAnimalFromGroup(animalId, groupId)
    }
}
