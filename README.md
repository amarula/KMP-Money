# KMPMoney

A Kotlin Multiplatform library for working with monetary amounts safely and precisely.
`KmpMoney` pairs an arbitrary-precision [`BigDecimal`](https://github.com/ionspin/kotlin-multiplatform-bignum)
value with a `Currency`, and enforces currency consistency on every operation — mixing currencies
throws instead of silently producing a wrong result.

```kotlin
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.KmpMoney

val price = KmpMoney.of("19.99", Currency.USD)
val tax = price.percentage(8.5)
val total = price + tax

println(total.toMoneyString()) // "$ 21.69"
```

## Contents

- [Why](#why)
- [Quick start](#quick-start)
- [Features](#features)
- [Project structure](#project-structure)
- [The example app](#the-example-app)
- [Development](#development)

## Why

Representing money as `Double` or `Float` invites rounding bugs; representing it as a bare
`BigDecimal` loses track of what currency it's actually in. `KmpMoney` fixes both: every amount
carries its currency, arithmetic between mismatched currencies fails loudly, and rounding always
follows the currency's own decimal places (so JPY rounds to whole yen, BHD to three decimal
places, and so on).

## Quick start

Amounts of different currencies can't be combined by accident:

```kotlin
val usd = KmpMoney.of("10.00", Currency.USD)
val eur = KmpMoney.of("10.00", Currency.EUR)

usd.add(eur) // throws IllegalArgumentException: Currency mismatch: USD vs EUR
```

Splitting a bill without losing a cent:

```kotlin
val bill = KmpMoney.of("100.00", Currency.USD)
bill.split(3) // [33.34, 33.33, 33.33] — sums back to exactly 100.00
```

## Features

- **Currency-safe arithmetic** — `add`, `subtract`, `multiply`, `divide`, and the `+ - *` operators,
  all guarded by currency checks.
- **Percentages** — `percentage`, `addPercentage`, `subtractPercentage` (reverse/embedded percentage).
- **Lossless allocation** — `allocate(ratios)` and `split(n)` distribute an amount proportionally
  without losing or duplicating a single minor unit; leftover pennies go to the first slots.
- **Rounding** — `round(mode)` and `roundToCashDenomination(denomination)` for cash rounding (e.g.
  nearest `0.05`).
- **Conversion** — `convertTo(currency, rate)`, plus an `ExchangeRateProvider` interface so an app
  can plug in its own rate source (the library does no networking or caching itself).
- **Comparisons** — `isGreaterThan`, `isLessThan`, `isEqualTo`, `isBetween`, `coerceAtLeast`,
  `coerceAtMost`, `coerceIn`, and full `Comparable` support.
- **Formatting** — `toMoneyString()`, `format(showSymbol, useCode, groupingSeparator)`, and
  `toCompactString()` (e.g. `$ 1.5K`, `$ 2.3M`).
- **Minor units & maps** — `toMinorUnits()` / `ofMinorUnits()` for integer-cents interop,
  `toMap()` / `fromMap()` for key-value storage.
- **JSON serialization** — `KmpMoneySerializer` for `kotlinx.serialization`.
- **Collection & Flow extensions** — `sum()`, `max()`, `min()`, `average()`, `sumMoneyOf { }` on
  `List<KmpMoney>`, and `sumMoney()` / `totalByCurrency()` on `Flow<KmpMoney>`.
- **~150 currencies** — ISO currencies with decimal places, symbol, symbol position, localized
  display name, and country flag metadata built in.

## Project structure

- **`kmp-money/`** — the library module (`commonMain`), published as a Kotlin Multiplatform
  library targeting JVM, Android, and iOS.
- **`example/`** — an Android Compose app that showcases every feature of the library through
  small, interactive examples.

## The example app

The `example` module is a single-screen Jetpack Compose app (`ExampleApp.kt`) with one button per
category; picking a category swaps in a small live demo backed by real `KmpMoney` calls, each
showing the exact API call and its result. Categories include:

Basics · Arithmetic · Multiply / Divide · Percentage · Comparisons · Rounding · Allocation ·
Conversion · Compact format · Collections · Sign & zero · Full comparisons · Clamping · Currency
mismatch · Negation & abs · Operators · Integer division · Raw & interop · Minor units · Factory
methods · Map round-trip · JSON serialization · Sorting · sumMoneyOf · Flow extensions · Currency
metadata

Each example lives in its own file under
`example/src/main/java/com/amarula/kmpMoney/example/examples/`, built from a small set of shared
components (`ExampleCard`, `AmountField`, `ResultField`, `CalculateButton`) in
`example/src/main/java/com/amarula/kmpMoney/example/components/ExampleComponents.kt`. This makes
the examples a good reference for API usage and a good starting point for adding a demo of a new
feature.

## Development

Run the library's unit tests:

```bash
./gradlew :kmp-money:test
```

Run static analysis (ktlint + detekt) and coverage:

```bash
./gradlew ktlintCheck detekt koverHtmlReport
```

Run the example app on a connected device or emulator:

```bash
./gradlew :example:installDebug
```
