package com.amarula.kmpMoney

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/**
 * Collects all [KmpMoney] values in the flow and returns their sum.
 * Returns `null` if the flow emits no items.
 *
 * @throws IllegalArgumentException if the flow contains amounts of different currencies.
 */
suspend fun Flow<KmpMoney>.sumMoney(): KmpMoney? {
    var result: KmpMoney? = null
    collect { value ->
        result = result?.add(value) ?: value
    }
    return result
}

/**
 * Collects all [KmpMoney] values in the flow and returns their sum.
 * Returns `KmpMoney.zero(currency)` if the flow emits no items.
 *
 * @param currency Currency used to produce the zero value for an empty flow.
 * @throws IllegalArgumentException if the flow contains amounts of a different currency.
 */
suspend fun Flow<KmpMoney>.sumMoney(currency: Currency): KmpMoney =
    sumMoney() ?: KmpMoney.zero(currency)

/**
 * Collects all [KmpMoney] values in the flow and returns a [Map] from each [Currency]
 * to the total sum of all amounts in that currency.
 * Currencies not present in the flow are absent from the result.
 */
suspend fun Flow<KmpMoney>.totalByCurrency(): Map<Currency, KmpMoney> {
    val totals = mutableMapOf<Currency, KmpMoney>()
    collect { value ->
        totals[value.currency] = totals[value.currency]?.add(value) ?: value
    }
    return totals
}
