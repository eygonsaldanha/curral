package ey.buriti.curral.domain

sealed interface ScannedCode {
    val value: String

    data class Animal(override val value: String) : ScannedCode
    data class Stock(override val value: String) : ScannedCode
    data class Plain(override val value: String) : ScannedCode
}

fun parseScannedCode(raw: String): ScannedCode {
    val value = raw.trim()
    return when {
        value.startsWith("curral://animal/", ignoreCase = true) ->
            ScannedCode.Animal(value.substringAfterLast('/'))
        value.startsWith("curral://stock/", ignoreCase = true) ->
            ScannedCode.Stock(value.substringAfterLast('/'))
        else -> ScannedCode.Plain(value)
    }
}
