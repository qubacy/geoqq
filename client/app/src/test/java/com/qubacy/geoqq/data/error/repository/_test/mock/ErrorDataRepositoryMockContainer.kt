package com.qubacy.geoqq.data.error.repository._test.mock

import com.qubacy.geoqq._common.error.Error
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import org.mockito.Mockito
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class ErrorDataRepositoryMockContainer {
    val errorDataRepositoryMock: ErrorDataRepository

    var getError: Error? = null

    var mGetErrorCallFlag = false
    val getErrorCallFlag get() = mGetErrorCallFlag

    init {
        errorDataRepositoryMock = mockErrorDataRepository()
    }

    private fun mockErrorDataRepository(): ErrorDataRepository {
        val errorDataRepositoryMock = Mockito.mock(ErrorDataRepository::class.java)

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