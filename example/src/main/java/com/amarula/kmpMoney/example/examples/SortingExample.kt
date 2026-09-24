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

/** Demonstrates sorting a `List<KmpMoney>` via [KmpMoney]'s `Comparable` implementation. */
@Composable
fun SortingExample() {
    var aText by remember { mutableStateOf("") }
    var bText by remember { mutableStateOf("") }
    var cText by remember { mutableStateOf("") }
    var sortedResult by remember { mutableStateOf("") }

    ExampleCard(title = "Sorting a list") {
        AmountField("Amount 1 (USD)", aText) { aText = it }
        AmountField("Amount 2 (USD)", bText) { bText = it }
        AmountField("Amount 3 (USD)", cText) { cText = it }
        CalculateButton {
            val amounts = listOf(
                aText.toKmpMoneyOrZero(Currency.USD),
                bText.toKmpMoneyOrZero(Currency.USD),
                cText.toKmpMoneyOrZero(Currency.USD)
            )
            sortedResult = amounts.sorted().joinToString { it.toMoneyString() }
        }
        ResultField("amounts.sorted()", sortedResult)
    }
}
