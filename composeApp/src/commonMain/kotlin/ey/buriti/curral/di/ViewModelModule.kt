package ey.buriti.curral.di

import ey.buriti.curral.ui.viewmodel.AnimaisViewModel
import ey.buriti.curral.ui.viewmodel.AnimalDetailViewModel
import ey.buriti.curral.ui.viewmodel.AnimalGroupDetailViewModel
import ey.buriti.curral.ui.viewmodel.AuthViewModel
import ey.buriti.curral.ui.viewmodel.EditGestationViewModel
import ey.buriti.curral.ui.viewmodel.EstoqueViewModel
import ey.buriti.curral.ui.viewmodel.GestationResultViewModel
import ey.buriti.curral.ui.viewmodel.HomeViewModel
import ey.buriti.curral.ui.viewmodel.ManageAnimalGroupsViewModel
import ey.buriti.curral.ui.viewmodel.PerfilViewModel
import ey.buriti.curral.ui.viewmodel.ProducaoViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { AuthViewModel(get()) }
    factory { AnimaisViewModel(get(), get(), get(), get(), get()) }
    factory { (animalId: String) -> AnimalDetailViewModel(animalId, get(), get(), get(), get()) }
    factory { HomeViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { EstoqueViewModel(get(), get()) }
    factory { ProducaoViewModel(get(), get()) }
    factory { PerfilViewModel(get(), get(), get()) }
    factory { (groupId: String) -> AnimalGroupDetailViewModel(groupId, get(), get()) }
    factory { (animalId: String) -> ManageAnimalGroupsViewModel(animalId, get(), get()) }
    factory { (animalId: String) -> GestationResultViewModel(animalId, get(), get(), get(), get()) }
    factory { (animalId: String) -> EditGestationViewModel(animalId, get(), get(), get()) }
}
