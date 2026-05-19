package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IProducaoRepository
import ey.buriti.curral.model.ProducaoEntry
import ey.buriti.curral.model.ProductType
import ey.buriti.curral.util.todayIso
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProducaoViewModel(
    private val producaoRepo: IProducaoRepository,
) : ViewModel() {

    val entries: StateFlow<List<ProducaoEntry>> = producaoRepo.getEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addEntry(
        productType: ProductType, quantity: Double, unit: String,
        date: String = todayIso(), notes: String = "",
    ) = viewModelScope.launch {
        val id = producaoRepo.generateId()
        producaoRepo.addEntry(
            ProducaoEntry(
                id = id, productType = productType,
                quantity = quantity, unit = unit, date = date, notes = notes,
            )
        )
    }

    fun deleteEntry(id: String) = viewModelScope.launch {
        producaoRepo.deleteEntry(id)
    }
}
