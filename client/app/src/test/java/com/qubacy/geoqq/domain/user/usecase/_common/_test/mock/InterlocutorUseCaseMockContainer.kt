package com.qubacy.geoqq.domain.user.usecase._common._test.mock

import com.qubacy.geoqq.domain._common._test.context.UseCaseTestContext
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.user.usecase._common.UserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mockito.Mockito

class InterlocutorUseCaseMockContainer {
    companion object {
        val DEFAULT_USER = UseCaseTestContext.DEFAULT_USER
    }

    val interlocutorUseCaseMock: UserUseCase

    val resultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()

    private var mGetInterlocutorCallFlag = false
    val getInterlocutorCallFlag get() = mGetInterlocutorCallFlag

    init {
        interlocutorUseCaseMock = mockInterlocutorUseCase()
    }

    fun clear() {
        mGetInterlocutorCallFlag = false
    }

    private fun mockInterlocutorUseCase(): UserUseCase {
        val interlocutorUseCaseMock = Mockito.mock(UserUseCase::class.java)

        Mockito.`when`(interlocutorUseCaseMock.resultFlow).thenReturn(resultFlow)
        Mockito.`when`(interlocutorUseCaseMock.getUser(Mockito.anyLong())).thenAnswer {
            mGetInterlocutorCallFlag = true

            Unit
        }

        return interlocutorUseCaseMock
    }
}