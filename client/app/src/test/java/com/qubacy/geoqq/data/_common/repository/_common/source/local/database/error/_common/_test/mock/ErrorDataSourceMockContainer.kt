package com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import org.mockito.Mockito

class ErrorDataSourceMockContainer {
    val errorDataSourceMock: LocalErrorDatabaseDataSource

    var getError: Error? = null

    private var mGetErrorCallFlag = false
    val getErrorCallFlag get() = mGetErrorCallFlag

    init {
        errorDataSourceMock = mockErrorDataSource()
    }

    private fun mockErrorDataSource(): LocalErrorDatabaseDataSource {
        val errorDataRepositoryMock = Mockito.mock(LocalErrorDatabaseDataSource::class.java)

        Mockito.`when`(errorDataRepositoryMock.getError(Mockito.anyLong())).thenAnswer {
            mGetErrorCallFlag = true
            getError
        }

        return errorDataRepositoryMock
    }

    fun reset() {
        getError = null

        mGetErrorCallFlag = false
    }
}