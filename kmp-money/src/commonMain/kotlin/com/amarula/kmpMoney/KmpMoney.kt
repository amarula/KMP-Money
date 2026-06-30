package com.amarula.kmpMoney

import androidx.compose.runtime.Stable
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode

/**
 * An immutable monetary amount combining a [BigDecimal] value with a [Currency].
 *
 * All arithmetic operations enforce currency consistency — mixing currencies throws
 * [IllegalArgumentException].
 *
 * @property currency The currency this amount is denominated in.
 */
@Stable
data class KmpMoney(private val amount: BigDecimal, val currency: Currency) : Comparable<KmpMoney> {

    /**
     * Returns a human-readable string with the currency symbol (or code if no symbol) and
     * formatted amount, e.g. `"$ 1,234.56"` or `"1.234,56 lei"`, respecting [Currency.symbolIsPrefix].
     *
     * @param groupingSeparator Character used to separate thousands groups (default `','`).
     */
    fun toMoneyString(groupingSeparator: Char = ','): String {
        val formatted = this.number.formatMoney(currency.decimalPlaces, groupingSeparator)
        val symbol = currency.currencySymbol.ifEmpty { currency.name }
        return if (currency.symbolIsPrefix) "$symbol $formatted" else "$formatted $symbol"
    }

    /**
     * Returns a formatted string with fine-grained control over the label and grouping.
     *
     * @param showSymbol Whether to include the currency label (symbol or code). Default `true`.
     * @param useCode When `true`, uses the ISO currency code (e.g. `"USD"`) instead of the symbol
     *   (e.g. `"$"`). Has no effect when [showSymbol] is `false`. Default `false`.
     * @param groupingSeparator Character used to separate thousands groups. Default `','`.
     */
    fun format(
        showSymbol: Boolean = true,
        useCode: Boolean = false,
        groupingSeparator: Char = ','
    ): String {
        val formatted = number.formatMoney(currency.decimalPlaces, groupingSeparator)
        if (!showSymbol) return formatted
        val label = if (useCode) {
            currency.name
        } else {
            currency.currencySymbol.ifEmpty {
                currency.name
            }
        }
        return if (currency.symbolIsPrefix) "$label $formatted" else "$formatted $label"
    }

