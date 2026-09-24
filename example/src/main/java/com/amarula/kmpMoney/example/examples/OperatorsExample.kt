package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField
import com.amarula.kmpMoney.example.components.toKmpMoneyOrZero

/** Demonstrates the Kotlin `+`, `-` and `*` operators as alternatives to method-call syntax. */
@Composable
fun OperatorsExample() {
    var priceText by remember { mutableStateOf("") }
    var taxText by remember { mutableStateOf("") }
    var refundText by remember { mutableStateOf("") }
    var unitPriceText by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("") }
    var totalResult by remember { mutableStateOf("") }
    var refundResult by remember { mutableStateOf("") }
    var lineTotalResult by remember { mutableStateOf("") }

    ExampleCard(title = "Kotlin operators") {
        AmountField("Price (USD)", priceText) { priceText = it }
        AmountField("Tax (USD)", taxText) { taxText = it }
        AmountField("Refund (USD)", refundText) { refundText = it }
        AmountField("Unit price (USD)", unitPriceText) { unitPriceText = it }
        AmountField("Quantity", quantityText) { quantityText = it }
        CalculateButton {
            val price = priceText.toKmpMoneyOrZero(Currency.USD)
            val tax = taxText.toKmpMoneyOrZero(Currency.USD)
            val refund = refundText.toKmpMoneyOrZero(Currency.USD)
            val unitPrice = unitPriceText.toKmpMoneyOrZero(Currency.USD)
            val quantity = quantityText.toIntOrNull() ?: 0
            totalResult = (price + tax).toMoneyString()
            refundResult = (-refund).toMoneyString()
            lineTotalResult = (unitPrice * quantity).toMoneyString()
        }
        ResultField("price + tax", totalResult)
        ResultField("-refund", refundResult)
        ResultField("unitPrice * quantity", lineTotalResult)
    }
}
