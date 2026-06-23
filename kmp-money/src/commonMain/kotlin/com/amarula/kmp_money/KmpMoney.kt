package com.amarula.kmp_money

import com.amarula.kmp_money.resources.Res
import com.amarula.kmp_money.resources.app_name
import org.jetbrains.compose.resources.getString

class KmpMoney {
    suspend fun greetings(): String = "Hello from ${getString(Res.string.app_name)}"
}
