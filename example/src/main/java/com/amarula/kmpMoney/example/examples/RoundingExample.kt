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
import com.ionspin.kotlin.bignum.decimal.RoundingMode

/** Demonstrates [KmpMoney.round] and [KmpMoney.roundToCashDenomination]. */
@Composable
fun RoundingExample() {
    var amountText by remember { mutableStateOf("") }
    var floorResult by remember { mutableStateOf("") }
    var cashResult by remember { mutableStateOf("") }

    ExampleCard(title = "Rounding") {
        AmountField("Amount (USD)", amountText) { amountText = it }
        CalculateButton {
            val amount = amountText.toKmpMoneyOrZero(Currency.USD)
            floorResult = amount.round(RoundingMode.FLOOR).toMoneyString()
            cashResult = amount.roundToCashDenomination(0.05).toMoneyString()
        }
        ResultField("amount.round(FLOOR)", floorResult)
        ResultField("amount.roundToCashDenomination(0.05)", cashResult)
    }
}
