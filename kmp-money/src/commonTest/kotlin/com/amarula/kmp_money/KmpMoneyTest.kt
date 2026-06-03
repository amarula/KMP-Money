package com.amarula.kmp_money

import kotlin.test.Test
import kotlin.test.assertEquals

class KmpMoneyTest {
    @Test
    fun greetingsReturnsExpectedMessage() {
        assertEquals("Hello from money", KmpMoney().greetings())
    }
}
