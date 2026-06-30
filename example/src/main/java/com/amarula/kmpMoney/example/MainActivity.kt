package com.amarula.kmpMoney.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.amarula.kmpMoney.Currency
import com.amarula.kmpMoney.KmpMoney
import com.amarula.kmpMoney.example.ui.theme.KMPMoneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val price = KmpMoney.of("19.99", Currency.USD)
        val tax = KmpMoney.of("1.50", Currency.USD)
        val total = price.add(tax)

        setContent {
            KMPMoneyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Text("Currency: ${price.currency.name}")
                        Text("Price:    ${price.toMoneyString()}")
                        Text("Tax:      ${tax.toMoneyString()}")
                        Text("Total:    ${total.toMoneyString()}")
                    }
                }
            }
        }
    }
}
