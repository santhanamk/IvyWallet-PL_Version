package com.ivy.core.persistence.dao.trn

import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant

class AccountCacheDaoFake: AccountCacheDao {

//    val accounts: MutableList<AccountCacheEntity> = mutableListOf()
    private val accounts = MutableStateFlow<MutableList<AccountCacheEntity>>(mutableListOf())

    override fun findAccountCache(accountId: String): Flow<AccountCacheEntity?> {
//        val account = accounts.value.find {
//            it.accountId == accountId
//        }
//        return account

        return accounts.map {
            entities ->
            entities.find {
                it.accountId == accountId
            }
        }
    }

    override suspend fun findTimestampById(accountId: String): Instant? {
        val account = accounts.value.find {
            it.accountId == accountId
        }
        return account?.timestamp
    }

    override suspend fun save(cache: AccountCacheEntity) {
        accounts.value.add(cache)
    }

    override suspend fun delete(accountId: String) {
        val account = accounts.value.find {
            it.accountId == accountId
        }
        accounts.value.remove(account)
    }

    override suspend fun deleteAll() {
        accounts.value = mutableListOf()
    }
}