package com.amarula.kmp_money

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KmpMoneyTest {

    // ── Factory: of(String, Currency) ─────────────────────────────────────────

    @Test
    fun `of string currency parses integer string`() {
        assertEquals("10.00", KmpMoney.of("10", Currency.USD).numberStrippedString)
    }

    @Test
    fun `of string currency parses decimal string`() {
        assertEquals("19.99", KmpMoney.of("19.99", Currency.USD).numberStrippedString)
    }

    @Test
    fun `of string currency stores the given currency`() {
        assertEquals(Currency.EUR, KmpMoney.of("5.00", Currency.EUR).currency)
    }

    // ── Factory: of(Number, Currency) ─────────────────────────────────────────

    @Test
    fun `of number currency accepts Int`() {
        assertEquals("42.00", KmpMoney.of(42, Currency.USD).numberStrippedString)
    }

    @Test
    fun `of number currency preserves Long precision beyond double range`() {
        // 2^53 + 1 cannot be represented exactly as a Double; toDouble() would silently truncate it
        assertEquals(
            "9007199254740993.00",
            KmpMoney.of(9007199254740993L, Currency.USD).numberStrippedString
        )
    }

    // ── Factory: of(String, String) ───────────────────────────────────────────

    @Test
    fun `of string string resolves currency by name`() {
        val m = KmpMoney.of("5.00", "USD")
        assertEquals(Currency.USD, m.currency)
        assertEquals("5.00", m.numberStrippedString)
    }

    @Test
    fun `of string string is case-insensitive`() {
        assertEquals(Currency.EUR, KmpMoney.of("1.00", "eur").currency)
    }

    @Test
    fun `of string string falls back to UNKNOWN for unrecognised code`() {
        assertEquals(Currency.UNKNOWN, KmpMoney.of("1.00", "XYZ").currency)
    }

    // ── Factory: of(Number, String) ───────────────────────────────────────────

    @Test
    fun `of number string resolves currency and preserves precision`() {
        val m = KmpMoney.of(9007199254740993L, "USD")
        assertEquals(Currency.USD, m.currency)
        assertEquals("9007199254740993.00", m.numberStrippedString)
    }

    @Test
    fun `of number string falls back to UNKNOWN for unrecognised code`() {
        assertEquals(Currency.UNKNOWN, KmpMoney.of(1, "XYZ").currency)
    }

    // ── add ───────────────────────────────────────────────────────────────────

    @Test
    fun `add returns sum of two amounts`() {
        val a = KmpMoney.of("10.00", Currency.USD)
        val b = KmpMoney.of("5.50", Currency.USD)
        assertEquals("15.50", a.add(b).numberStrippedString)
    }

    @Test
    fun `add works with zero`() {
        val m = KmpMoney.of("7.25", Currency.USD)
        assertEquals("7.25", m.add(KmpMoney.of("0", Currency.USD)).numberStrippedString)
    }

    @Test
    fun `add works with negative amounts`() {
        val a = KmpMoney.of("10.00", Currency.USD)
        val b = KmpMoney.of("-3.00", Currency.USD)
        assertEquals("7.00", a.add(b).numberStrippedString)
    }

    @Test
    fun `add preserves currency`() {
        val result = KmpMoney.of("1.00", Currency.GBP).add(KmpMoney.of("2.00", Currency.GBP))
        assertEquals(Currency.GBP, result.currency)
    }

    @Test
    fun `add throws on currency mismatch`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("1.00", Currency.USD).add(KmpMoney.of("1.00", Currency.EUR))
        }
    }

    // ── numberStrippedString ──────────────────────────────────────────────────

    @Test
    fun `numberStrippedString rounds positive half-value away from zero`() {
        assertEquals("10.56", KmpMoney.of("10.555", Currency.USD).numberStrippedString)
    }

    @Test
    fun `numberStrippedString rounds negative half-value away from zero`() {
        // ROUND_HALF_CEILING would give -10.55; ROUND_HALF_AWAY_FROM_ZERO gives -10.56
        assertEquals("-10.56", KmpMoney.of("-10.555", Currency.USD).numberStrippedString)
    }

    @Test
    fun `numberStrippedString pads to currency decimal places`() {
        assertEquals("5.00", KmpMoney.of("5", Currency.USD).numberStrippedString)
    }

    @Test
    fun `numberStrippedString respects zero-decimal currency`() {
        assertEquals("1500", KmpMoney.of("1500", Currency.JPY).numberStrippedString)
    }

    @Test
    fun `numberStrippedString respects three-decimal currency`() {
        assertEquals("1.500", KmpMoney.of("1.5", Currency.BHD).numberStrippedString)
    }

    // ── numberStripped ────────────────────────────────────────────────────────

    @Test
    fun `numberStripped returns BigDecimal rounded to currency scale`() {
        val stripped = KmpMoney.of("10.555", Currency.USD).numberStripped
        assertEquals(BigDecimal.parseString("10.56"), stripped)
    }

    // ── number ────────────────────────────────────────────────────────────────

    @Test
    fun `number returns raw unrounded amount`() {
        val bd = BigDecimal.parseString("10.555")
        assertEquals(bd, KmpMoney.of("10.555", Currency.USD).number)
    }

    // ── isNegativeOrZero ──────────────────────────────────────────────────────

    @Test
    fun `isNegativeOrZero true for negative amount`() {
        assertTrue(KmpMoney.of("-0.01", Currency.USD).isNegativeOrZero())
    }

    @Test
    fun `isNegativeOrZero true for zero`() {
        assertTrue(KmpMoney.of("0", Currency.USD).isNegativeOrZero())
    }

    @Test
    fun `isNegativeOrZero false for positive amount`() {
        assertFalse(KmpMoney.of("0.01", Currency.USD).isNegativeOrZero())
    }

    // ── compareTo ─────────────────────────────────────────────────────────────

    @Test
    fun `compareTo returns negative when less than other`() {
        val a = KmpMoney.of("1.00", Currency.USD)
        val b = KmpMoney.of("2.00", Currency.USD)
        assertTrue(a.compareTo(b) < 0)
    }

    @Test
    fun `compareTo returns zero for equal amounts`() {
        val a = KmpMoney.of("5.00", Currency.USD)
        val b = KmpMoney.of("5.00", Currency.USD)
        assertEquals(0, a.compareTo(b))
    }

    @Test
    fun `compareTo returns positive when greater than other`() {
        val a = KmpMoney.of("3.00", Currency.USD)
        val b = KmpMoney.of("1.00", Currency.USD)
        assertTrue(a.compareTo(b) > 0)
    }

    @Test
    fun `compareTo enables sorting`() {
        val list = listOf(
            KmpMoney.of("3.00", Currency.USD),
            KmpMoney.of("1.00", Currency.USD),
            KmpMoney.of("2.00", Currency.USD)
        ).sorted()
        assertEquals("1.00", list[0].numberStrippedString)
        assertEquals("2.00", list[1].numberStrippedString)
        assertEquals("3.00", list[2].numberStrippedString)
    }

    @Test
    fun `compareTo throws on currency mismatch`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("1.00", Currency.USD).compareTo(KmpMoney.of("1.00", Currency.EUR))
        }
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    fun `toString contains currency name`() {
        assertTrue(KmpMoney.of("10.50", Currency.USD).toString().contains("USD"))
    }

    // ── toMoneyString ─────────────────────────────────────────────────────────

    @Test
    fun `toMoneyString uses symbol not ISO code for prefix currency`() {
        // Before fix: produced "USD 1,234.56"
        assertEquals("$ 1,234.56", KmpMoney.of("1234.56", Currency.USD).toMoneyString())
    }

    @Test
    fun `toMoneyString places symbol as suffix`() {
        assertEquals("1,234.56 Kč", KmpMoney.of("1234.56", Currency.CZK).toMoneyString())
    }

    @Test
    fun `toMoneyString formats zero-decimal currency without fraction`() {
        assertEquals("¥ 1,500", KmpMoney.of("1500", Currency.JPY).toMoneyString())
    }

    @Test
    fun `toMoneyString formats three-decimal currency`() {
        // BHD has an empty currencySymbol at this point, so it falls back to the enum name
        assertEquals("BHD 1,234.500", KmpMoney.of("1234.5", Currency.BHD).toMoneyString())
    }

    @Test
    fun `toMoneyString with dot groupingSeparator`() {
        assertEquals(
            "$ 1.234.567.89",
            KmpMoney.of("1234567.89", Currency.USD).toMoneyString(groupingSeparator = '.')
        )
    }

    @Test
    fun `toMoneyString with space groupingSeparator`() {
        assertEquals(
            "$ 1 234 567.89",
            KmpMoney.of("1234567.89", Currency.USD).toMoneyString(groupingSeparator = ' ')
        )
    }

    @Test
    fun `toMoneyString formats negative amount correctly`() {
        // Before fix: 7-digit negative produced "-,123,456.78"
        assertEquals("$ -1,234,567.89", KmpMoney.of("-1234567.89", Currency.USD).toMoneyString())
    }

    @Test
    fun `toMoneyString formats small negative amount correctly`() {
        assertEquals("$ -99.50", KmpMoney.of("-99.50", Currency.USD).toMoneyString())
    }

    @Test
    fun `toMoneyString formats zero`() {
        assertEquals("$ 0.00", KmpMoney.of("0", Currency.USD).toMoneyString())
    }
}
