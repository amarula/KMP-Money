package com.amarula.kmpMoney

/**
 * Returns the sum of all [KmpMoney] values in the list, or `null` if the list is empty.
 *
 * @throws IllegalArgumentException if the list contains amounts of different currencies.
 */
fun List<KmpMoney>.sum(): KmpMoney? = fold(null as KmpMoney?) { acc, m -> acc?.add(m) ?: m }

/**
 * Returns the sum of all [KmpMoney] values in the list.
 * Returns `KmpMoney.zero(currency)` if the list is empty.
 *
 * @param currency Currency used to produce the zero value for an empty list.
 * @throws IllegalArgumentException if the list contains amounts of a different currency.
 */
fun List<KmpMoney>.sum(currency: Currency): KmpMoney = sum() ?: KmpMoney.zero(currency)

/**
 * Returns the largest amount in the list, or `null` if the list is empty.
 *
 * @throws IllegalArgumentException if the list contains amounts of different currencies.
 */
fun List<KmpMoney>.max(): KmpMoney? = fold(null as KmpMoney?) { acc, m ->
    if (acc == null || m.isGreaterThan(acc)) m else acc
}

/**
 * Returns the smallest amount in the list, or `null` if the list is empty.
 *
 * @throws IllegalArgumentException if the list contains amounts of different currencies.
 */
fun List<KmpMoney>.min(): KmpMoney? = fold(null as KmpMoney?) { acc, m ->
    if (acc == null || m.isLessThan(acc)) m else acc
}

/**
 * Returns the arithmetic mean of all amounts in the list, rounded to the currency's
 * decimal places (half-away-from-zero), or `null` if the list is empty.
 *
 * @throws IllegalArgumentException if the list contains amounts of different currencies.
 */
fun List<KmpMoney>.average(): KmpMoney? {
    if (isEmpty()) return null
    return sum()!!.divide(size)
}

/**
 * Maps each element to a [KmpMoney] via [selector] and returns their sum,
 * or `null` if the collection is empty.
 *
 * @throws IllegalArgumentException if the mapped amounts span different currencies.
 */
fun <T> Collection<T>.sumMoneyOf(selector: (T) -> KmpMoney): KmpMoney? =
    map(selector).let { list -> if (list.isEmpty()) null else list.sum() }
