package ey.buriti.curral.db

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<CurralDatabase> {
    val dbPath = System.getProperty("user.home") + File.separator + ".curral" + File.separator + "curral.db"
    File(dbPath).parentFile?.mkdirs()
    return Room.databaseBuilder<CurralDatabase>(name = dbPath)
        .setDriver(BundledSQLiteDriver())
}
