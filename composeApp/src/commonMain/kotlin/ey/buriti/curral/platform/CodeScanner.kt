package ey.buriti.curral.platform

interface CodeScanner {
    suspend fun scan(): CodeScanResult
}

sealed interface CodeScanResult {
    data class Value(val rawValue: String) : CodeScanResult
    data object Cancelled : CodeScanResult
    data class Unavailable(val reason: String) : CodeScanResult
}
