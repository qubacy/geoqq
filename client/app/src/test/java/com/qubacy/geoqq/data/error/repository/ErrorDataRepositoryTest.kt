package com.qubacy.geoqq.data.error.repository

import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.LocalErrorDataSourceDao
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.model.ErrorEntity
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.model.toError
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class ErrorDataRepositoryTest : DataRepositoryTest<ErrorDataRepository>() {
    companion object {
        val TEST_ERROR = ErrorEntity(0, "en", "test error", false)
    }

    @Before
    fun setup() {
        initRepository()
    }

    private fun initRepository(getErrorByIdResult: ErrorEntity = TEST_ERROR) {
        val localErrorDataSourceMock = Mockito.mock(LocalErrorDataSourceDao::class.java)

        Mockito.`when`(localErrorDataSourceMock.getErrorById(Mockito.anyLong(), Mockito.anyString()))
            .thenReturn(getErrorByIdResult)

        mDataRepository = ErrorDataRepository(localErrorDataSourceMock)
    }

    @Test
    fun getErrorTest() {
        val expectedErrorEntity = TEST_ERROR
        val expectedError = expectedErrorEntity.toError()

        initRepository(getErrorByIdResult = expectedErrorEntity)

        val gottenError = mDataRepository.getError(expectedErrorEntity.id)

        Assert.assertEquals(expectedError, gottenError)
    }
}