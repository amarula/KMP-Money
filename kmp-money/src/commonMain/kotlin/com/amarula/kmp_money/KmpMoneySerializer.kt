package com.amarula.kmp_money

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Kotlinx-serialization serializer for [KmpMoney].
 *
 * Serialises as a JSON object with two string fields:
 * ```json
 * { "amount": "12.50", "currency": "USD" }
 * ```
 *
 * Register via the `@Serializable` annotation or a `SerializersModule`:
 * ```kotlin
 * @Serializable(with = KmpMoneySerializer::class)
 * data class Order(val total: KmpMoney)
 * ```
 */
object KmpMoneySerializer : KSerializer<KmpMoney> {

    @Serializable
    private data class Surrogate(val amount: String, val currency: String)

    override val descriptor: SerialDescriptor = Surrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: KmpMoney) {
        encoder.encodeSerializableValue(
            Surrogate.serializer(),
            Surrogate(
                amount = value.numberStrippedString,
                currency = value.currency.name
            )
        )
    }

    override fun deserialize(decoder: Decoder): KmpMoney {
        val surrogate = decoder.decodeSerializableValue(Surrogate.serializer())
        return KmpMoney.fromMap(mapOf("amount" to surrogate.amount, "currency" to surrogate.currency))
    }
}
