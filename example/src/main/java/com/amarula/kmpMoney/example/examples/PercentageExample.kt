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

/** Demonstrates [KmpMoney.percentage], [KmpMoney.addPercentage] and [KmpMoney.subtractPercentage]. */
@Composable
fun PercentageExample() {
    var subtotalText by remember { mutableStateOf("") }
    var percentageResult by remember { mutableStateOf("") }
    var addPercentageResult by remember { mutableStateOf("") }
    var subtractPercentageResult by remember { mutableStateOf("") }

    ExampleCard(title = "Percentage calculations") {
        AmountField("Subtotal (USD)", subtotalText) { subtotalText = it }
        CalculateButton {
            val subtotal = subtotalText.toKmpMoneyOrZero(Currency.USD)
            val withVat = subtotal.addPercentage(16)
            percentageResult = subtotal.percentage(16).toMoneyString()
            addPercentageResult = withVat.toMoneyString()
            subtractPercentageResult = withVat.subtractPercentage(16).toMoneyString()
        }
        ResultField("subtotal.percentage(16)", percentageResult)
        ResultField("subtotal.addPercentage(16)", addPercentageResult)
        ResultField("withVat.subtractPercentage(16)", subtractPercentageResult)
    }
}
