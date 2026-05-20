package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IGestationRepository
import ey.buriti.curral.sync.SyncEngine
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.model.Gestation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditGestationViewModel(
    private val animalId: String,
    private val animalRepo: IAnimalRepository,
    private val gestationRepo: IGestationRepository,
    private val syncEngine: SyncEngine,
) : ViewModel() {

    val animal: StateFlow<Animal?> = animalRepo.getAnimalById(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val gestation: StateFlow<Gestation?> = gestationRepo.getGestationForAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val maleAnimals: StateFlow<List<Animal>> = animalRepo.getAnimals()
        .map { animals -> animals.filter { it.sex == AnimalSex.MACHO } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateGestation(gestation: Gestation) = viewModelScope.launch {
        gestationRepo.updateGestation(gestation)
        runCatching { syncEngine.sync() }
    }
}
