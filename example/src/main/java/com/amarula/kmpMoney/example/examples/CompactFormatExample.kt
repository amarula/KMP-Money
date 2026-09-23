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

/** Demonstrates [KmpMoney.toCompactString]. Try a value past 1 000, 1 000 000 or 1 000 000 000. */
@Composable
fun CompactFormatExample() {
    var amountText by remember { mutableStateOf("") }
    var compactResult by remember { mutableStateOf("") }

    ExampleCard(title = "Compact formatting") {
        AmountField("Amount (USD)", amountText) { amountText = it }
        CalculateButton {
            compactResult = amountText.toKmpMoneyOrZero(Currency.USD).toCompactString()
        }
        ResultField("amount.toCompactString()", compactResult)
    }
}
