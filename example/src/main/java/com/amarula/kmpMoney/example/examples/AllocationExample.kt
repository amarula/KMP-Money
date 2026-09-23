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

private const val SPLIT_PARTS = 3

/** Demonstrates [KmpMoney.allocate] and [KmpMoney.split]. */
@Composable
fun AllocationExample() {
    var billText by remember { mutableStateOf("") }
    var allocateResult by remember { mutableStateOf("") }
    var splitResult by remember { mutableStateOf("") }

    ExampleCard(title = "Allocation & splitting") {
        AmountField("Bill (USD)", billText) { billText = it }
        CalculateButton {
            val bill = billText.toKmpMoneyOrZero(Currency.USD)
            allocateResult = bill.allocate(listOf(1, 1, 1)).joinToString { it.toMoneyString() }
            splitResult = bill.split(SPLIT_PARTS).joinToString { it.toMoneyString() }
        }
        ResultField("bill.allocate([1, 1, 1])", allocateResult)
        ResultField("bill.split(3)", splitResult)
    }
}
