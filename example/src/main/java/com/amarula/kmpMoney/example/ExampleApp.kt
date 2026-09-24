package com.amarula.kmpMoney.example

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amarula.kmpMoney.example.examples.AllocationExample
import com.amarula.kmpMoney.example.examples.ArithmeticExample
import com.amarula.kmpMoney.example.examples.BasicsExample
import com.amarula.kmpMoney.example.examples.ClampingExample
import com.amarula.kmpMoney.example.examples.CollectionsExample
import com.amarula.kmpMoney.example.examples.CompactFormatExample
import com.amarula.kmpMoney.example.examples.ComparisonExample
import com.amarula.kmpMoney.example.examples.ConversionExample
import com.amarula.kmpMoney.example.examples.CurrencyMismatchExample
import com.amarula.kmpMoney.example.examples.FactoryMethodsExample
import com.amarula.kmpMoney.example.examples.FullComparisonExample
import com.amarula.kmpMoney.example.examples.IntegerDivisionExample
import com.amarula.kmpMoney.example.examples.JsonSerializationExample
import com.amarula.kmpMoney.example.examples.MapRoundTripExample
import com.amarula.kmpMoney.example.examples.MinorUnitsExample
import com.amarula.kmpMoney.example.examples.MultiplyDivideExample
import com.amarula.kmpMoney.example.examples.NegationAbsExample
import com.amarula.kmpMoney.example.examples.OperatorsExample
import com.amarula.kmpMoney.example.examples.PercentageExample
import com.amarula.kmpMoney.example.examples.RawInteropExample
import com.amarula.kmpMoney.example.examples.RoundingExample
import com.amarula.kmpMoney.example.examples.SignAndZeroExample

/** One selectable category button, and the example it reveals when picked. */
private data class ExampleCategory(val title: String, val content: @Composable () -> Unit)

private val exampleCategories = listOf(
    ExampleCategory("Basics") { BasicsExample() },
    ExampleCategory("Arithmetic") { ArithmeticExample() },
    ExampleCategory("Multiply / Divide") { MultiplyDivideExample() },
    ExampleCategory("Percentage") { PercentageExample() },
    ExampleCategory("Comparisons") { ComparisonExample() },
    ExampleCategory("Rounding") { RoundingExample() },
    ExampleCategory("Allocation") { AllocationExample() },
    ExampleCategory("Conversion") { ConversionExample() },
    ExampleCategory("Compact format") { CompactFormatExample() },
    ExampleCategory("Collections") { CollectionsExample() },
    ExampleCategory("Sign & zero") { SignAndZeroExample() },
    ExampleCategory("Full comparisons") { FullComparisonExample() },
    ExampleCategory("Clamping") { ClampingExample() },
    ExampleCategory("Currency mismatch") { CurrencyMismatchExample() },
    ExampleCategory("Negation & abs") { NegationAbsExample() },
    ExampleCategory("Operators") { OperatorsExample() },
    ExampleCategory("Integer division") { IntegerDivisionExample() },
    ExampleCategory("Raw & interop") { RawInteropExample() },
    ExampleCategory("Minor units") { MinorUnitsExample() },
    ExampleCategory("Factory methods") { FactoryMethodsExample() },
    ExampleCategory("Map round-trip") { MapRoundTripExample() },
    ExampleCategory("JSON serialization") { JsonSerializationExample() }
)

/**
 * Single-screen showcase of KMPMoney usage. One button per category is listed here in the order
 * it was added; clicking a button shows that category's example and hides the rest.
 */
@Composable
fun ExampleApp() {
    var selected by remember { mutableIntStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("KMPMoney Examples", style = MaterialTheme.typography.headlineSmall)

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                exampleCategories.forEachIndexed { index, category ->
                    if (index == selected) {
                        Button(onClick = { selected = index }) { Text(category.title) }
                    } else {
                        OutlinedButton(onClick = { selected = index }) { Text(category.title) }
                    }
                }
            }

            exampleCategories[selected].content()
        }
    }
}
