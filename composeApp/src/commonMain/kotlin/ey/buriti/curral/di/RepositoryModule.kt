package ey.buriti.curral.di

import ey.buriti.curral.data.*
import ey.buriti.curral.auth.SessionManager
import org.koin.dsl.module

val repositoryModule = module {
    single { ActivityRecorder(get()) { get<SessionManager>().farmId } }
    single<IActivityRepository> { ActivityRepositoryImpl(get()) }
    single<IAnimalRepository> { AnimalRepositoryImpl(get(), { get<SessionManager>().farmId }, get()) }
    single<IGroupRepository> { GroupRepositoryImpl(get(), { get<SessionManager>().farmId }, get()) }
    single<IEventRepository> { EventRepositoryImpl(get(), { get<SessionManager>().farmId }, get()) }
    single<IGestationRepository> { GestationRepositoryImpl(get(), { get<SessionManager>().farmId }, get()) }
    single<IProducaoRepository> { ProducaoRepositoryImpl(get(), { get<SessionManager>().farmId }, get()) }
    single<IStockRepository> { StockRepositoryImpl(get(), { get<SessionManager>().farmId }, get()) }
}
