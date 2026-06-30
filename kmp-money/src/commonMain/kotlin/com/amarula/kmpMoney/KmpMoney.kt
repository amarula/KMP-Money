package com.amarula.kmpMoney

import com.amarula.kmpMoney.resources.Res
import com.amarula.kmpMoney.resources.app_name
import org.jetbrains.compose.resources.getString

class KmpMoney {
    suspend fun greetings(): String = "Hello from ${getString(Res.string.app_name)}"
}
