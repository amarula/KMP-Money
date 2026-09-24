package com.amarula.kmpMoney.example.examples

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.example.components.AmountField
import com.amarula.kmpMoney.example.components.CalculateButton
import com.amarula.kmpMoney.example.components.ExampleCard
import com.amarula.kmpMoney.example.components.ResultField
import kotlinx.coroutines.launch

/**
 * Demonstrates [Currency.fromName], [Currency.getDisplayName], [Currency.flagUrl] and the
 * [Currency.decimalPlaces] / [Currency.currencySymbol] / [Currency.symbolIsPrefix] metadata.
 */
@Composable
fun CurrencyMetadataExample() {
    var codeText by remember { mutableStateOf("") }
    var displayNameResult by remember { mutableStateOf("") }
    var flagUrlResult by remember { mutableStateOf("") }
    var decimalPlacesResult by remember { mutableStateOf("") }
    var currencySymbolResult by remember { mutableStateOf("") }
    var symbolIsPrefixResult by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    ExampleCard(title = "Currency picker & metadata") {
        AmountField(
            "Currency code (e.g. USD, EUR, JPY)",
            codeText,
            keyboardType = KeyboardType.Text
        ) { codeText = it }
        CalculateButton {
            val currency = Currency.fromName(codeText) ?: Currency.UNKNOWN
            flagUrlResult = currency.flagUrl
            decimalPlacesResult = currency.decimalPlaces.toString()
            currencySymbolResult = currency.currencySymbol
            symbolIsPrefixResult = currency.symbolIsPrefix.toString()
            scope.launch {
                displayNameResult = currency.getDisplayName()
            }
        }
        ResultField("Currency.fromName(code)?.getDisplayName()", displayNameResult)
        ResultField("currency.flagUrl", flagUrlResult)
        ResultField("currency.decimalPlaces", decimalPlacesResult)
        ResultField("currency.currencySymbol", currencySymbolResult)
        ResultField("currency.symbolIsPrefix", symbolIsPrefixResult)
    }
}
