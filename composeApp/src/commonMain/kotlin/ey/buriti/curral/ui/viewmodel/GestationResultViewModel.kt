package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.GestationResultType
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IEventRepository
import ey.buriti.curral.data.IGestationRepository
import ey.buriti.curral.sync.SyncEngine
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.model.Gestation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GestationResultViewModel(
    private val animalId: String,
    private val animalRepo: IAnimalRepository,
    private val gestationRepo: IGestationRepository,
    private val eventRepo: IEventRepository,
    private val syncEngine: SyncEngine,
) : ViewModel() {

    val animal: StateFlow<Animal?> = animalRepo.getAnimalById(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val gestation: StateFlow<Gestation?> = gestationRepo.getGestationForAnimal(animalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun registerResult(resultType: GestationResultType, date: String, notes: String, onDone: () -> Unit) = viewModelScope.launch {
        val currentAnimal = animal.value ?: return@launch
        val currentGestation = gestation.value ?: return@launch
        val summary = if (notes.isBlank()) {
            "Resultado da gestação: ${resultType.label}"
        } else {
            "Resultado da gestação: ${resultType.label} — ${notes.trim()}"
        }

        gestationRepo.deleteGestation(currentGestation.id)
        animalRepo.updateAnimal(currentAnimal.copy(gestationId = null, status = AnimalStatus.SAUDAVEL))

        val eventId = eventRepo.generateEventId()
        eventRepo.addEvent(
            AnimalEvent(
                id = eventId,
                animalId = animalId,
                type = resultType.eventType,
                date = date,
                time = "",
                notes = summary,
                weightKg = null,
                groupId = null,
            )
        )
        runCatching { syncEngine.sync() }
        onDone()
    }
}
