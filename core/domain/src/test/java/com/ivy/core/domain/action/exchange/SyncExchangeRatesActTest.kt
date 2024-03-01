package com.ivy.core.domain.action.exchange

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SyncExchangeRatesActTest {

    // fake for RemoteExchangeProvider
    private lateinit var syncExchangeRatesAct: SyncExchangeRatesAct
    private lateinit var exchangeProviderFake: RemoteExchangeProviderFake
    private lateinit var exchangeDaoFake: ExchangeRateDaoFake

    @BeforeEach
    fun setUp() {
        exchangeProviderFake = RemoteExchangeProviderFake()
        exchangeDaoFake = ExchangeRateDaoFake()
        syncExchangeRatesAct = SyncExchangeRatesAct(
            exchangeProvider = exchangeProviderFake,
            exchangeRateDao = exchangeDaoFake
        )
    }

    @Test
    fun `Test sync exchange rates, negative values ignored`() = runBlocking {
        syncExchangeRatesAct("USD")

        // suspend function so use runBlocking

        val usdRates = exchangeDaoFake.findAllByBaseCurrency("USD")
            .first {
                it.isNotEmpty()
            }

        val cadRate = usdRates.find {
            it.currency == "CAD"
        }

        assertThat(cadRate).isNull()
    }

    @Test
    fun `Test sync exchange rates, positive values are shown`() = runBlocking<Unit> {
        syncExchangeRatesAct("USD")

        // suspend function so use runBlocking

        val usdRates = exchangeDaoFake.findAllByBaseCurrency("USD")
            .first {
                it.isNotEmpty()
            }
        val eurRate = usdRates.find {it.currency == "EUR"}
        val audRate = usdRates.find {it.currency == "AUD"}

        assertThat(eurRate).isNotNull()
        assertThat(audRate).isNotNull()
    }
}