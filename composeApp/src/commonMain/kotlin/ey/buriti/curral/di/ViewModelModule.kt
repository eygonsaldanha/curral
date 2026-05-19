package ey.buriti.curral.di

import ey.buriti.curral.ui.viewmodel.AnimaisViewModel
import ey.buriti.curral.ui.viewmodel.AnimalDetailViewModel
import ey.buriti.curral.ui.viewmodel.EstoqueViewModel
import ey.buriti.curral.ui.viewmodel.HomeViewModel
import ey.buriti.curral.ui.viewmodel.ProducaoViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {
    factory { AnimaisViewModel(get(), get(), get(), get()) }
    factory { (animalId: String) -> AnimalDetailViewModel(animalId, get(), get(), get()) }
    factory { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    factory { EstoqueViewModel(get()) }
    factory { ProducaoViewModel(get()) }
}
