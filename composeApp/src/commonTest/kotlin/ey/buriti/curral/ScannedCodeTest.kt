package ey.buriti.curral

import ey.buriti.curral.domain.ScannedCode
import ey.buriti.curral.domain.parseScannedCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ScannedCodeTest {
    @Test fun parsesAnimalUri() {
        val result = assertIs<ScannedCode.Animal>(parseScannedCode("curral://animal/a-123"))
        assertEquals("a-123", result.value)
    }

    @Test fun parsesStockUriIgnoringCase() {
        val result = assertIs<ScannedCode.Stock>(parseScannedCode("CURRAL://STOCK/s-9"))
        assertEquals("s-9", result.value)
    }

    @Test fun keepsPlainBarcode() {
        val result = assertIs<ScannedCode.Plain>(parseScannedCode(" 789123 "))
        assertEquals("789123", result.value)
    }
}
