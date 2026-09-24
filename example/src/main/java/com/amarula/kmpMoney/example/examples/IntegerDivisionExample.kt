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

private const val DEFAULT_BILL_DENOMINATION = 5.0

/**
 * Demonstrates [KmpMoney.divideToIntegralValue] and [KmpMoney.remainder], e.g. figuring out how
 * many whole $5 bills fit into an amount, plus the change left over.
 */
@Composable
fun IntegerDivisionExample() {
    var amountText by remember { mutableStateOf("") }
    var denominationText by remember { mutableStateOf("") }
    var billsResult by remember { mutableStateOf("") }
    var changeResult by remember { mutableStateOf("") }

    ExampleCard(title = "Integer division & remainder") {
        AmountField("Amount (USD)", amountText) { amountText = it }
        AmountField("Bill denomination (USD, default 5)", denominationText) {
            denominationText = it
        }
        CalculateButton {
            val amount = amountText.toKmpMoneyOrZero(Currency.USD)
            val denomination = denominationText.toDoubleOrNull() ?: DEFAULT_BILL_DENOMINATION
            billsResult = amount.divideToIntegralValue(denomination).toMoneyString()
            changeResult = amount.remainder(denomination).toMoneyString()
        }
        ResultField("amount.divideToIntegralValue(denomination)", billsResult)
        ResultField("amount.remainder(denomination)", changeResult)
    }
}
