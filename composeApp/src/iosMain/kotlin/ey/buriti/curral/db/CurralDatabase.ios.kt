package ey.buriti.curral.db

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<CurralDatabase> {
    val dbPath = NSHomeDirectory() + "/Documents/curral.db"
    return Room.databaseBuilder<CurralDatabase>(name = dbPath)
}
