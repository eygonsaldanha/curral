package ey.buriti.curral

import ey.buriti.curral.ui.components.formatDateInput
import kotlin.test.Test
import kotlin.test.assertEquals

class FormInputUtilsTest {
    @Test
    fun formatsDateWhileTyping() {
        assertEquals("12/10/2026", formatDateInput("12102026"))
        assertEquals("12/10/2026", formatDateInput("12/10/2026"))
        assertEquals("12/1", formatDateInput("12a1"))
    }

    @Test
    fun limitsDateToEightDigits() {
        assertEquals("12/10/2026", formatDateInput("12102026123"))
    }
}
