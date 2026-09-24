package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField
import com.amarula.kmpMoney.example.components.toKmpMoneyOrZero
import com.amarula.kmpMoney.sumMoney
import com.amarula.kmpMoney.totalByCurrency
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * Demonstrates [Flow.sumMoney] and [Flow.totalByCurrency], useful for streaming transactions or
 * totalling a multi-currency wallet.
 */
@Composable
fun FlowExtensionsExample() {
    var usd1Text by remember { mutableStateOf("") }
    var usd2Text by remember { mutableStateOf("") }
    var eurText by remember { mutableStateOf("") }
    var sumResult by remember { mutableStateOf("") }
    var totalsResult by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ExampleCard(title = "Flow extensions") {
        AmountField("Transaction 1 (USD)", usd1Text) { usd1Text = it }
        AmountField("Transaction 2 (USD)", usd2Text) { usd2Text = it }
        AmountField("Transaction 3 (EUR)", eurText) { eurText = it }
        CalculateButton {
            val usd1 = usd1Text.toKmpMoneyOrZero(Currency.USD)
            val usd2 = usd2Text.toKmpMoneyOrZero(Currency.USD)
            val eur = eurText.toKmpMoneyOrZero(Currency.EUR)
            scope.launch {
                sumResult = flowOf(usd1, usd2).sumMoney()?.toMoneyString().orEmpty()
                totalsResult = flowOf(usd1, usd2, eur).totalByCurrency()
                    .entries.joinToString { (currency, total) ->
                        "$currency: ${total.toMoneyString()}"
                    }
            }
        }
        ResultField("flowOf(usd1, usd2).sumMoney()", sumResult)
        ResultField("flowOf(usd1, usd2, eur).totalByCurrency()", totalsResult)
    }
}
