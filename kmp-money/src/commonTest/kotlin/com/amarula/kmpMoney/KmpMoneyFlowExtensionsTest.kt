package com.amarula.kmpMoney

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class KmpMoneyFlowExtensionsTest {

    // ── sumMoney() ────────────────────────────────────────────────────────────

    @Test
    fun `sumMoney returns sum of all items`() = runTest {
        val result = flowOf(
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("5.50", Currency.USD),
            KmpMoney.of("2.25", Currency.USD)
        ).sumMoney()
        assertEquals("17.75", result?.numberStrippedString)
    }

    @Test
    fun `sumMoney returns null for empty flow`() = runTest {
        val result = emptyFlow<KmpMoney>().sumMoney()
        assertNull(result)
    }

    @Test
    fun `sumMoney returns single item for single-element flow`() = runTest {
        val result = flowOf(KmpMoney.of("42.00", Currency.EUR)).sumMoney()
        assertEquals("42.00", result?.numberStrippedString)
        assertEquals(Currency.EUR, result?.currency)
    }

    @Test
    fun `sumMoney handles negative amounts`() = runTest {
        val result = flowOf(
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("-3.00", Currency.USD)
        ).sumMoney()
        assertEquals("7.00", result?.numberStrippedString)
    }

    @Test
    fun `sumMoney throws on currency mismatch`() = runTest {
        assertFailsWith<IllegalArgumentException> {
            flowOf(
                KmpMoney.of("10.00", Currency.USD),
                KmpMoney.of("5.00", Currency.EUR)
            ).sumMoney()
        }
    }

    // ── sumMoney(currency) ────────────────────────────────────────────────────

    @Test
    fun `sumMoney with currency returns zero for empty flow`() = runTest {
        val result = emptyFlow<KmpMoney>().sumMoney(Currency.USD)
        assertTrue(result.isZero())
        assertEquals(Currency.USD, result.currency)
    }

    @Test
    fun `sumMoney with currency returns sum for non-empty flow`() = runTest {
        val result = flowOf(
            KmpMoney.of("1.00", Currency.GBP),
            KmpMoney.of("2.00", Currency.GBP)
        ).sumMoney(Currency.GBP)
        assertEquals("3.00", result.numberStrippedString)
    }

    // ── totalByCurrency ───────────────────────────────────────────────────────

    @Test
    fun `totalByCurrency groups amounts by currency`() = runTest {
        val result = flowOf(
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("5.00", Currency.EUR),
            KmpMoney.of("3.00", Currency.USD),
            KmpMoney.of("2.00", Currency.EUR)
        ).totalByCurrency()

        assertEquals("13.00", result[Currency.USD]?.numberStrippedString)
        assertEquals("7.00", result[Currency.EUR]?.numberStrippedString)
    }

    @Test
    fun `totalByCurrency returns empty map for empty flow`() = runTest {
        val result = emptyFlow<KmpMoney>().totalByCurrency()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `totalByCurrency single currency has one entry`() = runTest {
        val result = flowOf(
            KmpMoney.of("1.00", Currency.GBP),
            KmpMoney.of("2.00", Currency.GBP)
        ).totalByCurrency()

        assertEquals(1, result.size)
        assertEquals("3.00", result[Currency.GBP]?.numberStrippedString)
    }

    @Test
    fun `totalByCurrency handles three distinct currencies`() = runTest {
        val result = flowOf(
            KmpMoney.of("1.00", Currency.USD),
            KmpMoney.of("2.00", Currency.EUR),
            KmpMoney.of("3.00", Currency.GBP)
        ).totalByCurrency()

        assertEquals(3, result.size)
        assertEquals("1.00", result[Currency.USD]?.numberStrippedString)
        assertEquals("2.00", result[Currency.EUR]?.numberStrippedString)
        assertEquals("3.00", result[Currency.GBP]?.numberStrippedString)
    }
}
