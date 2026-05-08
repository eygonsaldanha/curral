package ey.buriti.curral.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.model.AnimalType
import ey.buriti.curral.model.EventType
import ey.buriti.curral.model.Gestation

data class AgendaTask(
    val id: String,
    val date: String,
    val title: String,
    val notes: String = "",
)

enum class GestationResultType(val label: String, val eventType: EventType) {
    PARTO_CONCLUIDO("Parto concluído", EventType.NASCIMENTO_FILHOTE),
    PERDA_GESTACIONAL("Perda gestacional", EventType.OUTRO),
    ACOMPANHAMENTO("Acompanhamento finalizado", EventType.OUTRO),
}

object AnimalRepository {

    private var nextAnimalId = 100
    private var nextEventId = 100
    private var nextGroupId = 100

    val animals: SnapshotStateList<Animal> = mutableStateListOf<Animal>().also { list ->
        list.addAll(
            listOf(
                Animal(
                    id = "a1", name = "Mimosa", type = AnimalType.VACA, breed = "Nelore",
                    status = AnimalStatus.PRENHA, sex = AnimalSex.FEMEA, tagNumber = "0042",
                    birthDate = "2020-03-15", weightKg = 520.0,
                    groupIds = listOf("g1", "g2"), motherId = null, fatherId = "a3",
                    gestationId = "gest1",
                ),
                Animal(
                    id = "a2", name = "Beleza", type = AnimalType.VACA, breed = "Gir",
                    status = AnimalStatus.SAUDAVEL, sex = AnimalSex.FEMEA, tagNumber = "0043",
                    birthDate = "2019-07-20", weightKg = 490.0,
                    groupIds = listOf("g1"), motherId = null, fatherId = "a3",
                    offspringIds = listOf("a6"),
                ),
                Animal(
                    id = "a3", name = "Trovão", type = AnimalType.BOI, breed = "Angus",
                    status = AnimalStatus.SAUDAVEL, sex = AnimalSex.MACHO, tagNumber = "0010",
                    birthDate = "2018-01-10", weightKg = 780.0,
                    groupIds = listOf("g1"),
                ),
                Animal(
                    id = "a4", name = "Ventania", type = AnimalType.CAVALO, breed = "Quarto de Milha",
                    status = AnimalStatus.SAUDAVEL, sex = AnimalSex.FEMEA, tagNumber = "0021",
                    birthDate = "2021-05-08", weightKg = 460.0,
                    groupIds = listOf("g3"),
                ),
                Animal(
                    id = "a5", name = "Faísca", type = AnimalType.CAVALO, breed = "Mangalarga",
                    status = AnimalStatus.DOENTE, sex = AnimalSex.MACHO, tagNumber = "0022",
                    birthDate = "2020-11-03", weightKg = 500.0,
                    groupIds = listOf("g3"),
                ),
                Animal(
                    id = "a6", name = "Pintada", type = AnimalType.VACA, breed = "Gir",
                    status = AnimalStatus.SAUDAVEL, sex = AnimalSex.FEMEA, tagNumber = "0055",
                    birthDate = "2023-02-14", weightKg = 320.0,
                    groupIds = listOf("g1"), motherId = "a2", fatherId = "a3",
                ),
                Animal(
                    id = "a7", name = "Chico", type = AnimalType.BODE, breed = "Boer",
                    status = AnimalStatus.SAUDAVEL, sex = AnimalSex.MACHO, tagNumber = "0070",
                    birthDate = "2022-08-19", weightKg = 80.0,
                    groupIds = listOf("g4"),
                ),
                Animal(
                    id = "a8", name = "Caramela", type = AnimalType.GALINHA, breed = "Caipira",
                    status = AnimalStatus.SAUDAVEL, sex = AnimalSex.FEMEA, tagNumber = "G01",
                    birthDate = "2024-01-01", weightKg = 2.5,
                    groupIds = listOf("g5"),
                ),
                Animal(
                    id = "a9", name = "Preta", type = AnimalType.GALINHA, breed = "Caipira",
                    status = AnimalStatus.VENDIDO, sex = AnimalSex.FEMEA, tagNumber = "G02",
                    birthDate = "2024-01-01", weightKg = 2.3,
                    groupIds = listOf("g5"),
                ),
                Animal(
                    id = "a10", name = "Boitatá", type = AnimalType.BOI, breed = "Nelore",
                    status = AnimalStatus.MORTO, sex = AnimalSex.MACHO, tagNumber = "0011",
                    birthDate = "2017-06-12", weightKg = 0.0,
                    groupIds = emptyList(),
                ),
            ),
        )
    }

