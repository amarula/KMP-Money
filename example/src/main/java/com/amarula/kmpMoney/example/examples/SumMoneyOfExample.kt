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
import com.amarula.kmpMoney.sumMoneyOf

private data class LineItem(val name: String, val total: KmpMoney)

/** Demonstrates [Collection.sumMoneyOf], summing a [KmpMoney] field across domain objects. */
@Composable
fun SumMoneyOfExample() {
    var item1Text by remember { mutableStateOf("") }
    var item2Text by remember { mutableStateOf("") }
    var item3Text by remember { mutableStateOf("") }
    var totalResult by remember { mutableStateOf("") }

    ExampleCard(title = "sumMoneyOf on domain objects") {
        AmountField("Line item 1 total (USD)", item1Text) { item1Text = it }
        AmountField("Line item 2 total (USD)", item2Text) { item2Text = it }
        AmountField("Line item 3 total (USD)", item3Text) { item3Text = it }
        CalculateButton {
            val lineItems = listOf(
                LineItem("Item 1", item1Text.toKmpMoneyOrZero(Currency.USD)),
                LineItem("Item 2", item2Text.toKmpMoneyOrZero(Currency.USD)),
                LineItem("Item 3", item3Text.toKmpMoneyOrZero(Currency.USD))
            )
            totalResult = lineItems.sumMoneyOf { it.total }?.toMoneyString().orEmpty()
        }
        ResultField("lineItems.sumMoneyOf { it.total }", totalResult)
    }
}
