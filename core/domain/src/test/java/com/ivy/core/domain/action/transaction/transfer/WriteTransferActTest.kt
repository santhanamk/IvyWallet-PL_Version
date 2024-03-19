package com.ivy.core.domain.action.transaction.transfer

import android.graphics.ColorSpace.match
import com.ivy.core.domain.action.transaction.WriteTrnsAct
import com.ivy.core.domain.action.transaction.WriteTrnsBatchAct
import com.ivy.core.domain.action.transaction.account
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.transaction.TransactionType
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.ivy.data.transaction.TrnTime
import io.mockk.coVerify
import java.time.LocalDateTime

class WriteTransferActTest {

    private lateinit var writeTransferAct: WriteTransferAct
    private lateinit var writeTrnsBatchAct: WriteTrnsBatchAct
    private lateinit var transferByBatchIdAct: TransferByBatchIdAct
    private lateinit var writeTrnsAct: WriteTrnsAct

    @BeforeEach
    fun setup() {
        writeTrnsAct = mockk(relaxed = true)
        writeTrnsBatchAct = mockk(relaxed = true)
        transferByBatchIdAct = mockk(relaxed = true)

        writeTransferAct = WriteTransferAct(
            writeTrnsBatchAct,transferByBatchIdAct, writeTrnsAct)
    }

    @Test
    fun `Add transfer, fees are considered`() { runBlocking {
        writeTransferAct(
            ModifyTransfer.add(
                data = TransferData(amountFrom = Value(amount = 50.0, currency = "EUR"),
                    amountTo = Value(amount = 60.0, currency = "USD"),
                    accountFrom = account().copy(
                        name = "Test Account 1"
                    ),
                    accountTo = account().copy(
                        name = "Test Account 2"
                    ),
                    category = null,
                    time = TrnTime.Actual(LocalDateTime.now()),
                    title = "Title",
                    description ="Description",
                    fee = Value(amount = 5.0, currency = "EUR"),
                    sync = Sync(state = SyncState.Syncing, lastUpdated = LocalDateTime.now())
                )
            )
        )

        coVerify {
            writeTrnsBatchAct (
                match {
                    it as WriteTrnsBatchAct.ModifyBatch.Save
                    val from = it.batch.trns[0]
                    val to = it.batch.trns[1]
                    val fee = it.batch.trns[2]
                    from.value.amount == 50.0&&
                            to.value.amount == 60.0 &&
                            fee.value.amount == 5.0&&
                            fee.type == TransactionType.Expense

                }
            )
        }
    }

    }
}