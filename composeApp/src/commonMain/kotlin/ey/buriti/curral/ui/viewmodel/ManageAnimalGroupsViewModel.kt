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

class ManageAnimalGroupsViewModel(
    private val animalId: String,
    private val groupRepo: IGroupRepository,
    private val animalRepo: IAnimalRepository,
) : ViewModel() {

    val animal: StateFlow<Animal?> = animalRepo.getAnimalById(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val currentGroups: StateFlow<List<AnimalGroup>> = groupRepo.getGroupsForAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allGroups: StateFlow<List<AnimalGroup>> = groupRepo.getGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addToGroup(groupId: String) = viewModelScope.launch {
        groupRepo.addAnimalToGroup(animalId, groupId)
    }

    fun removeFromGroup(groupId: String) = viewModelScope.launch {
        groupRepo.removeAnimalFromGroup(animalId, groupId)
    }

    fun getAnimalsInGroup(groupId: String) = groupRepo.getAnimalsInGroup(groupId)
}
