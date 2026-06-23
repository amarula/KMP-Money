package com.amarula.kmp_money

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal

/**
 * A Kotlin Multiplatform-compatible immutable monetary amount class using IonSpin's BigDecimal.
 */
data class KmpMoney(private val amount: BigDecimal, val currency: Currency) : Comparable<KmpMoney> {

    fun toMoneyString(): String {
        val amount = this.number.formatMoney(currency.decimalPlaces)
        val currencyCode = currency.name
        return if (currency.symbolIsPrefix) {
            "$currencyCode $amount"
        } else {
            "$amount $currencyCode"
        }
    }

    fun add(other: KmpMoney): KmpMoney {
        requireSameCurrency(other)
        return KmpMoney(this.amount + other.amount, currency)
    }

    val numberStrippedString: String
        get() {
            val rounded =
                amount.roundToDigitPositionAfterDecimalPoint(
                    currency.decimalPlaces.toLong(),
                    RoundingMode.ROUND_HALF_CEILING
                )
            return rounded.toPlainString()
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

    override fun toString(): String = "${currency.name} $amount"

    companion object {
        fun of(amount: String, currency: Currency): KmpMoney {
            return KmpMoney(BigDecimal.parseString(amount), currency)
        }

        fun of(amount: Number, currency: Currency): KmpMoney {
            return KmpMoney(amount.toDouble().toBigDecimal(), currency)
        }

        fun of(amount: String, currency: String): KmpMoney {
            val curr = Currency.fromName(currency)
                ?: Currency.UNKNOWN
            return KmpMoney(BigDecimal.parseString(amount), curr)
        }

        fun of(amount: Number, currency: String): KmpMoney {
            val curr = Currency.fromName(currency)
                ?: Currency.UNKNOWN
            return KmpMoney(amount.toDouble().toBigDecimal(), curr)
        }
    }

    private fun BigDecimal.formatMoney(fractionDigits: Int = 2): String {
        val scaled = this.roundToDigitPositionAfterDecimalPoint(
            fractionDigits.toLong(),
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        )

        val plain = scaled.toPlainString()

        val parts = plain.split(".")
        val integerPart = parts[0]
        val fractionPart = if (parts.size > 1) parts[1] else ""

        val grouped = buildString {
            val chars = integerPart.reversed()
            for ((i, c) in chars.withIndex()) {
                if (i > 0 && i % 3 == 0) append(',')
                append(c)
            }
        }.reversed()

        return if (fractionDigits > 0) {
            val frac = fractionPart.padEnd(fractionDigits, '0').take(fractionDigits)
            "$grouped.$frac"
        } else {
            grouped
        }
    }
}