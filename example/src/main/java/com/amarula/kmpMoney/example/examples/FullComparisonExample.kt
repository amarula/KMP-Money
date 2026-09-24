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

private const val REFERENCE_EUR_AMOUNT = "10.00"

/**
 * Demonstrates [KmpMoney.isLessThan], [KmpMoney.isGreaterThanOrEqualTo],
 * [KmpMoney.isLessThanOrEqualTo], [KmpMoney.isEqualTo] and [KmpMoney.isSameCurrency].
 */
@Composable
fun FullComparisonExample() {
    var aText by remember { mutableStateOf("") }
    var bText by remember { mutableStateOf("") }
    var isLessThanResult by remember { mutableStateOf("") }
    var isGreaterThanOrEqualToResult by remember { mutableStateOf("") }
    var isLessThanOrEqualToResult by remember { mutableStateOf("") }
    var isEqualToResult by remember { mutableStateOf("") }
    var isSameCurrencyResult by remember { mutableStateOf("") }

    ExampleCard(title = "Full comparison suite") {
        AmountField("Amount A (USD)", aText) { aText = it }
        AmountField("Amount B (USD)", bText) { bText = it }
        CalculateButton {
            val a = aText.toKmpMoneyOrZero(Currency.USD)
            val b = bText.toKmpMoneyOrZero(Currency.USD)
            val eur = KmpMoney.of(REFERENCE_EUR_AMOUNT, Currency.EUR)
            isLessThanResult = a.isLessThan(b).toString()
            isGreaterThanOrEqualToResult = a.isGreaterThanOrEqualTo(b).toString()
            isLessThanOrEqualToResult = a.isLessThanOrEqualTo(b).toString()
            isEqualToResult = a.isEqualTo(b).toString()
            isSameCurrencyResult = a.isSameCurrency(eur).toString()
        }
        ResultField("a.isLessThan(b)", isLessThanResult)
        ResultField("a.isGreaterThanOrEqualTo(b)", isGreaterThanOrEqualToResult)
        ResultField("a.isLessThanOrEqualTo(b)", isLessThanOrEqualToResult)
        ResultField("a.isEqualTo(b)", isEqualToResult)
        ResultField("a.isSameCurrency(10.00 EUR)", isSameCurrencyResult)
    }
}
