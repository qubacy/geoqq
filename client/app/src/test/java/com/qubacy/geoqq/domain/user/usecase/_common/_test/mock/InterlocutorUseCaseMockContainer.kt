package com.qubacy.geoqq.domain.user.usecase._common._test.mock

import com.qubacy.geoqq.domain._common._test.context.UseCaseTestContext
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.user.usecase._common.InterlocutorUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mockito.Mockito

class InterlocutorUseCaseMockContainer {
    companion object {
        val DEFAULT_USER = UseCaseTestContext.DEFAULT_USER
    }

    val interlocutorUseCaseMock: InterlocutorUseCase

    val resultFlow: MutableSharedFlow<DomainResult> = MutableSharedFlow()

    private var mGetInterlocutorCallFlag = false
    val getInterlocutorCallFlag get() = mGetInterlocutorCallFlag

    init {
        interlocutorUseCaseMock = mockInterlocutorUseCase()
    }

    fun clear() {
        mGetInterlocutorCallFlag = false
    }

    private fun mockInterlocutorUseCase(): InterlocutorUseCase {
        val interlocutorUseCaseMock = Mockito.mock(InterlocutorUseCase::class.java)

        Mockito.`when`(interlocutorUseCaseMock.resultFlow).thenReturn(resultFlow)
        Mockito.`when`(interlocutorUseCaseMock.getInterlocutor(Mockito.anyLong())).thenAnswer {
            mGetInterlocutorCallFlag = true

            Unit
        }

        return interlocutorUseCaseMock
    }
}