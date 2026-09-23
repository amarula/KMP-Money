package com.amarula.kmpMoney

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExchangeRateProviderTest {

    // ── convertTo(Currency, ExchangeRateProvider) ───────────────────────────────

    @Test
    fun `convertTo uses rate from provider`() {
        val provider = ExchangeRateProvider { _, _ -> BigDecimal.parseString("0.92") }
        val converted = KmpMoney.of("10.00", Currency.USD).convertTo(Currency.EUR, provider)
        assertEquals(Currency.EUR, converted.currency)
        assertEquals("9.20", converted.numberStrippedString)
    }

    @Test
    fun `convertTo passes source and target currency to provider`() {
        var seenFrom: Currency? = null
        var seenTo: Currency? = null
        val provider = ExchangeRateProvider { from, to ->
            seenFrom = from
            seenTo = to
            BigDecimal.ONE
        }
        KmpMoney.of("10.00", Currency.USD).convertTo(Currency.EUR, provider)
        assertEquals(Currency.USD, seenFrom)
        assertEquals(Currency.EUR, seenTo)
    }

    @Test
    fun `convertTo rounds to target currency decimal places`() {
        val provider = ExchangeRateProvider { _, _ -> BigDecimal.parseString("150.456") }
        val converted = KmpMoney.of("10.00", Currency.USD).convertTo(Currency.JPY, provider)
        assertEquals("1505", converted.numberStrippedString)
    }

    @Test
    fun `convertTo propagates exception thrown by provider`() {
        val provider = ExchangeRateProvider { _, _ -> error("no rate available") }
        assertFailsWith<IllegalStateException> {
            KmpMoney.of("10.00", Currency.USD).convertTo(Currency.EUR, provider)
        }
    }
}
