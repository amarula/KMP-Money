package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.ExchangeRateProvider
import com.amarula.kmpMoney.KmpMoney
import com.amarula.kmpMoney.convertTo
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField
import com.amarula.kmpMoney.example.components.toKmpMoneyOrZero
import com.ionspin.kotlin.bignum.decimal.BigDecimal

/** Demonstrates [KmpMoney.convertTo] with a raw rate and with an [ExchangeRateProvider]. */
@Composable
fun ConversionExample() {
    var amountText by remember { mutableStateOf("") }
    var eurResult by remember { mutableStateOf("") }
    var gbpResult by remember { mutableStateOf("") }
    val gbpRates = remember { ExchangeRateProvider { _, _ -> BigDecimal.parseString("0.79") } }

    ExampleCard(title = "Currency conversion") {
        AmountField("Amount (USD)", amountText) { amountText = it }
        CalculateButton {
            val amountUsd = amountText.toKmpMoneyOrZero(Currency.USD)
            eurResult = amountUsd.convertTo(Currency.EUR, 0.92).toMoneyString()
            gbpResult = amountUsd.convertTo(Currency.GBP, gbpRates).toMoneyString()
        }
        ResultField("amountUsd.convertTo(EUR, 0.92)", eurResult)
        ResultField("amountUsd.convertTo(GBP, gbpRates)", gbpResult)
    }
}
