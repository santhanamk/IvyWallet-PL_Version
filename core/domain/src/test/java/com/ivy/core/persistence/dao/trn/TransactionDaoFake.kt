package com.ivy.core.persistence.dao.trn

import androidx.sqlite.db.SupportSQLiteQuery
import com.ivy.core.persistence.entity.attachment.AttachmentEntity
import com.ivy.core.persistence.entity.trn.TransactionEntity
import com.ivy.core.persistence.entity.trn.TrnMetadataEntity
import com.ivy.core.persistence.entity.trn.TrnTagEntity
import com.ivy.data.SyncState
import org.junit.jupiter.api.Assertions.*

class TransactionDaoFake: TransactionDao() {

    val transactionEntities: MutableList<TransactionEntity> = mutableListOf()
    var trnTagEntities: MutableList<TrnTagEntity> = mutableListOf()
    var attachmentEntities: MutableList<AttachmentEntity> = mutableListOf()
    var trnMetadataEntities: MutableList<TrnMetadataEntity> = mutableListOf()
    override suspend fun saveTrnEntity(entity: TransactionEntity) {
        transactionEntities.add(entity)
    }

    override suspend fun updateTrnTagsSyncByTrnId(trnId: String, sync: SyncState) {
        val entity = transactionEntities.find {
            it.id == trnId
        }
        val index = transactionEntities.indexOf(entity)
        entity?.sync = sync
        if (entity != null) {
            transactionEntities[index] = entity
        }
    }

    override suspend fun saveTags(entity: List<TrnTagEntity>) {
        trnTagEntities.addAll(entity)
    }

    override suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    ) {
        val entity = attachmentEntities.find {
            it.id == associatedId
        }
        val index = attachmentEntities.indexOf(entity)
        entity?.sync = sync
        if (entity != null) {
            attachmentEntities[index] = entity
        }
    }

    override suspend fun saveAttachments(entity: List<AttachmentEntity>) {
        attachmentEntities.addAll(entity)
    }

    override suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState) {
        val entity = trnMetadataEntities.find {
            it.id == trnId
        }
        val index = trnMetadataEntities.indexOf(entity)
        entity?.sync = sync
        if (entity != null) {
            trnMetadataEntities[index] = entity
        }
    }

    override suspend fun saveMetadata(entity: List<TrnMetadataEntity>) {
        trnMetadataEntities.addAll(entity)
    }

    override suspend fun findAllBlocking(): List<TransactionEntity> {
        return transactionEntities
    }

    // not supported for this fake
    override suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity> {
        return transactionEntities
    }

    override suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime? {
        val entity = transactionEntities.find {
            it.id == trnId && it.sync == SyncState.Deleting
        }?: return null

        return AccountIdAndTrnTime(
            accountId = entity.accountId,
            time = entity.time,
            timeType = entity.timeType
        )
    }

    override suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState) {
        val entity = trnMetadataEntities.find {
            it.id == trnId
        }
        val index = trnMetadataEntities.indexOf(entity)
        entity?.sync = sync
        if (entity != null) {
            trnMetadataEntities[index] = entity
        }
    }

}