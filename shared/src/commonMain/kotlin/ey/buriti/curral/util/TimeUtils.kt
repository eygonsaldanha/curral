package ey.buriti.curral.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Retorna o timestamp atual em formato ISO-8601 (UTC). */
fun nowIso(): String = Clock.System.now().toString()

/** Retorna a data atual em formato "YYYY-MM-DD" no fuso local. */
fun todayIso(): String {
    val local = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.year}-${local.monthNumber.toString().padStart(2, '0')}-${local.dayOfMonth.toString().padStart(2, '0')}"
}
