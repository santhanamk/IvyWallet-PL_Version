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
    var trnTagEntities: List<TrnTagEntity> = mutableListOf()
    var attachmentEntities: List<AttachmentEntity> = mutableListOf()
    var trnMetadataEntities: List<TrnMetadataEntity> = mutableListOf()
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
        trnTagEntities = entity
    }

    override suspend fun updateAttachmentsSyncByAssociatedId(
        associatedId: String,
        sync: SyncState
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun saveAttachments(entity: List<AttachmentEntity>) {
        attachmentEntities = entity
    }

    override suspend fun updateMetadataSyncByTrnId(trnId: String, sync: SyncState) {
        TODO("Not yet implemented")
    }

    override suspend fun saveMetadata(entity: List<TrnMetadataEntity>) {
        trnMetadataEntities = entity
    }

    override suspend fun findAllBlocking(): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findBySQL(query: SupportSQLiteQuery): List<TransactionEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun findAccountIdAndTimeById(trnId: String): AccountIdAndTrnTime? {
        TODO("Not yet implemented")
    }

    override suspend fun updateTrnEntitySyncById(trnId: String, sync: SyncState) {
        TODO("Not yet implemented")
    }

}