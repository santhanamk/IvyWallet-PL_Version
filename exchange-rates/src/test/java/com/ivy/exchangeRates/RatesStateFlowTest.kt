package com.ivy.exchangeRates

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsOnly
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import assertk.assertions.size
import com.ivy.MainCoroutineExtension
import com.ivy.TestDispatchers
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.exchangeRates.data.RateUi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(MainCoroutineExtension::class)
class RatesStateFlowTest {
    lateinit var baseCurrencyFlow: BaseCurrencyFlow
    lateinit var ratesDao: RatesDaoFake
    lateinit var ratesStateFlow: RatesStateFlow

    companion object {
        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeEach
    fun setUp() {
        baseCurrencyFlow = mockk()
        every {
            baseCurrencyFlow.invoke()
        } returns flowOf("", "EUR")
        ratesDao = RatesDaoFake()

        val testDispatchers = TestDispatchers(mainCoroutineExtension.testDispatcher)
        ratesStateFlow = RatesStateFlow(
            baseCurrencyFlow = baseCurrencyFlow,
            ratesDao = ratesDao,
            dispatcher = testDispatchers
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Test Exchange Rates flow emissions`() {
        runTest {

            ratesStateFlow().test{
                awaitItem() // initial emission, ignore

//                exchangeRateDao.save(exchangeRates)
//                exchangeRateOverrideDao.save(exchangeRateOverrides)

                val rates1 = awaitItem()
                assertThat(rates1.baseCurrency).isEqualTo("EUR")
                assertThat(rates1.manual).size().isEqualTo(1)
                assertThat(rates1.automatic).size().isEqualTo(2)
                val badRate = RateUi("EUR", "USD", 1.3)
                assertThat(rates1.automatic).doesNotContain(badRate)
                assertThat(rates1.manual).contains(badRate)
//                assertThat(rates1.rates["USD"]).isEqualTo(1.3)
//                assertThat(rates1.rates["CAD"]).isEqualTo(1.5) // Override rate
//                assertThat(rates1.rates["AUD"]).isEqualTo(1.9)
//
//                exchangeRateOverrideDao.save(listOf())
//
//                val rates2 = awaitItem()
//                assertThat(rates2.rates).hasSize(3)
//                assertThat(rates2.rates["USD"]).isEqualTo(1.3)
//                assertThat(rates2.rates["CAD"]).isEqualTo(1.7) // Real rate
//                assertThat(rates2.rates["AUD"]).isEqualTo(1.9)
            }
        }
    }
}