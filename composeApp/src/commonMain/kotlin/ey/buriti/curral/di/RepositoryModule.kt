package ey.buriti.curral.di

import ey.buriti.curral.data.*
import ey.buriti.curral.auth.SessionManager
import org.koin.dsl.module

val repositoryModule = module {
    single<IAnimalRepository> { AnimalRepositoryImpl(get()) { get<SessionManager>().farmId } }
    single<IGroupRepository> { GroupRepositoryImpl(get()) { get<SessionManager>().farmId } }
    single<IEventRepository> { EventRepositoryImpl(get()) { get<SessionManager>().farmId } }
    single<IGestationRepository> { GestationRepositoryImpl(get()) { get<SessionManager>().farmId } }
    single<IProducaoRepository> { ProducaoRepositoryImpl(get()) { get<SessionManager>().farmId } }
    single<IStockRepository> { StockRepositoryImpl(get()) { get<SessionManager>().farmId } }
}
