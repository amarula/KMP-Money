package com.amarula.kmpMoney

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
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

    // ── subtract ──────────────────────────────────────────────────────────────

    @Test
    fun `subtract returns difference`() {
        val a = KmpMoney.of("10.00", Currency.USD)
        val b = KmpMoney.of("3.25", Currency.USD)
        assertEquals("6.75", a.subtract(b).numberStrippedString)
    }

    @Test
    fun `subtract with negative result`() {
        assertEquals(
            "-2.00",
            KmpMoney.of("3.00", Currency.USD)
                .subtract(KmpMoney.of("5.00", Currency.USD)).numberStrippedString
        )
    }

    @Test
    fun `subtract throws on currency mismatch`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("5.00", Currency.USD).subtract(KmpMoney.of("1.00", Currency.EUR))
        }
    }

    // ── multiply ──────────────────────────────────────────────────────────────

    @Test
    fun `multiply by Int`() {
        assertEquals("30.00", KmpMoney.of("10.00", Currency.USD).multiply(3).numberStrippedString)
    }

    @Test
    fun `multiply by Double`() {
        assertEquals("15.00", KmpMoney.of("10.00", Currency.USD).multiply(1.5).numberStrippedString)
    }

    @Test
    fun `multiply by BigDecimal`() {
        assertEquals(
            "15.00",
            KmpMoney.of("10.00", Currency.USD)
                .multiply(BigDecimal.parseString("1.5")).numberStrippedString
        )
    }

    @Test
    fun `multiply by zero gives zero`() {
        assertTrue(KmpMoney.of("99.99", Currency.USD).multiply(0).isNegativeOrZero())
    }

    @Test
    fun `multiply preserves currency`() {
        assertEquals(Currency.GBP, KmpMoney.of("10.00", Currency.GBP).multiply(2).currency)
    }

    // ── divide ────────────────────────────────────────────────────────────────

    @Test
    fun `divide by Int rounds to currency scale`() {
        assertEquals("3.33", KmpMoney.of("10.00", Currency.USD).divide(3).numberStrippedString)
    }

    @Test
    fun `divide by BigDecimal`() {
        assertEquals(
            "5.00",
            KmpMoney.of("10.00", Currency.USD)
                .divide(BigDecimal.parseString("2")).numberStrippedString
        )
    }

    @Test
    fun `divide with explicit CEILING rounding`() {
        assertEquals(
            "3.34",
            KmpMoney.of("10.00", Currency.USD).divide(3, RoundingMode.CEILING).numberStrippedString
        )
    }

    @Test
    fun `divide preserves currency`() {
        assertEquals(Currency.EUR, KmpMoney.of("9.00", Currency.EUR).divide(3).currency)
    }

    // ── negate ────────────────────────────────────────────────────────────────

    @Test
    fun `negate flips positive to negative`() {
        assertEquals("-10.00", KmpMoney.of("10.00", Currency.USD).negate().numberStrippedString)
    }

    @Test
    fun `negate flips negative to positive`() {
        assertEquals("5.00", KmpMoney.of("-5.00", Currency.USD).negate().numberStrippedString)
    }

    @Test
    fun `negate of zero stays zero`() {
        assertTrue(KmpMoney.of("0", Currency.USD).negate().isNegativeOrZero())
    }

    // ── abs ───────────────────────────────────────────────────────────────────

    @Test
    fun `abs of negative returns positive`() {
        assertEquals("7.50", KmpMoney.of("-7.50", Currency.USD).abs().numberStrippedString)
    }

    @Test
    fun `abs of positive is unchanged`() {
        assertEquals("7.50", KmpMoney.of("7.50", Currency.USD).abs().numberStrippedString)
    }

    // ── remainder ─────────────────────────────────────────────────────────────

    @Test
    fun `remainder BigDecimal returns modulo`() {
        assertEquals(
            "1.00",
            KmpMoney.of("10.00", Currency.USD)
                .remainder(BigDecimal.parseString("3")).numberStrippedString
        )
    }

    @Test
    fun `remainder Number overload`() {
        assertEquals("1.50", KmpMoney.of("10.50", Currency.USD).remainder(3).numberStrippedString)
    }

    // ── allocate ──────────────────────────────────────────────────────────────

    @Test
    fun `allocate splits evenly`() {
        val parts = KmpMoney.of("10.00", Currency.USD).allocate(listOf(1, 2, 1))
        assertEquals("2.50", parts[0].numberStrippedString)
        assertEquals("5.00", parts[1].numberStrippedString)
        assertEquals("2.50", parts[2].numberStrippedString)
    }

    @Test
    fun `allocate loses no pennies on uneven split`() {
        val total = KmpMoney.of("10.01", Currency.USD)
        val parts = total.allocate(listOf(1, 1, 1))
        val minorSum = parts.sumOf {
            it.number.multiply(BigDecimal.parseString("100")).longValue(exactRequired = false)
        }
        assertEquals(1001L, minorSum)
        // First slot gets the extra penny
        assertEquals("3.34", parts[0].numberStrippedString)
        assertEquals("3.34", parts[1].numberStrippedString)
        assertEquals("3.33", parts[2].numberStrippedString)
    }

    @Test
    fun `allocate handles negative total`() {
        val parts = KmpMoney.of("-9.00", Currency.USD).allocate(listOf(1, 2))
        assertEquals("-3.00", parts[0].numberStrippedString)
        assertEquals("-6.00", parts[1].numberStrippedString)
    }

    @Test
    fun `allocate throws on empty ratios`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("10.00", Currency.USD).allocate(emptyList())
        }
    }

    @Test
    fun `allocate throws on negative ratio`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("10.00", Currency.USD).allocate(listOf(1, -1))
        }
    }

    @Test
    fun `allocate throws when all ratios sum to zero`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("10.00", Currency.USD).allocate(listOf(0, 0))
        }
    }

    // ── operator overloads ────────────────────────────────────────────────────

    @Test
    fun `plus operator delegates to add`() {
        assertEquals(
            "15.00",
            (KmpMoney.of("10.00", Currency.USD) + KmpMoney.of(
                "5.00",
                Currency.USD
            )).numberStrippedString
        )
    }

    @Test
    fun `minus operator delegates to subtract`() {
        assertEquals(
            "5.00",
            (KmpMoney.of("10.00", Currency.USD) - KmpMoney.of(
                "5.00",
                Currency.USD
            )).numberStrippedString
        )
    }

    @Test
    fun `times operator with Number`() {
        assertEquals("20.00", (KmpMoney.of("10.00", Currency.USD) * 2).numberStrippedString)
    }

    @Test
    fun `times operator with BigDecimal`() {
        assertEquals(
            "25.00",
            (KmpMoney.of(
                "10.00",
                Currency.USD
            ) * BigDecimal.parseString("2.5")).numberStrippedString
        )
    }

    @Test
    fun `unaryMinus operator delegates to negate`() {
        assertEquals("-10.00", (-KmpMoney.of("10.00", Currency.USD)).numberStrippedString)
    }

    // ── of(BigDecimal, Currency) ──────────────────────────────────────────────

    @Test
    fun `of BigDecimal currency stores value and currency`() {
        val bd = BigDecimal.parseString("12.34")
        val m = KmpMoney.of(bd, Currency.USD)
        assertEquals("12.34", m.numberStrippedString)
        assertEquals(Currency.USD, m.currency)
    }

    // ── zero ──────────────────────────────────────────────────────────────────

    @Test
    fun `zero returns zero amount for given currency`() {
        val m = KmpMoney.zero(Currency.EUR)
        assertTrue(m.isZero())
        assertEquals(Currency.EUR, m.currency)
    }

    // ── ofMinorUnits ──────────────────────────────────────────────────────────

    @Test
    fun `ofMinorUnits converts cents to USD`() {
        assertEquals("1.50", KmpMoney.ofMinorUnits(150L, Currency.USD).numberStrippedString)
    }

    @Test
    fun `ofMinorUnits converts minor units for zero-decimal currency`() {
        assertEquals("1500", KmpMoney.ofMinorUnits(1500L, Currency.JPY).numberStrippedString)
    }

    @Test
    fun `ofMinorUnits converts minor units for three-decimal currency`() {
        assertEquals("1.500", KmpMoney.ofMinorUnits(1500L, Currency.BHD).numberStrippedString)
    }

    @Test
    fun `ofMinorUnits handles negative minor units`() {
        assertEquals("-0.50", KmpMoney.ofMinorUnits(-50L, Currency.USD).numberStrippedString)
    }

    // ── toBigDecimal ──────────────────────────────────────────────────────────

    @Test
    fun `toBigDecimal returns amount rounded to currency scale`() {
        assertEquals(BigDecimal.parseString("10.56"), KmpMoney.of("10.555", Currency.USD).toBigDecimal())
    }

    // ── toDouble ──────────────────────────────────────────────────────────────

    @Test
    fun `toDouble returns amount as double`() {
        assertEquals(12.34, KmpMoney.of("12.34", Currency.USD).toDouble())
    }

    // ── toMinorUnits ──────────────────────────────────────────────────────────

    @Test
    fun `toMinorUnits converts USD to cents`() {
        assertEquals(150L, KmpMoney.of("1.50", Currency.USD).toMinorUnits())
    }

    @Test
    fun `toMinorUnits for zero-decimal currency returns whole units`() {
        assertEquals(1500L, KmpMoney.of("1500", Currency.JPY).toMinorUnits())
    }

    @Test
    fun `toMinorUnits rounds half-away-from-zero`() {
        assertEquals(156L, KmpMoney.of("1.555", Currency.USD).toMinorUnits())
    }

    @Test
    fun `toMinorUnits handles negative amount`() {
        assertEquals(-50L, KmpMoney.of("-0.50", Currency.USD).toMinorUnits())
    }

    @Test
    fun `ofMinorUnits and toMinorUnits are inverse operations`() {
        val original = 12345L
        assertEquals(original, KmpMoney.ofMinorUnits(original, Currency.USD).toMinorUnits())
    }

    // ── isZero ────────────────────────────────────────────────────────────────

    @Test
    fun `isZero returns true for zero`() {
        assertTrue(KmpMoney.of("0", Currency.USD).isZero())
    }

    @Test
    fun `isZero returns true for zero with decimal`() {
        assertTrue(KmpMoney.of("0.00", Currency.USD).isZero())
    }

    @Test
    fun `isZero returns false for positive amount`() {
        assertFalse(KmpMoney.of("0.01", Currency.USD).isZero())
    }

    @Test
    fun `isZero returns false for negative amount`() {
        assertFalse(KmpMoney.of("-0.01", Currency.USD).isZero())
    }

    // ── isPositive ────────────────────────────────────────────────────────────

    @Test
    fun `isPositive returns true for positive amount`() {
        assertTrue(KmpMoney.of("1.00", Currency.USD).isPositive())
    }

    @Test
    fun `isPositive returns false for zero`() {
        assertFalse(KmpMoney.of("0", Currency.USD).isPositive())
    }

    @Test
    fun `isPositive returns false for negative amount`() {
        assertFalse(KmpMoney.of("-1.00", Currency.USD).isPositive())
    }

    // ── isNegative ────────────────────────────────────────────────────────────

    @Test
    fun `isNegative returns true for negative amount`() {
        assertTrue(KmpMoney.of("-0.01", Currency.USD).isNegative())
    }

    @Test
    fun `isNegative returns false for zero`() {
        assertFalse(KmpMoney.of("0", Currency.USD).isNegative())
    }

    @Test
    fun `isNegative returns false for positive amount`() {
        assertFalse(KmpMoney.of("1.00", Currency.USD).isNegative())
    }

    // ── isGreaterThan ─────────────────────────────────────────────────────────

    @Test
    fun `isGreaterThan returns true when greater`() {
        assertTrue(
            KmpMoney.of("2.00", Currency.USD).isGreaterThan(KmpMoney.of("1.00", Currency.USD))
        )
    }

    @Test
    fun `isGreaterThan returns false for equal amounts`() {
        assertFalse(
            KmpMoney.of("1.00", Currency.USD).isGreaterThan(KmpMoney.of("1.00", Currency.USD))
        )
    }

    @Test
    fun `isGreaterThan returns false when less`() {
        assertFalse(
            KmpMoney.of("1.00", Currency.USD).isGreaterThan(KmpMoney.of("2.00", Currency.USD))
        )
    }

    @Test
    fun `isGreaterThan throws on currency mismatch`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("2.00", Currency.USD).isGreaterThan(KmpMoney.of("1.00", Currency.EUR))
        }
    }

    // ── isLessThan ────────────────────────────────────────────────────────────

    @Test
    fun `isLessThan returns true when less`() {
        assertTrue(KmpMoney.of("1.00", Currency.USD).isLessThan(KmpMoney.of("2.00", Currency.USD)))
    }

    @Test
    fun `isLessThan returns false for equal amounts`() {
        assertFalse(KmpMoney.of("1.00", Currency.USD).isLessThan(KmpMoney.of("1.00", Currency.USD)))
    }

    @Test
    fun `isLessThan returns false when greater`() {
        assertFalse(KmpMoney.of("2.00", Currency.USD).isLessThan(KmpMoney.of("1.00", Currency.USD)))
    }

    @Test
    fun `isLessThan throws on currency mismatch`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("1.00", Currency.USD).isLessThan(KmpMoney.of("2.00", Currency.EUR))
        }
    }

    // ── isSameCurrency ────────────────────────────────────────────────────────

    @Test
    fun `isSameCurrency returns true for matching currencies`() {
        assertTrue(
            KmpMoney.of("1.00", Currency.USD).isSameCurrency(KmpMoney.of("99.00", Currency.USD))
        )
    }

    @Test
    fun `isSameCurrency returns false for different currencies`() {
        assertFalse(
            KmpMoney.of("1.00", Currency.USD).isSameCurrency(KmpMoney.of("1.00", Currency.EUR))
        )
    }

    // ── isEqualTo ─────────────────────────────────────────────────────────────

    @Test
    fun `isEqualTo returns true for same value`() {
        assertTrue(KmpMoney.of("5.00", Currency.USD).isEqualTo(KmpMoney.of("5.00", Currency.USD)))
    }

    @Test
    fun `isEqualTo returns true for numerically equal values with different representations`() {
        assertTrue(KmpMoney.of("5.0", Currency.USD).isEqualTo(KmpMoney.of("5.00", Currency.USD)))
    }

    @Test
    fun `isEqualTo returns false for different values`() {
        assertFalse(KmpMoney.of("5.00", Currency.USD).isEqualTo(KmpMoney.of("5.01", Currency.USD)))
    }

    @Test
    fun `isEqualTo throws on currency mismatch`() {
        assertFailsWith<IllegalArgumentException> {
            KmpMoney.of("5.00", Currency.USD).isEqualTo(KmpMoney.of("5.00", Currency.EUR))
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
