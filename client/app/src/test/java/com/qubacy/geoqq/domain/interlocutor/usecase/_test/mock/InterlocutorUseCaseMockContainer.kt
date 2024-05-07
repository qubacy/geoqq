package com.qubacy.geoqq.domain.interlocutor.usecase._test.mock

import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import org.mockito.Mockito

class InterlocutorUseCaseMockContainer {
    companion object {
        val DEFAULT_USER = UserDataRepositoryMockContainer.DEFAULT_DATA_USER.toUser()
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