package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.KmpMoneySerializer
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField
import com.amarula.kmpMoney.example.components.toKmpMoneyOrZero
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Demonstrates [KmpMoneySerializer] with kotlinx.serialization's `Json.encodeToString`/`decodeFromString`. */
@Composable
fun JsonSerializationExample() {
    var amountText by remember { mutableStateOf("") }
    var jsonResult by remember { mutableStateOf("") }
    var decodedResult by remember { mutableStateOf("") }

    ExampleCard(title = "JSON serialization") {
        AmountField("Amount (USD)", amountText) { amountText = it }
        CalculateButton {
            val money = amountText.toKmpMoneyOrZero(Currency.USD)
            val json = Json.encodeToString(KmpMoneySerializer, money)
            jsonResult = json
            decodedResult = Json.decodeFromString(KmpMoneySerializer, json).toMoneyString()
        }
        ResultField("Json.encodeToString(KmpMoneySerializer, money)", jsonResult)
        ResultField("Json.decodeFromString(KmpMoneySerializer, json)", decodedResult)
    }
}
