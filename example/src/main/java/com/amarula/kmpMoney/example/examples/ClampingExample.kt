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

/** Demonstrates [KmpMoney.coerceAtLeast], [KmpMoney.coerceIn] and [KmpMoney.isBetween]. */
@Composable
fun ClampingExample() {
    var spendText by remember { mutableStateOf("") }
    var minText by remember { mutableStateOf("") }
    var maxText by remember { mutableStateOf("") }
    var coerceAtLeastResult by remember { mutableStateOf("") }
    var coerceInResult by remember { mutableStateOf("") }
    var isBetweenResult by remember { mutableStateOf("") }

    ExampleCard(title = "Clamping & ranges") {
        AmountField("Spend (USD)", spendText) { spendText = it }
        AmountField("Budget min (USD)", minText) { minText = it }
        AmountField("Budget max (USD)", maxText) { maxText = it }
        CalculateButton {
            val spend = spendText.toKmpMoneyOrZero(Currency.USD)
            val min = minText.toKmpMoneyOrZero(Currency.USD)
            val max = maxText.toKmpMoneyOrZero(Currency.USD)
            coerceAtLeastResult = spend.coerceAtLeast(min).toMoneyString()
            coerceInResult = spend.coerceIn(min, max).toMoneyString()
            isBetweenResult = spend.isBetween(min, max).toString()
        }
        ResultField("spend.coerceAtLeast(min)", coerceAtLeastResult)
        ResultField("spend.coerceIn(min, max)", coerceInResult)
        ResultField("spend.isBetween(min, max)", isBetweenResult)
    }
}
