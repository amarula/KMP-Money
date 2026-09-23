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

/** Demonstrates [KmpMoney.isGreaterThan] and [KmpMoney.coerceAtMost]. */
@Composable
fun ComparisonExample() {
    var budgetText by remember { mutableStateOf("") }
    var expenseText by remember { mutableStateOf("") }
    var isGreaterResult by remember { mutableStateOf("") }
    var coerceResult by remember { mutableStateOf("") }

    ExampleCard(title = "Comparisons") {
        AmountField("Budget (USD)", budgetText) { budgetText = it }
        AmountField("Expense (USD)", expenseText) { expenseText = it }
        CalculateButton {
            val budget = budgetText.toKmpMoneyOrZero(Currency.USD)
            val expense = expenseText.toKmpMoneyOrZero(Currency.USD)
            isGreaterResult = expense.isGreaterThan(budget).toString()
            coerceResult = expense.coerceAtMost(budget).toMoneyString()
        }
        ResultField("expense.isGreaterThan(budget)", isGreaterResult)
        ResultField("expense.coerceAtMost(budget)", coerceResult)
    }
}
