package ey.buriti.curral.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<CurralDatabase> {
    val appContext = context as Context
    return Room.databaseBuilder<CurralDatabase>(
        context = appContext,
        name = appContext.getDatabasePath("curral.db").absolutePath,
    )
}
