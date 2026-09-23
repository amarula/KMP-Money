package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.average
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField
import com.amarula.kmpMoney.example.components.toKmpMoneyOrZero
import com.amarula.kmpMoney.max
import com.amarula.kmpMoney.min
import com.amarula.kmpMoney.sum

/** Demonstrates the `List<KmpMoney>` extensions [sum], [max], [min] and [average]. */
@Composable
fun CollectionsExample() {
    var e1Text by remember { mutableStateOf("") }
    var e2Text by remember { mutableStateOf("") }
    var e3Text by remember { mutableStateOf("") }
    var sumResult by remember { mutableStateOf("") }
    var maxResult by remember { mutableStateOf("") }
    var minResult by remember { mutableStateOf("") }
    var averageResult by remember { mutableStateOf("") }

    ExampleCard(title = "Collections: sum, max, min, average") {
        AmountField("Expense 1 (USD)", e1Text) { e1Text = it }
        AmountField("Expense 2 (USD)", e2Text) { e2Text = it }
        AmountField("Expense 3 (USD)", e3Text) { e3Text = it }
        CalculateButton {
            val expenses = listOf(
                e1Text.toKmpMoneyOrZero(Currency.USD),
                e2Text.toKmpMoneyOrZero(Currency.USD),
                e3Text.toKmpMoneyOrZero(Currency.USD)
            )
            sumResult = expenses.sum()?.toMoneyString().orEmpty()
            maxResult = expenses.max()?.toMoneyString().orEmpty()
            minResult = expenses.min()?.toMoneyString().orEmpty()
            averageResult = expenses.average()?.toMoneyString().orEmpty()
        }
        ResultField("expenses.sum()", sumResult)
        ResultField("expenses.max()", maxResult)
        ResultField("expenses.min()", minResult)
        ResultField("expenses.average()", averageResult)
    }
}
