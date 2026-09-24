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
 * Demonstrates [KmpMoney.isZero], [KmpMoney.isPositive], [KmpMoney.isNegative],
 * [KmpMoney.isPositiveOrZero], [KmpMoney.isNegativeOrZero] and [KmpMoney.sign].
 */
@Composable
fun SignAndZeroExample() {
    var amountText by remember { mutableStateOf("") }
    var isZeroResult by remember { mutableStateOf("") }
    var isPositiveResult by remember { mutableStateOf("") }
    var isNegativeResult by remember { mutableStateOf("") }
    var isPositiveOrZeroResult by remember { mutableStateOf("") }
    var isNegativeOrZeroResult by remember { mutableStateOf("") }
    var signResult by remember { mutableStateOf("") }

    ExampleCard(title = "Sign & zero checks") {
        AmountField("Amount (USD, try a negative value)", amountText) { amountText = it }
        CalculateButton {
            val amount = amountText.toKmpMoneyOrZero(Currency.USD)
            isZeroResult = amount.isZero().toString()
            isPositiveResult = amount.isPositive().toString()
            isNegativeResult = amount.isNegative().toString()
            isPositiveOrZeroResult = amount.isPositiveOrZero().toString()
            isNegativeOrZeroResult = amount.isNegativeOrZero().toString()
            signResult = amount.sign.toString()
        }
        ResultField("amount.isZero()", isZeroResult)
        ResultField("amount.isPositive()", isPositiveResult)
        ResultField("amount.isNegative()", isNegativeResult)
        ResultField("amount.isPositiveOrZero()", isPositiveOrZeroResult)
        ResultField("amount.isNegativeOrZero()", isNegativeOrZeroResult)
        ResultField("amount.sign", signResult)
    }
}
