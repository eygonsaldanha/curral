package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.model.*
import ey.buriti.curral.platform.getCurrentDate
import ey.buriti.curral.sync.SyncEngine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnimalFormUiState(
    val step: Int = 0,
    val name: String = "",
    val type: AnimalType? = null,
    val breed: String = "",
    val sex: AnimalSex? = null,
    val tagNumber: String = "",
    val birthDate: String = "",
    val weight: String = "",
    val status: AnimalStatus = AnimalStatus.SAUDAVEL,
    val motherId: String? = null,
    val fatherId: String? = null,
    val animals: List<Animal> = emptyList(),
    val errors: Map<String, String> = emptyMap(),
    val isEditing: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isDirty: Boolean = false,
)

class AnimalFormViewModel(
    private val animalId: String?,
    private val repository: IAnimalRepository,
    private val syncEngine: SyncEngine,
) : ViewModel() {
    private val _state = MutableStateFlow(AnimalFormUiState(isEditing = animalId != null))
    val state: StateFlow<AnimalFormUiState> = _state.asStateFlow()
    private var original: Animal? = null

    init {
        viewModelScope.launch {
            try {
                val animals = repository.getAnimals().first()
                original = animalId?.let { id -> animals.firstOrNull { it.id == id } }
                val animal = original
                _state.update {
                    if (animal == null) {
                        val draft = FormDraftStore.animal
                        if (draft == null) it.copy(animals = animals, isLoading = false)
                        else it.copy(
                            name = draft.name, type = draft.type, breed = draft.breed, sex = draft.sex,
                            tagNumber = draft.tagNumber, birthDate = draft.birthDate, weight = draft.weight,
                            status = draft.status, motherId = draft.motherId, fatherId = draft.fatherId,
                            step = draft.step, animals = animals, isLoading = false, isDirty = true,
                        )
                    }
                    else it.copy(
                        name = animal.name, type = animal.type, breed = animal.breed, sex = animal.sex,
                        tagNumber = animal.tagNumber, birthDate = animal.birthDate.toDisplayDate(),
                        weight = animal.weightKg.takeIf { value -> value > 0 }?.toString().orEmpty(),
                        status = animal.status, motherId = animal.motherId, fatherId = animal.fatherId,
                        animals = animals, isLoading = false,
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.toFormMessage("Não foi possível carregar o formulário."),
                    )
                }
            }
        }
    }

    fun updateName(value: String) = change("name") { copy(name = value) }
    fun updateType(value: AnimalType) = change("type") { copy(type = value) }
    fun updateBreed(value: String) = change("breed") { copy(breed = value) }
    fun updateSex(value: AnimalSex) = change("sex") {
        copy(sex = value, status = if (value == AnimalSex.MACHO && status == AnimalStatus.PRENHA) AnimalStatus.SAUDAVEL else status)
    }
    fun updateTag(value: String) = change("tag") { copy(tagNumber = value) }
    fun updateBirthDate(value: String) = change("birthDate") { copy(birthDate = value) }
    fun updateWeight(value: String) = change("weight") { copy(weight = value.filter { it.isDigit() || it == '.' || it == ',' }) }
    fun updateStatus(value: AnimalStatus) = change("status") { copy(status = value) }
    fun updateMother(value: String?) { _state.update { it.copy(motherId = value, isDirty = true) } }
    fun updateFather(value: String?) { _state.update { it.copy(fatherId = value, isDirty = true) } }
    fun previous() { _state.update { it.copy(step = (it.step - 1).coerceAtLeast(0), errors = emptyMap()) } }

    fun next(): Boolean {
        val errors = validate(_state.value.step)
        _state.update { it.copy(errors = errors, step = if (errors.isEmpty()) (it.step + 1).coerceAtMost(2) else it.step) }
        return errors.isEmpty()
    }

    fun save(onSaved: (String) -> Unit) {
        if (_state.value.isSaving) return
        val allErrors = validate(0) + validate(1)
        if (allErrors.isNotEmpty()) {
            val target = if (allErrors.keys.any { it in setOf("name", "type", "breed", "sex", "tag") }) 0 else 1
            _state.update { it.copy(step = target, errors = allErrors) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            var savedId: String? = null
            try {
                val s = _state.value
                val current = original
                val id = current?.id ?: repository.generateAnimalId()
                val animal = Animal(
                    id = id, name = s.name.trim(), type = requireNotNull(s.type), breed = s.breed.trim(),
                    status = s.status, sex = requireNotNull(s.sex), tagNumber = s.tagNumber.trim(),
                    birthDate = s.birthDate.toIsoDateOrCurrent(),
                    weightKg = s.weight.replace(',', '.').toDoubleOrNull() ?: 0.0,
                    groupIds = current?.groupIds.orEmpty(), motherId = s.motherId, fatherId = s.fatherId,
                    offspringIds = current?.offspringIds.orEmpty(), gestationId = current?.gestationId,
                )
                if (current == null) repository.addAnimal(animal) else repository.updateAnimal(animal)
                if (current == null) FormDraftStore.animal = null
                savedId = id
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.update {
                    it.copy(errorMessage = error.toFormMessage("Não foi possível salvar o animal."))
                }
            } finally {
                _state.update { it.copy(isSaving = false) }
            }

            savedId?.let { id ->
                onSaved(id)
                runCatching { syncEngine.sync() }
            }
        }
    }

    private fun validate(step: Int): Map<String, String> = buildMap {
        val s = _state.value
        if (step == 0) {
            if (s.name.isBlank()) put("name", "Informe o nome.")
            if (s.type == null) put("type", "Selecione o tipo.")
            if (s.breed.isBlank()) put("breed", "Informe a raça.")
            if (s.sex == null) put("sex", "Selecione o sexo.")
            if (s.tagNumber.isBlank()) put("tag", "Informe o número do brinco.")
        }
        if (step == 1) {
            if (s.birthDate.isNotBlank() && s.birthDate.toIsoDateOrNull() == null) put("birthDate", "Use o formato DD/MM/AAAA.")
            if (s.weight.isNotBlank() && s.weight.replace(',', '.').toDoubleOrNull() == null) put("weight", "Informe um peso válido.")
        }
    }

    private fun change(field: String, block: AnimalFormUiState.() -> AnimalFormUiState) {
        _state.update { state -> state.block().copy(errors = state.errors - field, isDirty = true) }
    }

    fun saveDraft() {
        if (animalId != null) return
        val s = _state.value
        FormDraftStore.animal = AnimalFormDraft(
            s.name, s.type, s.breed, s.sex, s.tagNumber, s.birthDate, s.weight,
            s.status, s.motherId, s.fatherId, s.step,
        )
    }

    fun clearDraft() {
        if (animalId == null) FormDraftStore.animal = null
    }
}

private fun Exception.toFormMessage(fallback: String): String {
    val detail = message?.takeIf { it.isNotBlank() }
    return if (detail == null) fallback else "$fallback $detail"
}

private fun String.toIsoDateOrNull(): String? {
    val p = trim().split("/")
    if (p.size != 3) return null
    val day = p[0].toIntOrNull() ?: return null
    val month = p[1].toIntOrNull() ?: return null
    val year = p[2].toIntOrNull() ?: return null
    if (day !in 1..31 || month !in 1..12 || year !in 1900..2200) return null
    return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
}

private fun String.toIsoDateOrCurrent(): String = ifBlank { null }?.toIsoDateOrNull()
    ?: getCurrentDate().toIsoDateString()

private fun String.toDisplayDate(): String {
    val p = split("-")
    return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else this
}
