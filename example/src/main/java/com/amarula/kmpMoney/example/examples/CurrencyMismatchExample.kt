package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.KmpMoney
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField
import com.amarula.kmpMoney.example.components.toKmpMoneyOrZero

/**
 * Demonstrates the currency-mismatch safety guarantee: mixing currencies in [KmpMoney.add]
 * throws [IllegalArgumentException] instead of silently producing a wrong result.
 */
@Composable
fun CurrencyMismatchExample() {
    var usdText by remember { mutableStateOf("") }
    var eurText by remember { mutableStateOf("") }
    var addResult by remember { mutableStateOf("") }

    ExampleCard(title = "Currency-mismatch safety") {
        AmountField("Amount (USD)", usdText) { usdText = it }
        AmountField("Amount (EUR)", eurText) { eurText = it }
        CalculateButton {
            val usd = usdText.toKmpMoneyOrZero(Currency.USD)
            val eur = eurText.toKmpMoneyOrZero(Currency.EUR)
            addResult = runCatching { usd.add(eur) }.fold(
                onSuccess = { it.toMoneyString() },
                onFailure = { "Threw IllegalArgumentException: ${it.message}" }
            )
        }
        ResultField("usd.add(eur)", addResult)
    }
}
