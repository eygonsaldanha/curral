package ey.buriti.curral.data

import androidx.compose.runtime.mutableStateListOf
import ey.buriti.curral.model.ProducaoEntry

object ProducaoRepository {
    private var nextId = 200

    val entries = mutableStateListOf<ProducaoEntry>()

    fun addEntry(entry: ProducaoEntry) {
        entries.add(entry)
    }

    fun generateId(): String = "prod${nextId++}"
}
