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
import com.amarula.kmpMoney.example.examples.BasicsExample

/** One selectable category button, and the example it reveals when picked. */
private data class ExampleCategory(val title: String, val content: @Composable () -> Unit)

private val exampleCategories = listOf(
    ExampleCategory("Basics") { BasicsExample() }
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
