package ey.buriti.curral.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ey.buriti.curral.db.dao.AnimalDao
import ey.buriti.curral.db.dao.AnimalEventDao
import ey.buriti.curral.db.dao.AnimalGroupDao
import ey.buriti.curral.db.dao.GestationDao
import ey.buriti.curral.db.dao.ProducaoEntryDao
import ey.buriti.curral.db.dao.StockItemDao
import ey.buriti.curral.db.entity.AnimalEntity
import ey.buriti.curral.db.entity.AnimalEventEntity
import ey.buriti.curral.db.entity.AnimalGroupEntity
import ey.buriti.curral.db.entity.GestationEntity
import ey.buriti.curral.db.entity.ProducaoEntryEntity
import ey.buriti.curral.db.entity.StockItemEntity

@Database(
    entities = [
        AnimalEntity::class,
        AnimalGroupEntity::class,
        AnimalEventEntity::class,
        GestationEntity::class,
        ProducaoEntryEntity::class,
        StockItemEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class CurralDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun animalGroupDao(): AnimalGroupDao
    abstract fun animalEventDao(): AnimalEventDao
    abstract fun gestationDao(): GestationDao
    abstract fun producaoEntryDao(): ProducaoEntryDao
    abstract fun stockItemDao(): StockItemDao
}

/** Retorna o builder correto para cada plataforma via expect/actual. */
expect fun getDatabaseBuilder(context: Any? = null): RoomDatabase.Builder<CurralDatabase>

fun buildDatabase(context: Any? = null): CurralDatabase =
    getDatabaseBuilder(context)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
