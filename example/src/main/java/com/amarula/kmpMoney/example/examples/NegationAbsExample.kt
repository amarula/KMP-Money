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
 * Demonstrates [KmpMoney.negate], [KmpMoney.abs] and the unary minus operator, e.g. for
 * representing a refund or debit alongside a regular charge.
 */
@Composable
fun NegationAbsExample() {
    var amountText by remember { mutableStateOf("") }
    var negateResult by remember { mutableStateOf("") }
    var absResult by remember { mutableStateOf("") }
    var unaryMinusResult by remember { mutableStateOf("") }

    ExampleCard(title = "Negation & absolute value") {
        AmountField("Amount (USD, e.g. -25.00 for a refund)", amountText) { amountText = it }
        CalculateButton {
            val amount = amountText.toKmpMoneyOrZero(Currency.USD)
            negateResult = amount.negate().toMoneyString()
            absResult = amount.abs().toMoneyString()
            unaryMinusResult = (-amount).toMoneyString()
        }
        ResultField("amount.negate()", negateResult)
        ResultField("amount.abs()", absResult)
        ResultField("-amount", unaryMinusResult)
    }
}