    val groups: SnapshotStateList<AnimalGroup> = mutableStateListOf<AnimalGroup>().also { list ->
        list.addAll(
            listOf(
                AnimalGroup(
                    id = "g1", name = "Rebanho Leiteiro",
                    description = "Vacas e bois destinados à produção de leite.",
                    animalIds = listOf("a1", "a2", "a3", "a6"),
                ),
                AnimalGroup(
                    id = "g2", name = "Gestantes",
                    description = "Animais em período de gestação.",
                    animalIds = listOf("a1"),
                ),
                AnimalGroup(
                    id = "g3", name = "Equinos",
                    description = "Cavalos para trabalho e passeio.",
                    animalIds = listOf("a4", "a5"),
                ),
                AnimalGroup(
                    id = "g4", name = "Caprinos",
                    description = "Bodes para produção de carne e queijo.",
                    animalIds = listOf("a7"),
                ),
                AnimalGroup(
                    id = "g5", name = "Galinhas",
                    description = "Galinhas caipiras para ovos e corte.",
                    animalIds = listOf("a8", "a9"),
                ),
            ),
        )
    }

    val events: SnapshotStateList<AnimalEvent> = mutableStateListOf<AnimalEvent>().also { list ->
        list.addAll(
            listOf(
                AnimalEvent(id = "e1", animalId = "a1", type = EventType.NASCIMENTO, date = "2020-03-15", notes = "Nascimento saudável."),
                AnimalEvent(id = "e2", animalId = "a1", type = EventType.VACINACAO, date = "2024-02-10", notes = "Vacina aftosa — lote 2024."),
                AnimalEvent(id = "e3", animalId = "a1", type = EventType.CONTROLE_PESO, date = "2025-01-05", notes = "Peso registrado.", weightKg = 510.0),
                AnimalEvent(id = "e4", animalId = "a1", type = EventType.CONTROLE_PESO, date = "2025-04-01", notes = "Peso registrado.", weightKg = 520.0),
                AnimalEvent(id = "e5", animalId = "a2", type = EventType.NASCIMENTO_FILHOTE, date = "2023-02-14", notes = "Filhote Pintada nascida saudável."),
                AnimalEvent(id = "e6", animalId = "a5", type = EventType.TRATAMENTO, date = "2026-04-20", notes = "Tratamento para cólica. Veterinário Dr. Matos."),
                AnimalEvent(id = "e7", animalId = "a5", type = EventType.ALIMENTACAO_ESPECIAL, date = "2026-04-21", notes = "Dieta especial — feno e suplemento vitamínico."),
                AnimalEvent(id = "e8", animalId = "", type = EventType.VISITA_VETERINARIA, date = "2026-05-09", time = "08:00", notes = "Consulta coletiva para o grupo Gestantes.", groupId = "g2"),
                AnimalEvent(id = "e9", animalId = "a2", type = EventType.VACINACAO, date = "2026-05-10", time = "10:30", notes = "Reforço da vacinação anual."),
            ),
        )
    }

    val gestations: SnapshotStateList<Gestation> = mutableStateListOf<Gestation>().also { list ->
        list.addAll(
            listOf(
                Gestation(
                    id = "gest1",
                    animalId = "a1",
                    startDate = "2026-01-10",
                    expectedBirthDate = "2026-10-20",
                    notes = "Prenhez confirmada por ultrassom.",
                    fatherId = "a3",
                ),
            ),
        )
    }

    val agendaTasks: SnapshotStateList<AgendaTask> = mutableStateListOf<AgendaTask>().also { list ->
        list.addAll(
            listOf(
                AgendaTask(id = "t1", date = "2026-05-08", title = "Conferir estoque de ração", notes = "Revisar se precisa nova compra."),
                AgendaTask(id = "t2", date = "2026-05-09", title = "Separar animais prenhes", notes = "Revisar o piquete antes da visita do veterinário."),
                AgendaTask(id = "t3", date = "2026-05-10", title = "Limpar bebedouros", notes = "Fazer antes da vacinação."),
            ),
        )
    }

    fun addAnimal(animal: Animal) {
        animals.add(animal)
    }

    fun updateAnimal(updatedAnimal: Animal) {
        val index = animals.indexOfFirst { it.id == updatedAnimal.id }
        if (index >= 0) {
            animals[index] = updatedAnimal
        }
    }

    fun generateAnimalId(): String = "a${nextAnimalId++}"

    fun addEvent(event: AnimalEvent) {
        events.add(event)
    }

