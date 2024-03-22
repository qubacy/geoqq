package com.qubacy.geoqq.data._common.repository.source_common.local.database

import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.data._common.repository._common.source.local._common.database.Database
import com.qubacy.geoqq.data._common.repository.source_common.local.database.storage.TestDatabase
import org.junit.After
import org.junit.Before

abstract class LocalDatabaseDataSourceTest {
    protected lateinit var mDatabase: Database

    @Before
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