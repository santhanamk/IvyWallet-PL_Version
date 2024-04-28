package com.ivy.common.androidtest

import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class DummyTest: IvyAndroidTest() {
    @Test
    fun dummyTest() {
        println(db.accountCacheDao())
        assert(true)
    }
}