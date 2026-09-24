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

/** Demonstrates [KmpMoney.toMap] and [KmpMoney.fromMap] for key-value store round-trips. */
@Composable
fun MapRoundTripExample() {
    var amountText by remember { mutableStateOf("") }
    var mapResult by remember { mutableStateOf("") }
    var fromMapResult by remember { mutableStateOf("") }

    ExampleCard(title = "Map round-trip") {
        AmountField("Amount (USD)", amountText) { amountText = it }
        CalculateButton {
            val money = amountText.toKmpMoneyOrZero(Currency.USD)
            val map = money.toMap()
            mapResult = map.toString()
            fromMapResult = KmpMoney.fromMap(map).toMoneyString()
        }
        ResultField("money.toMap()", mapResult)
        ResultField("KmpMoney.fromMap(map)", fromMapResult)
    }
}
