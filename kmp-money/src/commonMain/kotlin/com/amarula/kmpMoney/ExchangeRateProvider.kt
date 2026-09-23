package com.amarula.kmpMoney

import com.ionspin.kotlin.bignum.decimal.BigDecimal

/**
 * Supplies exchange rates for currency conversion, decoupling "where a rate comes from" (a REST
 * API, a cached table, a database) from [KmpMoney.convertTo]. KMPMoney performs no networking or
 * caching itself — implement this to plug in whatever rate source the app already has.
 */
fun interface ExchangeRateProvider {

    /**
     * Returns the exchange rate from [from] to [to], expressed as units of [to] per one unit of
     * [from]. Implementations should throw if no rate is available for the given pair.
     */
    fun getRate(from: Currency, to: Currency): BigDecimal
}

/**
 * Converts this amount to [targetCurrency] using the rate supplied by [provider], rounded to
 * [targetCurrency]'s decimal places (half-away-from-zero). Equivalent to
 * `convertTo(targetCurrency, provider.getRate(currency, targetCurrency))`.
 *
 * @param targetCurrency Currency to convert into.
 * @param provider Source of the exchange rate between this amount's currency and [targetCurrency].
 */
fun KmpMoney.convertTo(targetCurrency: Currency, provider: ExchangeRateProvider): KmpMoney =
    convertTo(targetCurrency, provider.getRate(currency, targetCurrency))
