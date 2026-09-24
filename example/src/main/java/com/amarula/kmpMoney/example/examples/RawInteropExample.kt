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
 * Demonstrates the raw and interop value accessors [KmpMoney.number], [KmpMoney.numberStripped],
 * [KmpMoney.numberStrippedString], [KmpMoney.toBigDecimal] and [KmpMoney.toDouble].
 */
@Composable
fun RawInteropExample() {
    var amountText by remember { mutableStateOf("") }
    var numberResult by remember { mutableStateOf("") }
    var numberStrippedResult by remember { mutableStateOf("") }
    var numberStrippedStringResult by remember { mutableStateOf("") }
    var toBigDecimalResult by remember { mutableStateOf("") }
    var toDoubleResult by remember { mutableStateOf("") }

    ExampleCard(title = "Raw & interop values") {
        AmountField("Amount (USD, try 12.5)", amountText) { amountText = it }
        CalculateButton {
            val amount = amountText.toKmpMoneyOrZero(Currency.USD)
            numberResult = amount.number.toPlainString()
            numberStrippedResult = amount.numberStripped.toPlainString()
            numberStrippedStringResult = amount.numberStrippedString
            toBigDecimalResult = amount.toBigDecimal().toPlainString()
            toDoubleResult = amount.toDouble().toString()
        }
        ResultField("amount.number", numberResult)
        ResultField("amount.numberStripped", numberStrippedResult)
        ResultField("amount.numberStrippedString", numberStrippedStringResult)
        ResultField("amount.toBigDecimal()", toBigDecimalResult)
        ResultField("amount.toDouble()", toDoubleResult)
    }
}
