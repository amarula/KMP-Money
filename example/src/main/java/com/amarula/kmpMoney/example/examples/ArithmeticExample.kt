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

/** Demonstrates [KmpMoney.add] and [KmpMoney.subtract]. */
@Composable
fun ArithmeticExample() {
    var aText by remember { mutableStateOf("") }
    var bText by remember { mutableStateOf("") }
    var addResult by remember { mutableStateOf("") }
    var subtractResult by remember { mutableStateOf("") }

    ExampleCard(title = "Addition & subtraction") {
        AmountField("Amount A (USD)", aText) { aText = it }
        AmountField("Amount B (USD)", bText) { bText = it }
        CalculateButton {
            val a = aText.toKmpMoneyOrZero(Currency.USD)
            val b = bText.toKmpMoneyOrZero(Currency.USD)
            addResult = a.add(b).toMoneyString()
            subtractResult = a.subtract(b).toMoneyString()
        }
        ResultField("a.add(b)", addResult)
        ResultField("a.subtract(b)", subtractResult)
    }
}
