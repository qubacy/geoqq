package com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao

import android.content.ContentValues
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common._test.insertable.LocalInsertableDatabaseDataSourceTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.model.ErrorEntity
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.context.LocalErrorDatabaseDataSourceTestContext
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalErrorDatabaseDataSourceDaoTest(

) : LocalDatabaseDataSourceTest(), LocalInsertableDatabaseDataSourceTest<ErrorEntity> {
    companion object {
        val DEFAULT_ERROR_ENTITY = LocalErrorDatabaseDataSourceTestContext.DEFAULT_ERROR_ENTITY
    }

    private lateinit var mLocalErrorDatabaseDataSourceDao: LocalErrorDataSourceDao

    @Before
    override fun setup() {
        super.setup()

        mLocalErrorDatabaseDataSourceDao = mDatabase.errorDao()
    }

    override fun packEntityContent(itemEntity: ErrorEntity): ContentValues {
        val contentValues = ContentValues().apply {
            put(ErrorEntity.ID_PROP_NAME, itemEntity.id)
            put(ErrorEntity.LANG_PROP_NAME, itemEntity.lang)
            put(ErrorEntity.MESSAGE_PROP_NAME, itemEntity.message)
            put(ErrorEntity.IS_CRITICAL_PROP_NAME, itemEntity.isCritical)
        }

        return contentValues
    }

    @Test
    fun getErrorByIdTest() {
        val expectedErrorEntity = DEFAULT_ERROR_ENTITY

        insertItems(mDatabase, ErrorEntity.TABLE_NAME, listOf(expectedErrorEntity))

        val gottenError = mLocalErrorDatabaseDataSourceDao.getErrorById(
            expectedErrorEntity.id, expectedErrorEntity.lang)

        Assert.assertEquals(expectedErrorEntity, gottenError)
    }
}