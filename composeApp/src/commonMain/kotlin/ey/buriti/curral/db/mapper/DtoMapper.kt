package ey.buriti.curral.db.mapper

import ey.buriti.curral.dto.AnimalDto
import ey.buriti.curral.dto.AnimalEventDto
import ey.buriti.curral.dto.AnimalGroupDto
import ey.buriti.curral.dto.GestationDto
import ey.buriti.curral.dto.ProducaoEntryDto
import ey.buriti.curral.dto.StockItemDto
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.model.AnimalType
import ey.buriti.curral.model.EventType
import ey.buriti.curral.model.Gestation
import ey.buriti.curral.model.ProducaoEntry
import ey.buriti.curral.model.ProductType
import ey.buriti.curral.model.StockCategory
import ey.buriti.curral.model.StockItem

fun AnimalDto.toDomain(): Animal = Animal(
    id = id,
    name = name,
    type = AnimalType.valueOf(type),
    breed = breed,
    status = AnimalStatus.valueOf(status),
    sex = AnimalSex.valueOf(sex),
    tagNumber = tagNumber,
    birthDate = birthDate,
    weightKg = weightKg,
    groupIds = groupIds,
    motherId = motherId,
    fatherId = fatherId,
    offspringIds = offspringIds,
    gestationId = gestationId,
)

fun AnimalGroupDto.toDomain(): AnimalGroup = AnimalGroup(
    id = id,
    name = name,
    description = description,
    animalIds = animalIds,
)

fun AnimalEventDto.toDomain(): AnimalEvent = AnimalEvent(
    id = id,
    animalId = animalId,
    type = EventType.valueOf(type),
    date = date,
    time = time,
    notes = notes,
    weightKg = weightKg,
    groupId = groupId,
)

fun GestationDto.toDomain(): Gestation = Gestation(
    id = id,
    animalId = animalId,
    startDate = startDate,
    expectedBirthDate = expectedBirthDate,
    notes = notes,
    fatherId = fatherId,
)

fun ProducaoEntryDto.toDomain(): ProducaoEntry = ProducaoEntry(
    id = id,
    productType = ProductType.valueOf(productType),
    quantity = quantity,
    unit = unit,
    date = date,
    notes = notes,
)

fun StockItemDto.toDomain(): StockItem = StockItem(
    id = id,
    name = name,
    category = StockCategory.valueOf(category),
    quantity = quantity,
    unit = unit,
    expiryDate = expiryDate,
    lowStockThreshold = lowStockThreshold,
)
