package ey.buriti.curral.data

import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.mapper.toDomain
import ey.buriti.curral.db.mapper.toEntity
import ey.buriti.curral.model.StockItem
import ey.buriti.curral.util.nowIso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class StockRepositoryImpl(
    private val db: CurralDatabase,
    private val farmIdProvider: () -> String,
    private val activityRecorder: ActivityRecorder,
) : IStockRepository {

    private val dao get() = db.stockItemDao()

    private fun farmId(): String = farmIdProvider()

    override fun getItems(): Flow<List<StockItem>> =
        dao.getAll(farmId()).map { list -> list.map { it.toDomain() } }

    override fun getItemById(id: String): Flow<StockItem?> =
        dao.getById(id).map { it?.toDomain() }

    override suspend fun addItem(item: StockItem) {
        dao.upsert(item.toEntity(farmId()))
        activityRecorder.record(ey.buriti.curral.model.ActivityType.ESTOQUE, ey.buriti.curral.model.ActivityAction.CRIADO, item.id, "Item adicionado ao estoque", "${item.quantity} ${item.unit}", item.name)
    }

    override suspend fun updateItem(item: StockItem) {
        dao.upsert(item.toEntity(farmId()))
        activityRecorder.record(ey.buriti.curral.model.ActivityType.ESTOQUE, ey.buriti.curral.model.ActivityAction.ATUALIZADO, item.id, "Item de estoque atualizado", subjectName = item.name)
    }

    override suspend fun deleteItem(id: String) {
        val item = dao.getById(id).first()?.toDomain()
        dao.softDelete(id, nowIso())
        activityRecorder.record(ey.buriti.curral.model.ActivityType.ESTOQUE, ey.buriti.curral.model.ActivityAction.EXCLUIDO, id, "Item removido do estoque", subjectName = item?.name.orEmpty())
    }

    override suspend fun incrementQuantity(id: String) {
        val entity = dao.getById(id).first() ?: return
        dao.upsert(entity.copy(quantity = entity.quantity + 1))
        activityRecorder.record(ey.buriti.curral.model.ActivityType.ESTOQUE, ey.buriti.curral.model.ActivityAction.INCREMENTADO, id, "Entrada no estoque", "+1 ${entity.unit}", entity.name)
    }

    override suspend fun decrementQuantity(id: String) {
        val entity = dao.getById(id).first() ?: return
        if (entity.quantity > 0) {
            dao.upsert(entity.copy(quantity = entity.quantity - 1))
            activityRecorder.record(ey.buriti.curral.model.ActivityType.ESTOQUE, ey.buriti.curral.model.ActivityAction.REDUZIDO, id, "Saída do estoque", "-1 ${entity.unit}", entity.name)
        }
    }

    override suspend fun generateId(): String = "s${nowIso().replace(":", "-")}"
}
