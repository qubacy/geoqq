package com.qubacy.geoqq.domain.logout.usecase._test.mock

import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.logout.usecase.impl.LogoutUseCaseImpl
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mockito.Mockito

class LogoutUseCaseMockContainer {
    val logoutUseCaseMock: LogoutUseCaseImpl

    val resultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()

    private var mLogoutCallFlag = false
    val logoutCallFlag get() = mLogoutCallFlag

    init {
        logoutUseCaseMock = mockLogoutUseCase()
    }

    fun clear() {
        mLogoutCallFlag = false
    }

    private fun mockLogoutUseCase(): LogoutUseCaseImpl {
        val logoutUseCaseMock = Mockito.mock(LogoutUseCaseImpl::class.java)

        Mockito.`when`(logoutUseCaseMock.resultFlow).thenReturn(resultFlow)
        Mockito.`when`(logoutUseCaseMock.logout()).thenAnswer {
            mLogoutCallFlag = true

            Unit
        }

        return logoutUseCaseMock
    }
}