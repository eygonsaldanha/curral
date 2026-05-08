package ey.buriti.curral

import ey.buriti.curral.data.AnimalRepository
import ey.buriti.curral.data.GestationResultType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ComposeAppCommonTest {

    @Test
    fun saveWeightRecordUpdatesAnimalAndHistory() {
        AnimalRepository.saveWeightRecord(
            animalId = "a2",
            weightKg = 501.0,
            date = "2026-05-08",
            notes = "Pesagem de rotina",
        )

        val animal = AnimalRepository.getAnimal("a2")
        val latestWeight = AnimalRepository.getWeightHistory("a2").firstOrNull()

        assertNotNull(animal)
        assertTrue(animal.weightKg == 501.0)
        assertNotNull(latestWeight)
        assertTrue(latestWeight.weightKg == 501.0)
    }

    @Test
    fun addAndRemoveAnimalFromGroupKeepsMembershipInSync() {
        AnimalRepository.addAnimalToGroup(animalId = "a10", groupId = "g1")

        assertTrue(AnimalRepository.getGroup("g1")?.animalIds?.contains("a10") == true)
        assertTrue(AnimalRepository.getAnimal("a10")?.groupIds?.contains("g1") == true)

        AnimalRepository.removeAnimalFromGroup(animalId = "a10", groupId = "g1")

        assertFalse(AnimalRepository.getGroup("g1")?.animalIds?.contains("a10") == true)
        assertFalse(AnimalRepository.getAnimal("a10")?.groupIds?.contains("g1") == true)
    }

    @Test
    fun registerGestationResultClearsGestationAndCreatesEvent() {
        AnimalRepository.registerGestationResult(
            animalId = "a1",
            resultType = GestationResultType.PARTO_CONCLUIDO,
            date = "2026-05-08",
            notes = "Bezerro saudável",
        )

        assertNull(AnimalRepository.getAnimal("a1")?.gestationId)
        assertTrue(
            AnimalRepository.getEventsForAnimal("a1").any {
                it.date == "2026-05-08" && it.notes.contains("Parto concluído")
            },
        )
    }
}
