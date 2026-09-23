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

/** Demonstrates [KmpMoney.of] and the basic formatting functions [KmpMoney.toMoneyString]/[KmpMoney.format]. */
@Composable
fun BasicsExample() {
    var usdText by remember { mutableStateOf("") }
    var jpyText by remember { mutableStateOf("") }
    var usdResult by remember { mutableStateOf("") }
    var jpyResult by remember { mutableStateOf("") }
    var formatResult by remember { mutableStateOf("") }

    ExampleCard(title = "Creating & formatting money") {
        AmountField("USD amount", usdText) { usdText = it }
        AmountField("JPY amount", jpyText) { jpyText = it }
        CalculateButton {
            val price = usdText.toKmpMoneyOrZero(Currency.USD)
            val yen = jpyText.toKmpMoneyOrZero(Currency.JPY)
            usdResult = price.toMoneyString()
            jpyResult = yen.toMoneyString()
            formatResult = price.format(useCode = true)
        }
        ResultField("KmpMoney.of(usdText, Currency.USD)", usdResult)
        ResultField("KmpMoney.of(jpyText, Currency.JPY)", jpyResult)
        ResultField("price.format(useCode = true)", formatResult)
    }
}
