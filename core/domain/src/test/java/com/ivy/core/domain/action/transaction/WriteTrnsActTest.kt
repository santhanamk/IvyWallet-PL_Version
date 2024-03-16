package com.ivy.core.domain.action.transaction

import assertk.assertThat
import assertk.assertions.isNotNull
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.core.persistence.dao.trn.AccountCacheDaoFake
import com.ivy.core.persistence.dao.trn.TimeProviderFake
import com.ivy.core.persistence.dao.trn.TransactionDaoFake
import com.ivy.data.Sync
import com.ivy.data.SyncState
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.account.AccountState
import com.ivy.data.attachment.AttachmentSource
import com.ivy.data.attachment.AttachmentType
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.data.tag.Tag
import com.ivy.data.tag.TagState
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnMetadata
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState
import com.ivy.data.transaction.TrnTime
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

internal class WriteTrnsActTest {
    lateinit var transactionDaoFake: TransactionDaoFake
    lateinit var timeProviderFake: TimeProviderFake
    lateinit var accountCacheDaoFake: AccountCacheDaoFake
    lateinit var writeTrnsAct: WriteTrnsAct

    @BeforeEach
    fun setup() {
        transactionDaoFake = TransactionDaoFake()
        timeProviderFake = TimeProviderFake()
        accountCacheDaoFake = AccountCacheDaoFake()
        writeTrnsAct = WriteTrnsAct(transactionDaoFake, TrnsSignal(), timeProviderFake, InvalidateAccCacheAct(accountCacheDaoFake, timeProviderFake), accountCacheDaoFake)
    }

    @Test
    fun `Test new create transaction with expense`() { runBlocking {

        val randomUUID = UUID.randomUUID()
        val account = Account(id = randomUUID, name = "fake account", currency = "USD", color = 2, icon = "fakeIcon", excluded = true,
            folderId = randomUUID, orderNum = 12.0, state = AccountState.Default, sync = Sync(SyncState.Synced, LocalDateTime.now()))
        val type = TransactionType.Expense
        val value = Value(12.0, "USD")
        val category = com.ivy.data.category.Category(randomUUID, "category", CategoryType.Both, randomUUID, 23, "string", 12.0, CategoryState.Default, Sync(SyncState.Synced, LocalDateTime.now()))
        val trnTime = TrnTime.Actual(LocalDateTime.now())
        val trnState = TrnState.Hidden
        val trnPurpose = TrnPurpose.AdjustBalance
        val trnMetadata = TrnMetadata(randomUUID, randomUUID, randomUUID)

        val tags = Tag("Tag",2,"tag",2.0,TagState.Archived, Sync(SyncState.Synced, LocalDateTime.now()))
        val attachments = com.ivy.data.attachment.Attachment("attachment", "attachmentId", "Uri", AttachmentSource.Local,"filename", AttachmentType.File, Sync(SyncState.Synced, LocalDateTime.now()) )
        val trn = Transaction(randomUUID, account, type, value, category, trnTime, "title", "description", trnState, trnPurpose, mutableListOf(tags), mutableListOf(attachments), trnMetadata, Sync(SyncState.Synced, LocalDateTime.now()))
        writeTrnsAct(WriteTrnsAct.Input.CreateNew(trn))

        val transactionEntity = transactionDaoFake.transactionEntities.find {
            it.id == randomUUID.toString()
        }

        val trnTagEntity = transactionDaoFake.trnTagEntities.find {
            it.tagId == "Tag"
        }

        val attachmentsEntity = transactionDaoFake.attachmentEntities.find {
            it.id == "attachment"
        }

        assertThat(transactionEntity).isNotNull()
        assertThat(trnTagEntity).isNotNull()
        assertThat(attachmentsEntity).isNotNull()
    }
    }
}