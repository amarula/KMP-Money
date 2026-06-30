package com.amarula.kmpMoney

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KmpMoneySerializerTest {

    @Serializable
    private data class Wrapper(@Serializable(with = KmpMoneySerializer::class) val money: KmpMoney)

    private val json = Json { prettyPrint = false }

    // ── serialize ─────────────────────────────────────────────────────────────

    @Test
    fun `serializes amount and currency as strings`() {
        val wrapper = Wrapper(KmpMoney.of("12.50", Currency.USD))
        val encoded = json.encodeToString(wrapper)
        assertTrue(encoded.contains("\"amount\":\"12.50\""), "encoded: $encoded")
        assertTrue(encoded.contains("\"currency\":\"USD\""), "encoded: $encoded")
    }

    @Test
    fun `serializes amount rounded to currency decimal places`() {
        val wrapper = Wrapper(KmpMoney.of("10.555", Currency.USD))
        val encoded = json.encodeToString(wrapper)
        assertTrue(encoded.contains("\"amount\":\"10.56\""), "encoded: $encoded")
    }

    @Test
    fun `serializes zero-decimal currency without fraction`() {
        val wrapper = Wrapper(KmpMoney.of("1500", Currency.JPY))
        val encoded = json.encodeToString(wrapper)
        assertTrue(encoded.contains("\"amount\":\"1500\""), "encoded: $encoded")
        assertTrue(encoded.contains("\"currency\":\"JPY\""), "encoded: $encoded")
    }

    @Test
    fun `serializes three-decimal currency`() {
        val wrapper = Wrapper(KmpMoney.of("1.5", Currency.BHD))
        val encoded = json.encodeToString(wrapper)
        assertTrue(encoded.contains("\"amount\":\"1.500\""), "encoded: $encoded")
    }

    // ── deserialize ───────────────────────────────────────────────────────────

    @Test
    fun `deserializes amount and currency correctly`() {
        val decoded = json.decodeFromString<Wrapper>(
            """{"money":{"amount":"12.50","currency":"USD"}}"""
        )
        assertEquals("12.50", decoded.money.numberStrippedString)
        assertEquals(Currency.USD, decoded.money.currency)
    }

    @Test
    fun `deserializes unknown currency as UNKNOWN`() {
        val decoded = json.decodeFromString<Wrapper>(
            """{"money":{"amount":"1.00","currency":"XYZ"}}"""
        )
        assertEquals(Currency.UNKNOWN, decoded.money.currency)
    }

    // ── round-trip ────────────────────────────────────────────────────────────

    @Test
    fun `encode then decode produces equal value`() {
        val original = KmpMoney.of("99.99", Currency.EUR)
        val encoded = json.encodeToString(Wrapper(original))
        val decoded = json.decodeFromString<Wrapper>(encoded)
        assertEquals(original.numberStrippedString, decoded.money.numberStrippedString)
        assertEquals(original.currency, decoded.money.currency)
    }

    @Test
    fun `round-trip preserves negative amounts`() {
        val original = KmpMoney.of("-42.50", Currency.GBP)
        val decoded = json.decodeFromString<Wrapper>(json.encodeToString(Wrapper(original)))
        assertEquals(original.numberStrippedString, decoded.money.numberStrippedString)
    }
}
