package com.qubacy.geoqq.data._common.repository.source_common.local.database

import androidx.annotation.CallSuper
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data._common.repository.source_common.local.database.storage.TestDatabase
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