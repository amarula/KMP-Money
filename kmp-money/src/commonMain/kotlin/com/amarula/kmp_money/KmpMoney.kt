package com.amarula.kmp_money

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode

/**
 * A Kotlin Multiplatform-compatible immutable monetary amount class using IonSpin's BigDecimal.
 */
data class KmpMoney(private val amount: BigDecimal, val currency: Currency) : Comparable<KmpMoney> {

    fun toMoneyString(groupingSeparator: Char = ','): String {
        val formatted = this.number.formatMoney(currency.decimalPlaces, groupingSeparator)
        val symbol = currency.currencySymbol.ifEmpty { currency.name }
        return if (currency.symbolIsPrefix) "$symbol $formatted" else "$formatted $symbol"
    }

    fun add(other: KmpMoney): KmpMoney {
        requireSameCurrency(other)
        return KmpMoney(this.amount + other.amount, currency)
    }

    fun subtract(other: KmpMoney): KmpMoney {
        requireSameCurrency(other)
        return KmpMoney(this.amount - other.amount, currency)
    }

    fun multiply(factor: BigDecimal): KmpMoney = KmpMoney(this.amount * factor, currency)

    fun multiply(factor: Number): KmpMoney = multiply(BigDecimal.parseString(factor.toString()))

    fun divide(
        divisor: BigDecimal,
        roundingMode: RoundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
    ): KmpMoney {
        val result = amount.divide(divisor, DecimalMode(DECIMAL128_PRECISION, roundingMode))
            .roundToDigitPositionAfterDecimalPoint(currency.decimalPlaces.toLong(), roundingMode)
        return KmpMoney(result, currency)
    }

    fun divide(
        divisor: Number,
        roundingMode: RoundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
    ): KmpMoney = divide(BigDecimal.parseString(divisor.toString()), roundingMode)

    fun negate(): KmpMoney = KmpMoney(amount.negate(), currency)

    fun abs(): KmpMoney = KmpMoney(amount.abs(), currency)

    fun remainder(divisor: BigDecimal): KmpMoney {
        val whole = amount.divide(
            divisor,
            DecimalMode(DECIMAL128_PRECISION, RoundingMode.ROUND_HALF_AWAY_FROM_ZERO)
        )
            .roundToDigitPositionAfterDecimalPoint(0, RoundingMode.TOWARDS_ZERO)
        return KmpMoney(amount - whole * divisor, currency)
    }

    fun remainder(divisor: Number): KmpMoney = remainder(BigDecimal.parseString(divisor.toString()))

    /**
     * Distributes this money across [ratios] proportionally without losing any minor unit.
     * Leftover pennies are assigned to the first slots.
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

    operator fun plus(other: KmpMoney): KmpMoney = add(other)
    operator fun minus(other: KmpMoney): KmpMoney = subtract(other)
    operator fun times(factor: Number): KmpMoney = multiply(factor)
    operator fun times(factor: BigDecimal): KmpMoney = multiply(factor)
    operator fun unaryMinus(): KmpMoney = negate()

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

    val numberStripped: BigDecimal
        get() = BigDecimal.parseString(numberStrippedString)

    fun isNegativeOrZero(): Boolean = amount <= BigDecimal.ZERO

    val number: BigDecimal
        get() = amount

    override fun compareTo(other: KmpMoney): Int {
        requireSameCurrency(other)
        return this.amount.compareTo(other.amount)
    }

    private fun requireSameCurrency(other: KmpMoney) {
        require(this.currency == other.currency) {
            "Currency mismatch: ${this.currency.name} vs ${other.currency.name}"
        }
    }

    override fun toString(): String = "${currency.name} ${amount.toPlainString()}"

    companion object {
        // IEEE 754 Decimal128 mandates 34 significant decimal digits of precision.
        // Using this as the intermediate precision for divide/remainder prevents
        // silent truncation while staying consistent with the standard.
        private const val DECIMAL128_PRECISION = 34L

        fun of(amount: String, currency: Currency): KmpMoney {
            return KmpMoney(BigDecimal.parseString(amount), currency)
        }

        fun of(amount: Number, currency: Currency): KmpMoney {
            return KmpMoney(BigDecimal.parseString(amount.toString()), currency)
        }

        fun of(amount: String, currency: String): KmpMoney {
            val curr = Currency.fromName(currency)
                ?: Currency.UNKNOWN
            return KmpMoney(BigDecimal.parseString(amount), curr)
        }

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
