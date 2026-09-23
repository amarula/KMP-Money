package com.amarula.kmpMoney.example.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.KmpMoney

/**
 * A titled card wrapping one KMPMoney usage example: [content] should render one [AmountField]
 * per user-editable amount, a [CalculateButton], then one [ResultField] per call being
 * demonstrated. Every child is spaced evenly so examples don't need their own padding.
 */
@Composable
fun ExampleCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

/** A single-line numeric input for an amount the example computes from. Starts empty. */
@Composable
fun AmountField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}

/** A read-only field showing one computed result, labelled with the expression that produced it. */
@Composable
fun ResultField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

/** Triggers an example's computation; results only appear once this is pressed. */
@Composable
fun CalculateButton(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text("Calculate")
    }
}

/** Parses this string as a [KmpMoney] amount in [currency], or zero if it isn't a valid number. */
fun String.toKmpMoneyOrZero(currency: Currency): KmpMoney =
    runCatching { KmpMoney.of(this, currency) }.getOrDefault(KmpMoney.zero(currency))