    /**
     * Returns a compact human-readable string that abbreviates large amounts with K/M/B suffixes.
     * Amounts below 1 000 are formatted at full currency precision.
     *
     * Examples: `"$ 1.5K"`, `"$ 2.3M"`, `"$ 4.1B"`, `"$ 99.99"`.
     */
    fun toCompactString(): String {
        val absAmount = amount.abs()
        val billion = BigDecimal.parseString("1000000000")
        val million = BigDecimal.parseString("1000000")
        val thousand = BigDecimal.parseString("1000")

        val (divisor, suffix) = when {
            absAmount >= billion -> Pair(billion, "B")
            absAmount >= million -> Pair(million, "M")
            absAmount >= thousand -> Pair(thousand, "K")
            else -> Pair(null, "")
        }

        val label = currency.currencySymbol.ifEmpty { currency.name }
        val formatted = if (divisor == null) {
            number.formatMoney(currency.decimalPlaces)
        } else {
            val compact = amount.divide(
                divisor,
                DecimalMode(34L, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
            )
                .roundToDigitPositionAfterDecimalPoint(1, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
                .toPlainString()
            "$compact$suffix"
        }
        return if (currency.symbolIsPrefix) "$label $formatted" else "$formatted $label"
    }

    /**
     * Adds [other] to this amount and returns the result.
     *
     * @throws IllegalArgumentException if [other] has a different currency.
     */
    fun add(other: KmpMoney): KmpMoney {
        requireSameCurrency(other)
        return KmpMoney(this.amount + other.amount, currency)
    }

    /**
     * Subtracts [other] from this amount and returns the result.
     *
     * @throws IllegalArgumentException if [other] has a different currency.
     */
    fun subtract(other: KmpMoney): KmpMoney {
        requireSameCurrency(other)
        return KmpMoney(this.amount - other.amount, currency)
    }

    /**
     * Multiplies this amount by [factor] and returns the result.
     *
     * @param factor [BigDecimal] multiplier.
     */
    fun multiply(factor: BigDecimal): KmpMoney = KmpMoney(this.amount * factor, currency)

    /**
     * Multiplies this amount by [factor] and returns the result.
     *
     * @param factor Numeric multiplier; converted to [BigDecimal] via its string representation.
     */
    fun multiply(factor: Number): KmpMoney = multiply(BigDecimal.parseString(factor.toString()))

    /**
     * Divides this amount by [divisor] and returns the result, rounded to [Currency.decimalPlaces].
     *
     * @param divisor [BigDecimal] divisor; must not be zero.
     * @param roundingMode Rounding strategy applied after division (default: half-away-from-zero).
     */
    fun divide(
        divisor: BigDecimal,
        roundingMode: RoundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
    ): KmpMoney {
        val result = amount.divide(divisor, DecimalMode(DECIMAL128_PRECISION, roundingMode))
            .roundToDigitPositionAfterDecimalPoint(currency.decimalPlaces.toLong(), roundingMode)
        return KmpMoney(result, currency)
    }

    /**
     * Divides this amount by [divisor] and returns the result, rounded to [Currency.decimalPlaces].
     *
     * @param divisor Numeric divisor; converted to [BigDecimal] via its string representation.
     * @param roundingMode Rounding strategy applied after division (default: half-away-from-zero).
     */
    fun divide(
        divisor: Number,
        roundingMode: RoundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
    ): KmpMoney = divide(BigDecimal.parseString(divisor.toString()), roundingMode)

    /**
     * Returns the portion of this amount that represents [rate] percent.
     *
     * Example: `100.00.percentage(10)` → `10.00`
     *
     * @param rate Percentage value, e.g. `10` for 10 %.
     */
    fun percentage(rate: Number): KmpMoney =
        multiply(BigDecimal.parseString(rate.toString()) / BigDecimal.parseString("100"))

    /**
     * Returns this amount increased by [rate] percent (e.g. adding VAT or a markup).
     *
     * Example: `100.00.addPercentage(10)` → `110.00`
     *
     * @param rate Percentage value, e.g. `10` for 10 %.
     */
    fun addPercentage(rate: Number): KmpMoney = add(percentage(rate))

    /**
     * Returns the pre-tax / pre-markup base amount when this amount already includes
     * an embedded [rate] percent (reverse percentage calculation).
     *
     * Example: `110.00.subtractPercentage(10)` → `100.00`
     *
     * @param rate Percentage value, e.g. `10` for 10 %.
     */
    fun subtractPercentage(rate: Number): KmpMoney {
        val divisor = BigDecimal.parseString("100") + BigDecimal.parseString(rate.toString())
        return divide(divisor / BigDecimal.parseString("100"))
    }

    /** Returns a new [KmpMoney] with the sign of this amount flipped. */
    fun negate(): KmpMoney = KmpMoney(amount.negate(), currency)

    /** Returns a new [KmpMoney] with the absolute value of this amount. */
    fun abs(): KmpMoney = KmpMoney(amount.abs(), currency)

    /**
     * Returns the remainder of dividing this amount by [divisor], truncating towards zero.
     *
     * @param divisor [BigDecimal] divisor; must not be zero.
     */
    fun remainder(divisor: BigDecimal): KmpMoney {
        val whole = amount.divide(
            divisor,
            DecimalMode(DECIMAL128_PRECISION, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        )
            .roundToDigitPositionAfterDecimalPoint(0, RoundingMode.TOWARDS_ZERO)
        return KmpMoney(amount - whole * divisor, currency)
    }

    /**
     * Returns the remainder of dividing this amount by [divisor], truncating towards zero.
     *
     * @param divisor Numeric divisor; converted to [BigDecimal] via its string representation.
     */
    fun remainder(divisor: Number): KmpMoney = remainder(BigDecimal.parseString(divisor.toString()))

    /**
     * Distributes this money across [ratios] proportionally without losing any minor unit.
     * Leftover pennies are assigned to the first slots.
     *
     * @param ratios Non-empty list of non-negative integers representing relative shares.
     * @throws IllegalArgumentException if [ratios] is empty, contains negative values, or sums to zero.
     */
    fun allocate(ratios: List<Int>): List<KmpMoney> {
        require(ratios.isNotEmpty()) { "Ratios must not be empty" }
        require(ratios.all { it >= 0 }) { "Ratios must be non-negative" }
        val totalRatio = ratios.sumOf { it }.toLong()
        require(totalRatio > 0) { "Sum of ratios must be positive" }

        val scale = currency.decimalPlaces
        val factor = BigDecimal.parseString("1" + "0".repeat(scale))
        val totalMinor = (amount * factor)
            .roundToDigitPositionAfterDecimalPoint(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
            .longValue(exactRequired = false)
        val isNeg = totalMinor < 0
        val absMinor = if (isNeg) -totalMinor else totalMinor

        val allocated = LongArray(ratios.size) { i -> absMinor * ratios[i] / totalRatio }
        var leftover = absMinor - allocated.sum()
        var idx = 0
        while (leftover > 0 && idx < allocated.size) {
            allocated[idx++] += 1
            leftover--
        }

        return allocated.map { minor ->
            val signedMinor = if (isNeg) -minor else minor
            KmpMoney(BigDecimal.fromLong(signedMinor) / factor, currency)
        }
    }

    /** Operator alias for [add]. @throws IllegalArgumentException on currency mismatch. */
    operator fun plus(other: KmpMoney): KmpMoney = add(other)

    /** Operator alias for [subtract]. @throws IllegalArgumentException on currency mismatch. */
    operator fun minus(other: KmpMoney): KmpMoney = subtract(other)

    /** Operator alias for [multiply] with a [Number] factor. */
    operator fun times(factor: Number): KmpMoney = multiply(factor)

    /** Operator alias for [multiply] with a [BigDecimal] factor. */
    operator fun times(factor: BigDecimal): KmpMoney = multiply(factor)

    /** Operator alias for [negate]. */
    operator fun unaryMinus(): KmpMoney = negate()

    /**
     * The amount rounded to [Currency.decimalPlaces] decimal places (half-away-from-zero) as a
     * plain string, with no grouping separators.
     */
    val numberStrippedString: String
        get() {
            val rounded = amount.roundToDigitPositionAfterDecimalPoint(
                currency.decimalPlaces.toLong(),
                RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
            )
            val plain = rounded.toPlainString()
            if (currency.decimalPlaces == 0) return plain
            return if ('.' in plain) {
                val parts = plain.split(".")
                "${parts[0]}.${parts[1].padEnd(currency.decimalPlaces, '0')}"
            } else {
                "$plain.${"0".repeat(currency.decimalPlaces)}"
            }
        }

    /** The amount rounded to [Currency.decimalPlaces] decimal places as a [BigDecimal]. */
    val numberStripped: BigDecimal
        get() = BigDecimal.parseString(numberStrippedString)

    /**
     * Returns a new [KmpMoney] with the amount rounded to [Currency.decimalPlaces] using [roundingMode].
     *
     * @param roundingMode Rounding strategy to apply.
     */
    fun round(roundingMode: RoundingMode): KmpMoney {
        val rounded = amount.roundToDigitPositionAfterDecimalPoint(
            currency.decimalPlaces.toLong(),
            roundingMode
        )
        return KmpMoney(rounded, currency)
    }

    /** Returns `true` if the amount is zero or negative. */
    fun isNegativeOrZero(): Boolean = amount <= BigDecimal.ZERO

    /** Returns `true` if the amount is exactly zero. */
    fun isZero(): Boolean = amount.compareTo(BigDecimal.ZERO) == 0

    /** Returns `true` if the amount is strictly greater than zero. */
    fun isPositive(): Boolean = amount > BigDecimal.ZERO

    /** Returns `true` if the amount is strictly less than zero. */
    fun isNegative(): Boolean = amount < BigDecimal.ZERO

    /**
     * Returns `true` if this amount is strictly greater than [other].
     *
     * @throws IllegalArgumentException if [other] has a different currency.
     */
    fun isGreaterThan(other: KmpMoney): Boolean {
        requireSameCurrency(other)
        return this.amount > other.amount
    }

    /**
     * Returns `true` if this amount is strictly less than [other].
     *
     * @throws IllegalArgumentException if [other] has a different currency.
     */
    fun isLessThan(other: KmpMoney): Boolean {
        requireSameCurrency(other)
        return this.amount < other.amount
    }

    /** Returns `true` if [other] has the same currency as this amount. */
    fun isSameCurrency(other: KmpMoney): Boolean = this.currency == other.currency

    /**
     * Returns `true` if this amount equals [other] in both value and currency.
     *
     * Unlike [equals], this compares amounts numerically (e.g. `1.0` == `1.00`).
     *
     * @throws IllegalArgumentException if [other] has a different currency.
     */
    fun isEqualTo(other: KmpMoney): Boolean {
        requireSameCurrency(other)
        return this.amount.compareTo(other.amount) == 0
    }

    /**
     * Returns `true` if this amount is strictly between [min] and [max] (exclusive).
     *
     * @throws IllegalArgumentException if [min] or [max] has a different currency.
     */
    fun isBetween(min: KmpMoney, max: KmpMoney): Boolean {
        requireSameCurrency(min)
        requireSameCurrency(max)
        return this.amount > min.amount && this.amount < max.amount
    }

    /**
     * The sign of this amount: `-1` for negative, `0` for zero, `1` for positive.
     */
    val sign: Int
        get() = when {
            amount < BigDecimal.ZERO -> -1
            amount > BigDecimal.ZERO -> 1
            else -> 0
        }

    /** The raw underlying [BigDecimal] amount, without rounding. */
    val number: BigDecimal
        get() = amount

    /** Returns the amount rounded to [Currency.decimalPlaces] as a [BigDecimal]. */
    fun toBigDecimal(): BigDecimal = numberStripped

    /** Returns the amount rounded to [Currency.decimalPlaces] as a [Double]. */
    fun toDouble(): Double = numberStripped.doubleValue(exactRequired = false)

    /**
     * Returns the amount as an integer count of the currency's minor units
     * (e.g. `1.50 USD` → `150`), rounded half-away-from-zero.
     */
    fun toMinorUnits(): Long {
        val factor = BigDecimal.parseString("1" + "0".repeat(currency.decimalPlaces))
        return (amount * factor)
            .roundToDigitPositionAfterDecimalPoint(0, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
            .longValue(exactRequired = false)
    }

    /**
     * Compares this amount to [other] by value.
     *
     * @throws IllegalArgumentException if [other] has a different currency.
     */
    override fun compareTo(other: KmpMoney): Int {
        requireSameCurrency(other)
        return this.amount.compareTo(other.amount)
    }

    private fun requireSameCurrency(other: KmpMoney) {
        require(this.currency == other.currency) {
            "Currency mismatch: ${this.currency.name} vs ${other.currency.name}"
        }
    }

    /**
     * Serialises this amount to a [Map] with two keys: `"amount"` (the rounded decimal string)
     * and `"currency"` (the ISO currency code). Suitable for key-value stores and JSON-like maps.
     *
     * Use [KmpMoney.fromMap] to deserialise.
     */
    fun toMap(): Map<String, String> = mapOf(
        "amount" to numberStrippedString,
        "currency" to currency.name
    )

    /** Returns a debug string in the form `"USD 12.5"` using the raw unformatted amount. */
    override fun toString(): String = "${currency.name} ${amount.toPlainString()}"

    companion object {
        // IEEE 754 Decimal128 mandates 34 significant decimal digits of precision.
        // Using this as the intermediate precision for divide/remainder prevents
        // silent truncation while staying consistent with the standard.
        private const val DECIMAL128_PRECISION = 34L

        /**
         * Creates a [KmpMoney] from a decimal string and a [Currency].
         *
         * @param amount Decimal string, e.g. `"12.50"`.
         */
        fun of(amount: String, currency: Currency): KmpMoney {
            return KmpMoney(BigDecimal.parseString(amount), currency)
        }

        /**
         * Creates a [KmpMoney] from a [Number] and a [Currency].
         *
         * @param amount Numeric value; converted via [Double] so very large integers may lose precision.
         */
        fun of(amount: Number, currency: Currency): KmpMoney {
            return KmpMoney(BigDecimal.parseString(amount.toString()), currency)
        }

        /**
         * Creates a [KmpMoney] from a decimal string and a currency name string.
         * Falls back to [Currency.UNKNOWN] if [currency] is not recognised.
         *
         * @param amount Decimal string, e.g. `"12.50"`.
         * @param currency Currency code such as `"USD"` (case-insensitive).
         */
        fun of(amount: String, currency: String): KmpMoney {
            val curr = Currency.fromName(currency)
                ?: Currency.UNKNOWN
            return KmpMoney(BigDecimal.parseString(amount), curr)
        }

        /**
         * Creates a [KmpMoney] from a [Number] and a currency name string.
         * Falls back to [Currency.UNKNOWN] if [currency] is not recognised.
         *
         * @param amount Numeric value; converted via [Double] so very large integers may lose precision.
         * @param currency Currency code such as `"USD"` (case-insensitive).
         */
        fun of(amount: Number, currency: String): KmpMoney {
            val curr = Currency.fromName(currency)
                ?: Currency.UNKNOWN
            return KmpMoney(BigDecimal.parseString(amount.toString()), curr)
        }

        /**
         * Creates a [KmpMoney] from a [BigDecimal] and a [Currency].
         *
         * @param amount [BigDecimal] value.
         */
        fun of(amount: BigDecimal, currency: Currency): KmpMoney = KmpMoney(amount, currency)

        /**
         * Creates a [KmpMoney] with a zero amount for the given [currency].
         */
        fun zero(currency: Currency): KmpMoney = KmpMoney(BigDecimal.ZERO, currency)

        /**
         * Creates a [KmpMoney] from an integer count of the currency's minor units
         * (e.g. cents for USD, pence for GBP, yen for JPY).
         *
         * @param minorUnits Integer minor-unit amount (e.g. `150` → `1.50 USD`).
         */
        fun ofMinorUnits(minorUnits: Long, currency: Currency): KmpMoney {
            val factor = BigDecimal.parseString("1" + "0".repeat(currency.decimalPlaces))
            val value = if (currency.decimalPlaces == 0) {
                BigDecimal.fromLong(minorUnits)
            } else {
                BigDecimal.fromLong(
                    minorUnits
                ).divide(
                    factor,
                    DecimalMode(DECIMAL128_PRECISION, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
                )
            }
            return KmpMoney(value, currency)
        }

        /**
         * Deserialises a [KmpMoney] from a map produced by [KmpMoney.toMap].
         *
         * Expects keys `"amount"` (decimal string) and `"currency"` (ISO code).
         * Falls back to [Currency.UNKNOWN] for unrecognised currency codes.
         *
         * @throws IllegalArgumentException if either key is absent.
         */
        fun fromMap(map: Map<String, String>): KmpMoney {
            val amount = requireNotNull(map["amount"]) { "Missing 'amount' key in map" }
            val currency = requireNotNull(map["currency"]) { "Missing 'currency' key in map" }
            return of(amount, currency)
        }
    }

    private fun BigDecimal.formatMoney(
        fractionDigits: Int = 2,
        groupingSeparator: Char = ','
    ): String {
        val scaled = this.roundToDigitPositionAfterDecimalPoint(
            fractionDigits.toLong(),
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        )
        val plain = scaled.toPlainString()
        val isNegative = plain.startsWith("-")
        val absPlain = if (isNegative) plain.substring(1) else plain

        val parts = absPlain.split(".")
        val integerPart = parts[0]
        val fractionPart = if (parts.size > 1) parts[1] else ""

        val grouped = buildString {
            val chars = integerPart.reversed()
            for ((i, c) in chars.withIndex()) {
                if (i > 0 && i % 3 == 0) append(groupingSeparator)
                append(c)
            }
        }.reversed()

        val result = if (fractionDigits > 0) {
            val frac = fractionPart.padEnd(fractionDigits, '0').take(fractionDigits)
            "$grouped.$frac"
        } else {
            grouped
        }
        return if (isNegative) "-$result" else result
    }
}
