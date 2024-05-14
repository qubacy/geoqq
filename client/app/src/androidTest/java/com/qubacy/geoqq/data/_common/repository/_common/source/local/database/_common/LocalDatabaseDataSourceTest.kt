package com.qubacy.geoqq.data._common.repository._common.source.local.database._common

import androidx.annotation.CallSuper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before

abstract class LocalDatabaseDataSourceTest {
    protected lateinit var mDatabase: Database

    @Before
    @CallSuper
    open fun setup() {
        mDatabase = TestDatabase.getDatabase(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
    }

    @After
    open fun clear() {
        mDatabase.clearAllTables()
    }
}