    fun generateEventId(): String = "e${nextEventId++}"

    fun addGroup(name: String, description: String): AnimalGroup {
        val group = AnimalGroup(
            id = "g${nextGroupId++}",
            name = name.trim(),
            description = description.trim(),
        )
        groups.add(group)
        return group
    }

    fun updateGroup(updatedGroup: AnimalGroup) {
        val index = groups.indexOfFirst { it.id == updatedGroup.id }
        if (index >= 0) {
            groups[index] = updatedGroup
        }
    }

    fun addAnimalToGroup(animalId: String, groupId: String) {
        val animal = getAnimal(animalId) ?: return
        val group = getGroup(groupId) ?: return
        if (animalId !in group.animalIds) {
            updateGroup(group.copy(animalIds = group.animalIds + animalId))
        }
        if (groupId !in animal.groupIds) {
            updateAnimal(animal.copy(groupIds = animal.groupIds + groupId))
        }
    }

    fun removeAnimalFromGroup(animalId: String, groupId: String) {
        val animal = getAnimal(animalId) ?: return
        val group = getGroup(groupId) ?: return
        if (animalId in group.animalIds) {
            updateGroup(group.copy(animalIds = group.animalIds - animalId))
        }
        if (groupId in animal.groupIds) {
            updateAnimal(animal.copy(groupIds = animal.groupIds - groupId))
        }
    }

    fun saveWeightRecord(animalId: String, weightKg: Double, date: String, notes: String) {
        val animal = getAnimal(animalId) ?: return
        updateAnimal(animal.copy(weightKg = weightKg))
        addEvent(
            AnimalEvent(
                id = generateEventId(),
                animalId = animalId,
                type = EventType.CONTROLE_PESO,
                date = date,
                notes = notes.ifBlank { "Peso registrado." },
                weightKg = weightKg,
            ),
        )
    }

    fun updateGestation(updatedGestation: Gestation) {
        val index = gestations.indexOfFirst { it.id == updatedGestation.id }
        if (index >= 0) {
            gestations[index] = updatedGestation
        }
    }

    fun registerGestationResult(animalId: String, resultType: GestationResultType, date: String, notes: String) {
        val animal = getAnimal(animalId) ?: return
        val summary = listOf("Resultado da gestação: ${resultType.label}", notes.trim())
            .filter { it.isNotBlank() }
            .joinToString(" — ")

        addEvent(
            AnimalEvent(
                id = generateEventId(),
                animalId = animalId,
                type = resultType.eventType,
                date = date,
                notes = summary,
            ),
        )

        animal.gestationId?.let { gestationId ->
            gestations.removeAll { it.id == gestationId }
        }
        updateAnimal(animal.copy(gestationId = null, status = AnimalStatus.SAUDAVEL))
    }

    fun getAnimal(id: String) = animals.find { it.id == id }

    fun getGroup(id: String) = groups.find { it.id == id }

    fun getEventsForAnimal(animalId: String): List<AnimalEvent> = events
        .filter { it.animalId == animalId || (it.groupId != null && groupContainsAnimal(it.groupId, animalId)) }
        .sortedWith(compareByDescending<AnimalEvent> { it.date }.thenByDescending { it.time })

    fun getEventsForDay(date: String): List<AnimalEvent> = events
        .filter { it.date == date }
        .sortedBy { it.time.ifBlank { "99:99" } }

    fun getTasksForDay(date: String): List<AgendaTask> = agendaTasks.filter { it.date == date }

    fun getGestation(id: String) = gestations.find { it.id == id }

    fun getAnimalsInGroup(groupId: String): List<Animal> {
        val group = getGroup(groupId) ?: return emptyList()
        return animals.filter { it.id in group.animalIds }
    }

    fun getGroupsForAnimal(animalId: String): List<AnimalGroup> = groups.filter { animalId in it.animalIds }

    fun getAvailableGroupsForAnimal(animalId: String): List<AnimalGroup> = groups.filter { animalId !in it.animalIds }

    fun getAnimalsOutsideGroup(groupId: String): List<Animal> {
        val group = getGroup(groupId) ?: return emptyList()
        return animals.filter { it.id !in group.animalIds }
    }

    fun getWeightHistory(animalId: String): List<AnimalEvent> = getEventsForAnimal(animalId)
        .filter { it.type == EventType.CONTROLE_PESO && it.weightKg != null }
        .sortedByDescending { it.date }

    private fun groupContainsAnimal(groupId: String, animalId: String): Boolean {
        return getGroup(groupId)?.animalIds?.contains(animalId) == true
    }
}
