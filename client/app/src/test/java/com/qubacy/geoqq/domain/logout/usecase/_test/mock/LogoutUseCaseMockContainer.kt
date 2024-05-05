package com.qubacy.geoqq.domain.logout.usecase._test.mock

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mockito.Mockito

class LogoutUseCaseMockContainer {
    val logoutUseCaseMock: LogoutUseCase

    val resultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()

    private var mLogoutCallFlag = false
    val logoutCallFlag get() = mLogoutCallFlag

    init {
        logoutUseCaseMock = mockLogoutUseCase()
    }

    fun clear() {
        mLogoutCallFlag = false
    }

    private fun mockLogoutUseCase(): LogoutUseCase {
        val logoutUseCaseMock = Mockito.mock(LogoutUseCase::class.java)

        Mockito.`when`(logoutUseCaseMock.resultFlow).thenReturn(resultFlow)
        Mockito.`when`(logoutUseCaseMock.logout()).thenAnswer {
            mLogoutCallFlag = true

            Unit
        }

        return logoutUseCaseMock
    }
}