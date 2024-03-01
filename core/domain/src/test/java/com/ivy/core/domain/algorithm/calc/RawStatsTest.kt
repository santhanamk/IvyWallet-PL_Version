package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.Instant
import java.time.temporal.ChronoUnit

internal class RawStatsTest {

    private lateinit var trns: MutableList<CalcTrn>

    @BeforeEach
    fun setUp() {
        trns = mutableListOf()
    }

    @ParameterizedTest
    @CsvSource(
        "15.00,EUR,1"
    )
    fun `Test raw status function for one CalcTran `(amount: String, currency: CurrencyCode, type: Int) {
        val transactionType = TransactionType.fromCode(type)?:TransactionType.Income
        trns.add(CalcTrn(amount.toDouble(), currency, transactionType, Instant.now().truncatedTo(
            ChronoUnit.SECONDS)))
        val stats = rawStats(trns)
        assertThat(stats.incomesCount).isEqualTo(1)
        assertThat(stats.expensesCount).isEqualTo(0)
        val incomesMap = mutableMapOf<CurrencyCode, Double>()
        incomesMap[currency] = amount.toDouble()
        assertThat(stats.incomes).isEqualTo(incomesMap)
        assertThat(stats.expenses).isEqualTo(mutableMapOf())
    }

    // Phillip's
    // idea

    @Test
    fun `Test raw stats function for multiple CalcTran `() {

        val oneInstant = Instant.now().minusSeconds(1)
        val twoInstant = Instant.now().minusSeconds(2)
        val threeInstant = Instant.now().minusSeconds(3)
        val fourInstant = Instant.now().minusSeconds(4)

        val firstCalcTrn = CalcTrn(10.0, "EUR", TransactionType.Income, oneInstant)
        val secondCalcTrn = CalcTrn(15.0, "EUR", TransactionType.Expense, twoInstant)
        val thirdCalcTrn = CalcTrn(5.0, "USD", TransactionType.Income, threeInstant)
        val fourthCalcTrn = CalcTrn(20.0, "USD", TransactionType.Expense, fourInstant)
        trns.add(firstCalcTrn)
        trns.add(secondCalcTrn)
        trns.add(thirdCalcTrn)
        trns.add(fourthCalcTrn)
        val stats = rawStats(trns)
        assertThat(stats.incomesCount).isEqualTo(2)
        assertThat(stats.expensesCount).isEqualTo(2)
        assertThat(stats.newestTrnTime).isEqualTo(oneInstant)
        val incomesMap = mutableMapOf<CurrencyCode, Double>()
        incomesMap["EUR"] = 10.0
        incomesMap["USD"] = 5.0
        val expensesMap = mutableMapOf<CurrencyCode, Double>()
        expensesMap["EUR"] = 15.0
        expensesMap["USD"] = 20.0
        assertThat(stats.incomes).isEqualTo(incomesMap)
        assertThat(stats.expenses).isEqualTo(expensesMap)
    }
}