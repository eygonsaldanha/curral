package ey.buriti.curral.ui.viewmodel

import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.model.AnimalType
import ey.buriti.curral.model.EventType
import ey.buriti.curral.model.ProductType
import ey.buriti.curral.model.StockCategory

data class AnimalFormDraft(
    val name: String,
    val type: AnimalType?,
    val breed: String,
    val sex: AnimalSex?,
    val tagNumber: String,
    val birthDate: String,
    val weight: String,
    val status: AnimalStatus,
    val motherId: String?,
    val fatherId: String?,
    val step: Int,
)

data class ProductionFormDraft(
    val productType: ProductType?,
    val quantity: String,
    val unit: String,
    val date: String,
    val notes: String,
    val photoLabel: String?,
)

data class EventFormDraft(
    val title: String,
    val type: EventType?,
    val date: String,
    val time: String,
    val assignToAnimal: Boolean,
    val animalId: String?,
    val groupId: String?,
    val description: String,
    val photoLabel: String?,
)

data class StockFormDraft(
    val category: StockCategory?,
    val name: String,
    val code: String,
    val quantity: String,
    val unit: String,
    val expiryDate: String,
    val lowStockThreshold: String,
    val notes: String,
)

object FormDraftStore {
    var animal: AnimalFormDraft? = null
    var production: ProductionFormDraft? = null
    var event: EventFormDraft? = null
    var stock: StockFormDraft? = null
}
