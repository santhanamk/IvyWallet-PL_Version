package com.ivy.core.persistence.dao.trn

import com.ivy.common.time.provider.TimeProvider
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TimeProviderFake: TimeProvider {
    override fun timeNow(): LocalDateTime {
        return LocalDateTime.now()
    }

    override fun dateNow(): LocalDate {
        return LocalDate.parse("2018-12-12")
    }

    override fun zoneId(): ZoneId {
        return ZoneId.systemDefault()
    }
}