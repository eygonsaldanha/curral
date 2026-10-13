package ey.buriti.curral.db

import androidx.room.Database
import androidx.room.ConstructedBy
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import ey.buriti.curral.db.dao.AnimalDao
import ey.buriti.curral.db.dao.ActivityLogDao
import ey.buriti.curral.db.dao.AnimalEventDao
import ey.buriti.curral.db.dao.AnimalGroupDao
import ey.buriti.curral.db.dao.GestationDao
import ey.buriti.curral.db.dao.ProducaoEntryDao
import ey.buriti.curral.db.dao.StockItemDao
import ey.buriti.curral.db.entity.AnimalEntity
import ey.buriti.curral.db.entity.ActivityLogEntity
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
        ActivityLogEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
@ConstructedBy(CurralDatabaseConstructor::class)
abstract class CurralDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun animalGroupDao(): AnimalGroupDao
    abstract fun animalEventDao(): AnimalEventDao
    abstract fun gestationDao(): GestationDao
    abstract fun producaoEntryDao(): ProducaoEntryDao
    abstract fun stockItemDao(): StockItemDao
    abstract fun activityLogDao(): ActivityLogDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CurralDatabaseConstructor : RoomDatabaseConstructor<CurralDatabase> {
    override fun initialize(): CurralDatabase
}

/** Retorna o builder correto para cada plataforma via expect/actual. */
expect fun getDatabaseBuilder(context: Any? = null): RoomDatabase.Builder<CurralDatabase>

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE stock_items ADD COLUMN code TEXT")
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS activity_logs (
                id TEXT NOT NULL PRIMARY KEY,
                farmId TEXT NOT NULL,
                type TEXT NOT NULL,
                action TEXT NOT NULL,
                entityId TEXT,
                occurredAt TEXT NOT NULL,
                title TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                subjectName TEXT NOT NULL DEFAULT '',
                syncStatus TEXT NOT NULL DEFAULT 'PENDING',
                version INTEGER NOT NULL DEFAULT 0,
                updatedAt TEXT NOT NULL DEFAULT ''
            )
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT OR IGNORE INTO activity_logs
                (id, farmId, type, action, entityId, occurredAt, title, description, subjectName, syncStatus, version, updatedAt)
            SELECT 'import-animal-' || id, farmId, 'ANIMAL', 'IMPORTADO', id, updatedAt,
                'Animal importado', tagNumber, name, 'PENDING', 0, updatedAt
            FROM animals WHERE deletedAt IS NULL AND updatedAt <> ''
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT OR IGNORE INTO activity_logs
                (id, farmId, type, action, entityId, occurredAt, title, description, subjectName, syncStatus, version, updatedAt)
            SELECT 'import-stock-' || id, farmId, 'ESTOQUE', 'IMPORTADO', id, updatedAt,
                'Item de estoque importado', CAST(quantity AS TEXT) || ' ' || unit, name, 'PENDING', 0, updatedAt
            FROM stock_items WHERE deletedAt IS NULL AND updatedAt <> ''
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT OR IGNORE INTO activity_logs
                (id, farmId, type, action, entityId, occurredAt, title, description, subjectName, syncStatus, version, updatedAt)
            SELECT 'import-group-' || id, farmId, 'GRUPO', 'IMPORTADO', id, updatedAt,
                'Grupo importado', description, name, 'PENDING', 0, updatedAt
            FROM animal_groups WHERE deletedAt IS NULL AND updatedAt <> ''
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT OR IGNORE INTO activity_logs
                (id, farmId, type, action, entityId, occurredAt, title, description, subjectName, syncStatus, version, updatedAt)
            SELECT 'import-gestation-' || id, farmId, 'GESTACAO', 'IMPORTADO', animalId, updatedAt,
                'Gestação importada', expectedBirthDate, '', 'PENDING', 0, updatedAt
            FROM gestations WHERE deletedAt IS NULL AND updatedAt <> ''
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT OR IGNORE INTO activity_logs
                (id, farmId, type, action, entityId, occurredAt, title, description, subjectName, syncStatus, version, updatedAt)
            SELECT 'import-event-' || id, farmId, 'ANIMAL', 'IMPORTADO', animalId,
                date || CASE WHEN time = '' THEN 'T00:00:00Z' ELSE 'T' || time || ':00Z' END,
                'Evento importado', notes, '', 'PENDING', 0, updatedAt
            FROM animal_events WHERE deletedAt IS NULL
            """.trimIndent()
        )
        connection.execSQL(
            """
            INSERT OR IGNORE INTO activity_logs
                (id, farmId, type, action, entityId, occurredAt, title, description, subjectName, syncStatus, version, updatedAt)
            SELECT 'import-production-' || id, farmId, 'PRODUCAO', 'IMPORTADO', id,
                date || 'T00:00:00Z', 'Produção importada', CAST(quantity AS TEXT) || ' ' || unit,
                productType, 'PENDING', 0, updatedAt
            FROM producao_entries WHERE deletedAt IS NULL
            """.trimIndent()
        )
    }
}

fun buildDatabase(context: Any? = null): CurralDatabase =
    getDatabaseBuilder(context)
        .addMigrations(MIGRATION_1_2)
        .build()
