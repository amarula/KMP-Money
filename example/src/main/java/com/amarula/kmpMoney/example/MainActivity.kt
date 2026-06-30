package com.amarula.kmpMoney.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.amarula.kmpMoney.KmpMoney
import com.amarula.kmpMoney.example.ui.theme.KMPMoneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val money = KmpMoney()
        setContent {
            KMPMoneyTheme {
                var greeting by remember { mutableStateOf("") }
                LaunchedEffect(Unit) {
                    greeting = money.greetings()
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        greeting,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
