package com.qubacy.geoqq.data._common.repository._common.source.local.database.error

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.LocalErrorDataSourceDao
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.model.ErrorEntity
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.model.toError
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class LocalErrorDataSourceTest {
    companion object {
        val TEST_ERROR = ErrorEntity(0, "en", "test error", false)
    }

    private lateinit var mLocalErrorDataSource: LocalErrorDataSource

    @Before
    fun setup() {
        initDataSource()
    }

    private fun initDataSource(getErrorByIdResult: ErrorEntity = TEST_ERROR) {
        val localErrorDataSourceDaoMock = Mockito.mock(LocalErrorDataSourceDao::class.java)

        Mockito.`when`(localErrorDataSourceDaoMock.getErrorById(
            Mockito.anyLong(), Mockito.anyString()
        )).thenReturn(getErrorByIdResult)

        mLocalErrorDataSource = LocalErrorDataSource(localErrorDataSourceDaoMock)
    }

    @Test
    fun getErrorTest() {
        val expectedErrorEntity = TEST_ERROR
        val expectedError = expectedErrorEntity.toError()

        initDataSource(getErrorByIdResult = expectedErrorEntity)

        val gottenError = mLocalErrorDataSource.getError(expectedErrorEntity.id)

        Assert.assertEquals(expectedError, gottenError)
    }
}