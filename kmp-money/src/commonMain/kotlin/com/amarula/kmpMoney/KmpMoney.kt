package com.amarula.kmpMoney

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode

/**
 * An immutable monetary amount combining a [BigDecimal] value with a [Currency].
 *
 * All arithmetic operations enforce currency consistency — mixing currencies throws
 * [IllegalArgumentException].
 *
 * @property currency The currency this amount is denominated in.
 */
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
     * Adds [other] to this amount and returns the result.
     *
     * @throws IllegalArgumentException if [other] has a different currency.
     */
    fun add(other: KmpMoney): KmpMoney {
        requireSameCurrency(other)
        return KmpMoney(this.amount + other.amount, currency)
    }

    /**
     * The amount rounded to [Currency.decimalPlaces] decimal places (half-ceiling) as a plain string,
     * with no grouping separators.
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

    /** Returns `true` if the amount is zero or negative. */
    fun isNegativeOrZero(): Boolean = amount <= BigDecimal.ZERO

    /** The raw underlying [BigDecimal] amount, without rounding. */
    val number: BigDecimal
        get() = amount

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

    /** Returns a debug string in the form `"USD 12.5"` using the raw unformatted amount. */
    override fun toString(): String = "${currency.name} ${amount.toPlainString()}"

    companion object {
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
