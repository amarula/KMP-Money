package com.amarula.kmp_money

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KmpMoneyCollectionExtensionsTest {

    // ── List.sum() ────────────────────────────────────────────────────────────

    @Test
    fun `sum returns total of all items`() {
        val list = listOf(
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("5.50", Currency.USD),
            KmpMoney.of("2.25", Currency.USD)
        )
        assertEquals("17.75", list.sum()?.numberStrippedString)
    }

    @Test
    fun `sum returns null for empty list`() {
        assertNull(emptyList<KmpMoney>().sum())
    }

    @Test
    fun `sum returns single item for one-element list`() {
        assertEquals("42.00", listOf(KmpMoney.of("42.00", Currency.EUR)).sum()?.numberStrippedString)
    }

    @Test
    fun `sum throws on currency mismatch`() {
        assertFailsWith<IllegalArgumentException> {
            listOf(
                KmpMoney.of("1.00", Currency.USD),
                KmpMoney.of("1.00", Currency.EUR)
            ).sum()
        }
    }

    // ── List.sum(currency) ────────────────────────────────────────────────────

    @Test
    fun `sum with currency returns zero for empty list`() {
        val result = emptyList<KmpMoney>().sum(Currency.USD)
        assertTrue(result.isZero())
        assertEquals(Currency.USD, result.currency)
    }

    @Test
    fun `sum with currency returns total for non-empty list`() {
        val result = listOf(
            KmpMoney.of("3.00", Currency.GBP),
            KmpMoney.of("2.00", Currency.GBP)
        ).sum(Currency.GBP)
        assertEquals("5.00", result.numberStrippedString)
    }

    // ── List.max() ────────────────────────────────────────────────────────────

    @Test
    fun `max returns the largest amount`() {
        val list = listOf(
            KmpMoney.of("3.00", Currency.USD),
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("1.00", Currency.USD)
        )
        assertEquals("10.00", list.max()?.numberStrippedString)
    }

    @Test
    fun `max returns null for empty list`() {
        assertNull(emptyList<KmpMoney>().max())
    }

    @Test
    fun `max returns only element for single-item list`() {
        assertEquals("5.00", listOf(KmpMoney.of("5.00", Currency.USD)).max()?.numberStrippedString)
    }

    // ── List.min() ────────────────────────────────────────────────────────────

    @Test
    fun `min returns the smallest amount`() {
        val list = listOf(
            KmpMoney.of("3.00", Currency.USD),
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("1.00", Currency.USD)
        )
        assertEquals("1.00", list.min()?.numberStrippedString)
    }

    @Test
    fun `min returns null for empty list`() {
        assertNull(emptyList<KmpMoney>().min())
    }

    @Test
    fun `min returns only element for single-item list`() {
        assertEquals("5.00", listOf(KmpMoney.of("5.00", Currency.USD)).min()?.numberStrippedString)
    }

    // ── List.average() ────────────────────────────────────────────────────────

    @Test
    fun `average returns mean of amounts`() {
        val list = listOf(
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("20.00", Currency.USD),
            KmpMoney.of("30.00", Currency.USD)
        )
        assertEquals("20.00", list.average()?.numberStrippedString)
    }

    @Test
    fun `average rounds to currency scale`() {
        val list = listOf(
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("10.00", Currency.USD),
            KmpMoney.of("10.01", Currency.USD)
        )
        assertEquals("10.00", list.average()?.numberStrippedString)
    }

    @Test
    fun `average returns null for empty list`() {
        assertNull(emptyList<KmpMoney>().average())
    }

    @Test
    fun `average of single item returns that item`() {
        assertEquals("7.50", listOf(KmpMoney.of("7.50", Currency.USD)).average()?.numberStrippedString)
    }

    // ── Collection.sumMoneyOf ─────────────────────────────────────────────────

    @Test
    fun `sumMoneyOf maps and sums correctly`() {
        data class Item(val price: KmpMoney, val qty: Int)
        val items = listOf(
            Item(KmpMoney.of("10.00", Currency.USD), 2),
            Item(KmpMoney.of("5.00", Currency.USD), 3)
        )
        val result = items.sumMoneyOf { it.price * it.qty }
        assertEquals("35.00", result?.numberStrippedString)
    }

    @Test
    fun `sumMoneyOf returns null for empty collection`() {
        assertNull(emptyList<KmpMoney>().sumMoneyOf { it })
    }
}
