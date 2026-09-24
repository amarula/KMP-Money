package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.amarula.kmpMoney.KmpMoney
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField

private const val FIXED_AMOUNT = 42

/**
 * Demonstrates [KmpMoney.of] with currency-code strings, [KmpMoney.zero] and the
 * [com.amarula.kmpMoney.Currency.UNKNOWN] fallback used for unrecognised codes.
 */
@Composable
fun FactoryMethodsExample() {
    var amountText by remember { mutableStateOf("") }
    var codeText by remember { mutableStateOf("") }
    var ofStringResult by remember { mutableStateOf("") }
    var ofNumberResult by remember { mutableStateOf("") }
    var zeroResult by remember { mutableStateOf("") }
    var currencyNameResult by remember { mutableStateOf("") }

    ExampleCard(title = "More factory methods") {
        AmountField("Amount", amountText) { amountText = it }
        AmountField(
            "Currency code (e.g. USD, or unknown like XYZ)",
            codeText,
            keyboardType = KeyboardType.Text
        ) { codeText = it }
        CalculateButton {
            val money = runCatching { KmpMoney.of(amountText, codeText) }
                .getOrElse { KmpMoney.of("0", codeText) }
            ofStringResult = money.toMoneyString()
            ofNumberResult = KmpMoney.of(FIXED_AMOUNT, codeText).toMoneyString()
            zeroResult = KmpMoney.zero(money.currency).toMoneyString()
            currencyNameResult = money.currency.name
        }
        ResultField("KmpMoney.of(amountText, code)", ofStringResult)
        ResultField("KmpMoney.of(42, code)", ofNumberResult)
        ResultField("KmpMoney.zero(money.currency)", zeroResult)
        ResultField("money.currency.name (UNKNOWN if unrecognised)", currencyNameResult)
    }
}
