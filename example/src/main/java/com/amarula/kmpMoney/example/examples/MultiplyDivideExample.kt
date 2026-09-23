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

private const val MULTIPLIER = 3
private const val DIVISOR = 4

/** Demonstrates [KmpMoney.multiply] and [KmpMoney.divide]. */
@Composable
fun MultiplyDivideExample() {
    var amountText by remember { mutableStateOf("") }
    var multiplyResult by remember { mutableStateOf("") }
    var divideResult by remember { mutableStateOf("") }

    ExampleCard(title = "Multiplication & division") {
        AmountField("Amount (USD)", amountText) { amountText = it }
        CalculateButton {
            val amount = amountText.toKmpMoneyOrZero(Currency.USD)
            multiplyResult = amount.multiply(MULTIPLIER).toMoneyString()
            divideResult = amount.divide(DIVISOR).toMoneyString()
        }
        ResultField("amount.multiply(3)", multiplyResult)
        ResultField("amount.divide(4)", divideResult)
    }
}
