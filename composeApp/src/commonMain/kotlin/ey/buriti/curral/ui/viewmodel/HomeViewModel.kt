package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IAnimalRepository
import ey.buriti.curral.data.IEventRepository
import ey.buriti.curral.data.IGestationRepository
import ey.buriti.curral.data.IGroupRepository
import ey.buriti.curral.data.IProducaoRepository
import ey.buriti.curral.data.IStockRepository
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.model.Gestation
import ey.buriti.curral.model.ProducaoEntry
import ey.buriti.curral.model.StockItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    animalRepo: IAnimalRepository,
    groupRepo: IGroupRepository,
    eventRepo: IEventRepository,
    gestationRepo: IGestationRepository,
    producaoRepo: IProducaoRepository,
    stockRepo: IStockRepository,
) : ViewModel() {

    val animals: StateFlow<List<Animal>> = animalRepo.getAnimals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val groups: StateFlow<List<AnimalGroup>> = groupRepo.getGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val upcomingEvents: StateFlow<List<AnimalEvent>> = eventRepo.getAllEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activeGestations: StateFlow<List<Gestation>> = gestationRepo.getAllGestations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentProducao: StateFlow<List<ProducaoEntry>> = producaoRepo.getEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val lowStockItems: StateFlow<List<StockItem>> = stockRepo.getItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
