package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.KmpMoney
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField
import com.amarula.kmpMoney.example.components.toKmpMoneyOrZero

/**
 * Demonstrates [KmpMoney.toMinorUnits] and [KmpMoney.ofMinorUnits], the cents-style integer APIs
 * used by payment processors such as Stripe.
 */
@Composable
fun MinorUnitsExample() {
    var amountText by remember { mutableStateOf("") }
    var minorUnitsText by remember { mutableStateOf("") }
    var toMinorUnitsResult by remember { mutableStateOf("") }
    var ofMinorUnitsResult by remember { mutableStateOf("") }

    ExampleCard(title = "Minor units") {
        AmountField("Amount (USD)", amountText) { amountText = it }
        AmountField(
            "Minor units, e.g. Stripe cents",
            minorUnitsText,
            keyboardType = KeyboardType.Number
        ) { minorUnitsText = it }
        CalculateButton {
            val amount = amountText.toKmpMoneyOrZero(Currency.USD)
            val minorUnits = minorUnitsText.toLongOrNull() ?: 0L
            toMinorUnitsResult = amount.toMinorUnits().toString()
            ofMinorUnitsResult = KmpMoney.ofMinorUnits(minorUnits, Currency.USD).toMoneyString()
        }
        ResultField("amount.toMinorUnits()", toMinorUnitsResult)
        ResultField("KmpMoney.ofMinorUnits(minorUnits, USD)", ofMinorUnitsResult)
    }
}
